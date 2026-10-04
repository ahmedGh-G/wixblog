package com.tech.wixblog.media.validation;

import com.tech.wixblog.media.domain.MediaScope;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Asserts that a {@code String} field holding an image reference points at an image
 * this application actually issued, of the expected {@link MediaScope}.
 * <p>
 * Rejects, with a specific message:
 * <ul>
 *     <li>values that are not a resolvable media key, so a typo fails at validation
 *         rather than producing a broken image in production;</li>
 *     <li>a key that belongs to a different scope, so an inline image cannot be
 *         smuggled in as an avatar or vice versa;</li>
 *     <li>a key whose asset has been retired, so a deleted image cannot be
 *         re-attached;</li>
 *     <li>absolute URLs, unless explicitly allowed by configuration.</li>
 * </ul>
 *
 * <p><b>Scope of this check.</b> Ownership is deliberately <em>not</em> verified here,
 * because a bean-validation constraint has no access to the authenticated principal
 * and guessing at it would be fragile. Ownership is enforced separately by
 * {@code MediaService.resolveOwnedReference} at the point of use. Both checks must
 * pass.
 */
@Documented
@Constraint(validatedBy = MediaReferenceValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface MediaReference {

    String message () default "Must reference an image uploaded for this purpose.";

    Class<?>[] groups () default {};

    Class<? extends Payload>[] payload () default {};

    /**
     * The scope the referenced image must belong to.
     */
    MediaScope scope ();

    /**
     * Overrides {@code app.media.allow-external-urls} for this field. Useful where a
     * legacy record may still hold a hotlinked URL.
     */
    boolean allowExternal () default false;
}