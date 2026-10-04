package com.tech.wixblog.media.validation;

import com.tech.wixblog.media.config.MediaProperties;
import com.tech.wixblog.media.domain.MediaAsset;
import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.domain.MediaStorageType;
import com.tech.wixblog.media.repository.MediaAssetRepository;
import com.tech.wixblog.user.domain.Role;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.domain.UserStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorFactory;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.stereotype.Component;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Behavioural tests for {@link @MediaReference}, plus an explicit assertion that the
 * validator is a Spring bean.
 * <p>
 * The bean assertion matters: a {@code ConstraintValidator} that is not resolvable by
 * Spring is silently ignored by {@code LocalValidatorFactoryBean}, so the annotation
 * would stop being enforced and the fields would quietly accept anything. That is
 * precisely the failure mode this feature cannot afford.
 */
class MediaReferenceConstraintTest {

    private ValidatorFactory validatorFactory;
    private Validator validator;
    private MediaAssetRepository mediaAssetRepository;

    private final String managedUrl =
            "http://localhost:8080/api/v1/media/covers/2026/10/abc.png";

    @BeforeEach
    void setUp () {
        mediaAssetRepository = mock(MediaAssetRepository.class);

        MediaProperties properties = new MediaProperties(
                MediaStorageType.LOCAL,
                "./target/test-media",
                "/media",
                "http://localhost:8080/api/v1",
                false,
                true,
                Duration.ofDays(30),
                Map.of(),
                null
                                  );

        MediaReferenceValidator delegate =
                new MediaReferenceValidator(mediaAssetRepository, properties);

        validatorFactory = Validation.byDefaultProvider()
                                     .configure()
                                     .constraintValidatorFactory(
                                             new SingleValidatorFactory(
                                                     MediaReferenceValidator.class,
                                                     delegate
                                                             ))
                                     .buildValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterEach
    void tearDown () {
        validatorFactory.close();
    }

    record Holder(
            @MediaReference(scope = MediaScope.STORY_COVER) String coverImageUrl
    ) {}

    @Test
    @DisplayName("is a Spring bean, so LocalValidatorFactoryBean will resolve and apply it")
    void validatorIsDiscoverable () {
        assertThat(MediaReferenceValidator.class.getAnnotation(Component.class))
                .as("MediaReferenceValidator must be a @Component or Spring will not inject "
                        + "its repository dependency and the constraint will not run")
                .isNotNull();
    }

    @Test
    @DisplayName("accepts a null reference, leaving requiredness to @NotNull")
    void acceptsNull () {
        assertThat(validator.validate(new Holder(null))).isEmpty();
    }

    @Test
    @DisplayName("accepts a blank reference as absent")
    void acceptsBlank () {
        assertThat(validator.validate(new Holder("   "))).isEmpty();
    }

    @Test
    @DisplayName("accepts a reference to a live asset of the right scope")
    void acceptsValidReference () {
        when(mediaAssetRepository.findByPublicUrl(anyString()))
                .thenReturn(Optional.of(asset(MediaScope.STORY_COVER)));

        assertThat(validator.validate(new Holder(managedUrl))).isEmpty();
    }

    @Test
    @DisplayName("rejects an absolute third-party URL when external references are disabled")
    void rejectsExternalUrl () {
        Set<ConstraintViolation<Holder>> violations =
                validator.validate(new Holder("https://example.com/photo.png"));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("uploaded through this application");
    }

    @Test
    @DisplayName("rejects an unknown value that carries no media path at all")
    void rejectsGarbage () {
        Set<ConstraintViolation<Holder>> violations =
                validator.validate(new Holder("not-a-url-at-all"));

        assertThat(violations).hasSize(1);
    }

    @Test
    @DisplayName("rejects an asset belonging to the wrong scope")
    void rejectsWrongScope () {
        when(mediaAssetRepository.findByPublicUrl(anyString()))
                .thenReturn(Optional.of(asset(MediaScope.INLINE_IMAGE)));

        Set<ConstraintViolation<Holder>> violations = validator.validate(new Holder(managedUrl));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("story cover");
    }

    @Test
    @DisplayName("rejects a retired asset")
    void rejectsDeletedAsset () {
        MediaAsset deleted = asset(MediaScope.STORY_COVER);
        deleted.markDeleted();
        when(mediaAssetRepository.findByPublicUrl(anyString()))
                .thenReturn(Optional.of(deleted));

        Set<ConstraintViolation<Holder>> violations = validator.validate(new Holder(managedUrl));

        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage())
                .contains("has been removed");
    }

    /**
     * Supplies the collaborator-injected validator instance, mirroring how Spring
     * substitutes its own bean during validation.
     */
    private record SingleValidatorFactory(
            Class<? extends ConstraintValidator<?, ?>> target,
            ConstraintValidator<?, ?> instance
    ) implements ConstraintValidatorFactory {

        @Override
        @SuppressWarnings("unchecked")
        public <T extends ConstraintValidator<?, ?>> T getInstance (Class<T> key) {
            if (target.equals(key)) {
                return (T) instance;
            }
            return null;
        }

        @Override
        public void releaseInstance (ConstraintValidator<?, ?> instance) {
            // Nothing to release: the single instance is reused for the whole test.
        }
    }

    private MediaAsset asset (MediaScope scope) {
        User owner = new User("a@b.com", "tester", "hash", Role.USER, UserStatus.ACTIVE);
        ReflectionTestUtils.setField(owner, "id", UUID.randomUUID());
        return new MediaAsset(
                "covers/2026/10/abc.png",
                managedUrl,
                scope,
                owner,
                "a.png",
                "image/png",
                10L, 10, 10
                            );
    }
}