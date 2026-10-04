package com.tech.wixblog.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Guards against two {@code @ExceptionHandler} methods claiming the same exception
 * type.
 * <p>
 * Spring resolves this at startup: {@code ExceptionHandlerMethodResolver} throws
 * {@code IllegalStateException: Ambiguous @ExceptionHandler method mapped for [...]}
 * while building the {@code handlerExceptionResolver} bean, and the application
 * refuses to start. That failure was invisible to the test suite, because
 * {@code @SpringBootTest} in its default mock web environment never eagerly builds
 * that bean, so {@code contextLoads} passed on an application that could not boot.
 * Asserting the mapping directly catches the mistake without needing a server.
 */
class GlobalExceptionHandlerMappingTest {

    @Test
    @DisplayName("no exception type is handled by more than one method")
    void noAmbiguousExceptionMappings () {
        Map<Class<?>, List<String>> handlersByException = new LinkedHashMap<>();

        for (Method method : GlobalExceptionHandler.class.getDeclaredMethods()) {
            ExceptionHandler annotation =
                    AnnotatedElementUtils.findMergedAnnotation(method, ExceptionHandler.class);
            if (annotation == null) {
                continue;
            }
            for (Class<? extends Throwable> exceptionType : annotation.value()) {
                handlersByException
                        .computeIfAbsent(exceptionType, key -> new ArrayList<>())
                        .add(method.getName());
            }
        }

        Map<Class<?>, List<String>> conflicts =
                handlersByException.entrySet()
                                  .stream()
                                  .filter(entry -> entry.getValue().size() > 1)
                                  .collect(LinkedHashMap::new,
                                           (map, entry) -> map.put(entry.getKey(),
                                                   entry.getValue()),
                                           LinkedHashMap::putAll);

        assertThat(conflicts)
                .as("each exception type must be handled by exactly one method, otherwise "
                        + "the application context fails to start")
                .isEmpty();
    }

    @Test
    @DisplayName("registers handlers at all, so the assertions above are not vacuous")
    void registersHandlers () {
        long handlerCount = java.util.Arrays
                .stream(GlobalExceptionHandler.class.getDeclaredMethods())
                .filter(method ->
                        AnnotatedElementUtils.findMergedAnnotation(
                                method, ExceptionHandler.class) != null)
                .count();

        assertThat(handlerCount)
                .as("expected the media and resource exception handlers to be present")
                .isGreaterThanOrEqualTo(15);
    }

    @Test
    @DisplayName("keeps the exception types that the media feature depends on")
    void keepsMediaExceptionTypes () {
        List<Class<? extends Throwable>> handled =
                new ArrayList<>(handledExceptionTypes());

        assertThat(handled)
                .contains(
                        ResourceNotFoundException.class,
                        BusinessRuleException.class,
                        UnsupportedMediaException.class,
                        PayloadTooLargeException.class,
                        InvalidRequestException.class
                            );
    }

    /**
     * Cross-check against the status codes the API contract promises, so a future
     * refactor cannot quietly downgrade a documented status.
     */
    @Test
    @DisplayName("exposes one handler per exception type with no duplicates")
    void handledTypesAreDistinct () {
        assertThat(new TreeSet<>(handledExceptionTypes().stream()
                                                           .map(Class::getName)
                                                           .toList()))
                .hasSameSizeAs(handledExceptionTypes());
    }

    private List<Class<? extends Throwable>> handledExceptionTypes () {
        List<Class<? extends Throwable>> types = new ArrayList<>();
        for (Method method : GlobalExceptionHandler.class.getDeclaredMethods()) {
            ExceptionHandler annotation =
                    AnnotatedElementUtils.findMergedAnnotation(method, ExceptionHandler.class);
            if (annotation != null) {
                types.addAll(List.of(annotation.value()));
            }
        }
        return types;
    }
}