package com.tech.wixblog.media.service;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.media.domain.MediaAsset;
import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.dto.MediaUploadResponse;
import com.tech.wixblog.media.repository.MediaAssetRepository;
import com.tech.wixblog.media.validation.ImageFormat;
import com.tech.wixblog.media.validation.ImageValidator;
import com.tech.wixblog.media.support.MediaTestFixtures;
import com.tech.wixblog.user.domain.User;
import com.tech.wixblog.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MediaServiceTest {

    @Mock
    private MediaStorage mediaStorage;
    @Mock
    private MediaAssetRepository mediaAssetRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ImageValidator imageValidator;

    private MediaService mediaService;

    private final UUID ownerId = UUID.randomUUID();

    @BeforeEach
    void setUp () {
        mediaService = new MediaService(
                mediaStorage,
                mediaAssetRepository,
                userRepository,
                imageValidator,
                MediaTestFixtures.defaultProperties()
                             );

        when(userRepository.findById(ownerId))
                .thenReturn(Optional.of(user(ownerId)));
    }

    @Test
    @DisplayName("stores the file under a server-generated key that never contains the client filename")
    void generatesKeyWithoutClientFilename () throws IOException {
        when(imageValidator.validate(any(), any()))
                .thenReturn(new ImageValidator.ValidatedImage(
                        new byte[]{1, 2, 3},
                        ImageFormat.PNG,
                        "image/png",
                        "png",
                        10, 10, false
                                  ));
        when(mediaAssetRepository.saveAndFlush(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        MultipartFile file = new MockMultipartFile(
                "file", "../../etc/passwd.png", "image/png", new byte[]{1});

        MediaUploadResponse response = mediaService.store(file, MediaScope.STORY_COVER, ownerId);

        assertThat(response.key())
                .startsWith("covers/")
                .endsWith(".png")
                .doesNotContain("passwd")
                .doesNotContain("..");
        assertThat(response.url()).endsWith(response.key());
        assertThat(response.scope()).isEqualTo(MediaScope.STORY_COVER);
        verify(mediaStorage).store(anyString(), any());
    }

    @Test
    @DisplayName("records the sanitised original filename for display only")
    void sanitisesOriginalFilename () throws IOException {
        when(imageValidator.validate(any(), any()))
                .thenReturn(validated());
        when(mediaAssetRepository.saveAndFlush(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mediaService.store(
                new MockMultipartFile("file", "my holiday/photo.png", "image/png", new byte[]{1}),
                MediaScope.AVATAR,
                ownerId
                             );

        ArgumentCaptor<MediaAsset> captor = ArgumentCaptor.forClass(MediaAsset.class);
        verify(mediaAssetRepository).saveAndFlush(captor.capture());
        assertThat(captor.getValue().getOriginalFilename()).isEqualTo("photo.png");
    }

    @Test
    @DisplayName("removes the file again when persisting the ledger row fails")
    void compensatesWhenPersistFails () throws IOException {
        when(imageValidator.validate(any(), any()))
                .thenReturn(validated());
        /*
         * saveAndFlush, not save. This is the regression guard for the shipped defect:
         * with save() the INSERT was merely queued, so this failure mode never occurred
         * inside the try block and the compensation below was unreachable. Flushing
         * forces the constraint evaluation to happen where the catch can see it.
         */
        when(mediaAssetRepository.saveAndFlush(any()))
                .thenThrow(new IllegalStateException("constraint violated"));

        assertThatThrownBy(() -> mediaService.store(
                new MockMultipartFile("file", "a.png", "image/png", new byte[]{1}),
                MediaScope.STORY_COVER,
                ownerId
                                         ))
                .isInstanceOf(IllegalStateException.class);

        verify(mediaStorage).delete(anyString());
    }

    @Test
    @DisplayName("compiles the INSERT inside the try, so a flush failure can be compensated")
    void flushesSoFailuresAreCompensatable () throws IOException {
        when(imageValidator.validate(any(), any()))
                .thenReturn(validated());
        when(mediaAssetRepository.saveAndFlush(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        mediaService.store(
                new MockMultipartFile("file", "a.png", "image/png", new byte[]{1}),
                MediaScope.STORY_COVER,
                ownerId
                             );

        verify(mediaAssetRepository).saveAndFlush(any());
        // A deferred save() would leave the INSERT pending past the try block.
        verify(mediaAssetRepository, never()).save(any());
    }

    @Test
    @DisplayName("rejects a reference belonging to a different user")
    void rejectsForeignReference () {
        String url = "http://localhost:8080/api/v1/media/covers/2026/10/abc.png";
        when(mediaAssetRepository.findOwnedActiveByPublicUrl(url, MediaScope.STORY_COVER, ownerId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> mediaService.resolveOwnedReference(
                url, MediaScope.STORY_COVER, ownerId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("does not belong to you");
    }

    @Test
    @DisplayName("rejects an absolute URL when external references are disabled")
    void rejectsExternalUrlWhenDisabled () {
        assertThatThrownBy(() -> mediaService.resolveOwnedReference(
                "https://example.com/photo.png", MediaScope.STORY_COVER, ownerId))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessageContaining("uploaded through this application");
    }

    @Test
    @DisplayName("treats a blank reference as absent rather than invalid")
    void treatsBlankAsAbsent () {
        assertThat(mediaService.resolveOwnedReference("   ", MediaScope.AVATAR, ownerId))
                .isEmpty();
        assertThat(mediaService.resolveOwnedReference(null, MediaScope.AVATAR, ownerId))
                .isEmpty();
        verify(mediaAssetRepository, never()).findOwnedActiveByPublicUrl(anyString(), any(), any());
    }

    @Test
    @DisplayName("resolves an owned, live reference")
    void resolvesOwnedReference () {
        String url = "http://localhost:8080/api/v1/media/avatars/2026/10/abc.png";
        MediaAsset asset = ownedAsset(url, MediaScope.AVATAR);
        when(mediaAssetRepository.findOwnedActiveByPublicUrl(url, MediaScope.AVATAR, ownerId))
                .thenReturn(Optional.of(asset));

        assertThat(mediaService.resolveOwnedReference(url, MediaScope.AVATAR, ownerId))
                .contains(asset);
    }

    @Test
    @DisplayName("retires and deletes the previous avatar when it is replaced")
    void retiresReplacedAvatar () throws IOException {
        String oldUrl = "http://localhost:8080/api/v1/media/avatars/2026/10/old.png";
        MediaAsset old = ownedAsset(oldUrl, MediaScope.AVATAR);
        when(mediaAssetRepository.findOwnedActiveByPublicUrl(oldUrl, MediaScope.AVATAR, ownerId))
                .thenReturn(Optional.of(old));

        mediaService.retireReplacedReference(oldUrl, ownerId);

        assertThat(old.isDeleted()).isTrue();
        verify(mediaStorage).delete(old.getStorageKey());
    }

    @Test
    @DisplayName("does not attempt to delete an externally hosted reference")
    void ignoresExternalReferenceOnRetire () throws IOException {
        mediaService.retireReplacedReference("https://example.com/a.png", ownerId);

        verify(mediaStorage, never()).delete(anyString());
    }

    private ImageValidator.ValidatedImage validated () {
        return new ImageValidator.ValidatedImage(
                new byte[]{1, 2, 3},
                ImageFormat.PNG,
                "image/png",
                "png",
                10, 10, false
                                  );
    }

    private MediaAsset ownedAsset (String url, MediaScope scope) {
        return new MediaAsset(
                "avatars/2026/10/abc.png",
                url,
                scope,
                user(ownerId),
                "a.png",
                "image/png",
                3L, 10, 10
                            );
    }

    private User user (UUID id) {
        User user = new User("a@b.com", "tester", "hash",
                com.tech.wixblog.user.domain.Role.USER,
                com.tech.wixblog.user.domain.UserStatus.ACTIVE
                            );
        org.springframework.test.util.ReflectionTestUtils.setField(user, "id", id);
        return user;
    }
}