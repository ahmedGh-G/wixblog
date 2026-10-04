package com.tech.wixblog.content.validation;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.media.domain.MediaAsset;
import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.service.MediaService;
import com.tech.wixblog.media.support.MediaTestFixtures;
import com.tech.wixblog.user.domain.Role;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InlineImageContentValidatorTest {

    private static final String INLINE_URL =
            "http://localhost:8080/api/v1/media/inline/2026/10/aaa.png";

    @Mock
    private MediaService mediaService;

    private InlineImageContentValidator validator;

    private final UUID authorId = UUID.randomUUID();

    @BeforeEach
    void setUp () {
        validator = new InlineImageContentValidator(
                new ObjectMapper(),
                mediaService,
                MediaTestFixtures.defaultProperties()
                            );
    }

    @Test
    @DisplayName("accepts a document whose inline image is owned and live")
    void acceptsOwnedImage () {
        when(mediaService.resolveOwnedReferences(anyList(), eq(MediaScope.INLINE_IMAGE), any()))
                .thenReturn(List.of(asset(INLINE_URL)));

        assertThatCode(() -> validator.validate(documentWith(INLINE_URL), author()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("rejects a document embedding an image the author does not own")
    void rejectsForeignImage () {
        when(mediaService.resolveOwnedReferences(anyList(), eq(MediaScope.INLINE_IMAGE), any()))
                .thenReturn(List.of());

        assertThatThrownBy(() -> validator.validate(documentWith(INLINE_URL), author()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("do not belong to you");
    }

    @Test
    @DisplayName("rejects a document whose image has been deleted since upload")
    void rejectsDeletedImage () {
        // A retired asset simply does not come back from the ledger lookup.
        when(mediaService.resolveOwnedReferences(anyList(), eq(MediaScope.INLINE_IMAGE), any()))
                .thenReturn(List.of());

        assertThatThrownBy(() -> validator.validate(documentWith(INLINE_URL), author()))
                .isInstanceOf(BusinessRuleException.class);
    }

    @Test
    @DisplayName("rejects malformed content rather than surfacing a 500")
    void rejectsMalformedContent () {
        assertThatThrownBy(() -> validator.validate("{ this is not json", author()))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("valid Editor.js document");
    }

    @Test
    @DisplayName("accepts blank content, which is legal for a draft")
    void acceptsBlankContent () {
        assertThatCode(() -> validator.validate(null, author())).doesNotThrowAnyException();
        assertThatCode(() -> validator.validate("   ", author())).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("accepts a document with no images at all without touching the ledger")
    void acceptsDocumentWithoutImages () {
        assertThatCode(() -> validator.validate(
                "{\"time\":1,\"blocks\":[{\"type\":\"paragraph\",\"data\":{\"text\":\"hi\"}}]}",
                author()
                         ))
                .doesNotThrowAnyException();

        org.mockito.Mockito.verify(mediaService, org.mockito.Mockito.never())
                .resolveOwnedReferences(anyList(), any(), any());
    }

    @Test
    @DisplayName("finds images nested inside list and quote blocks, not just top-level images")
    void findsNestedImages () {
        String content = """
                {
                  "time": 1,
                  "blocks": [
                    {"type":"list","data":{"items":[{"image":{"file":{"url":"%s"}}}]}},
                    {"type":"quote","data":{"text":"x","caption":"y","file":{"url":"%s"}}}
                  ],
                  "version": "2.30.7"
                }
                """.formatted(INLINE_URL, INLINE_URL);

        when(mediaService.resolveOwnedReferences(anyList(), eq(MediaScope.INLINE_IMAGE), any()))
                .thenReturn(List.of(asset(INLINE_URL)));

        assertThatCode(() -> validator.validate(content, author()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("recognises the bare-string file form emitted by older Editor.js builds")
    void recognisesBareStringFileForm () {
        String content = """
                {"blocks":[{"type":"image","data":{"file":"%s","width":10,"height":10}}]}
                """.formatted(INLINE_URL);

        when(mediaService.resolveOwnedReferences(anyList(), eq(MediaScope.INLINE_IMAGE), any()))
                .thenReturn(List.of(asset(INLINE_URL)));

        assertThatCode(() -> validator.validate(content, author()))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("leaves externally hosted images alone")
    void ignoresExternalImages () {
        String content = """
                {"blocks":[{"type":"image","data":{"file":{"url":"https://example.com/a.png"}}}]}
                """;

        assertThatCode(() -> validator.validate(content, author()))
                .doesNotThrowAnyException();

        org.mockito.Mockito.verify(mediaService, org.mockito.Mockito.never())
                .resolveOwnedReferences(anyList(), any(), any());
    }

    private String documentWith (String url) {
        return """
                {
                  "time": 1,
                  "blocks": [
                    {"type":"paragraph","data":{"text":"before"}},
                    {"type":"image","data":{"file":{"url":"%s"},"width":10,"height":10,"caption":""}}
                  ],
                  "version": "2.30.7"
                }
                """.formatted(url);
    }

    private MediaAsset asset (String url) {
        return new MediaAsset(
                "inline/2026/10/aaa.png",
                url,
                MediaScope.INLINE_IMAGE,
                author(),
                "a.png",
                "image/png",
                100L, 10, 10
                            );
    }

    private User author () {
        User user = new User("a@b.com", "tester", "hash", Role.USER, UserStatus.ACTIVE);
        ReflectionTestUtils.setField(user, "id", authorId);
        return user;
    }
}