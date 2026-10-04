package com.tech.wixblog.content.validation;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.media.config.MediaProperties;
import com.tech.wixblog.media.domain.MediaAsset;
import com.tech.wixblog.media.domain.MediaScope;
import com.tech.wixblog.media.service.MediaService;
import com.tech.wixblog.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Validates the images embedded in an Editor.js story document.
 * <p>
 * {@code Story.content} is an opaque JSON string produced by Editor.js. Because the
 * backend does not render it, the only way a story body can reference an image is by
 * carrying a URL, and those URLs must be checked, otherwise two things go wrong:
 * <ul>
 *     <li>Any authenticated user could embed an image URL belonging to someone else,
 *         including an unpublished inline image from a draft they cannot read.</li>
 *     <li>An image that has since been deleted would keep rendering from a browser
 *         cache while being permanently broken for new readers.</li>
 * </ul>
 * Both are prevented by resolving every referenced key against the ledger and
 * requiring it to be live and owned by the story's author.
 * <p>
 * A single batched lookup covers the whole document, so validating a story with twenty
 * inline images still costs one extra query rather than twenty.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InlineImageContentValidator {

    /**
     * Editor.js image block shape: {@code {"type":"image","data":{"file":{"url":...}}}}.
     * The bare-string form {@code "file":"..."} is also emitted by older Editor.js
     * builds, so both are recognised.
     */
    private static final String FILE_FIELD = "file";
    private static final String URL_FIELD = "url";

    private final ObjectMapper objectMapper;
    private final MediaService mediaService;
    private final MediaProperties mediaProperties;

    /**
     * @param content the raw {@code Story.content} value; may be null or blank
     * @param author  the user who will own the story
     * @throws BusinessRuleException if the document is unparseable, or references an
     *                               image that is missing, retired, of the wrong
     *                               scope, or owned by somebody else
     */
    public void validate (String content, User author) {
        if (!StringUtils.hasText(content)) {
            return;
        }
        JsonNode root = parse(content);
        Set<String> referencedUrls = collectMediaReferences(root);

        if (referencedUrls.isEmpty()) {
            return;
        }

        UUID ownerId = author.getId();
        List<MediaAsset> resolved = mediaService.resolveOwnedReferences(
                new ArrayList<>(referencedUrls),
                MediaScope.INLINE_IMAGE,
                ownerId
                                         );

        Set<String> resolvedUrls = new LinkedHashSet<>();
        for (MediaAsset asset : resolved) {
            resolvedUrls.add(asset.getPublicUrl());
        }

        List<String> rejected = referencedUrls.stream()
                                              .filter(url -> isManaged(url))
                                              .filter(url -> !resolvedUrls.contains(url))
                                              .toList();

        if (!rejected.isEmpty()) {
            throw new BusinessRuleException(
                    "One or more images in this story are unavailable or do not belong to you: %s"
                            .formatted(String.join(", ", truncate(rejected)))
            );
        }
    }

    /**
     * Parses the document, converting a malformed payload into a business-rule error
     * rather than leaking a Jackson exception as a 500.
     */
    private JsonNode parse (String content) {
        try {
            return objectMapper.readTree(content);
        } catch (JacksonException exception) {
            log.debug("Rejected malformed story content", exception);
            throw new BusinessRuleException(
                    "Story content must be a valid Editor.js document."
            );
        }
    }

    /**
     * Walks the whole tree collecting every URL that points at this application's media
     * store. A full traversal rather than a fixed path walk, so that an image nested in
     * a list, a quote or a custom block is still found.
     */
    private Set<String> collectMediaReferences (JsonNode node) {
        Set<String> urls = new LinkedHashSet<>();
        walk(node, urls);
        return urls;
    }

    private void walk (JsonNode node, Set<String> sink) {
        if (node == null) {
            return;
        }
        if (node.isObject()) {
            // Jackson 3 renamed JsonNode.fields() to properties(), and it returns a Set
            // of entries rather than a bare iterator.
            for (Map.Entry<String, JsonNode> entry : node.properties()) {
                String fieldName = entry.getKey();
                JsonNode value = entry.getValue();
                if ((URL_FIELD.equals(fieldName) || FILE_FIELD.equals(fieldName))
                        && value.isString()) {
                    String candidate = value.asString().trim();
                    if (isManaged(candidate)) {
                        sink.add(candidate);
                    }
                }
                walk(value, sink);
            }
        } else if (node.isArray()) {
            node.forEach(child -> walk(child, sink));
        }
    }

    /**
     * True when the URL addresses this application's own media store. External images
     * are left alone here and governed by {@code app.media.allow-external-urls}.
     */
    private boolean isManaged (String url) {
        if (!StringUtils.hasText(url)) {
            return false;
        }
        String marker = mediaProperties.publicPath() + "/";
        return url.contains(marker);
    }

    private List<String> truncate (List<String> values) {
        int limit = 5;
        if (values.size() <= limit) {
            return values;
        }
        List<String> head = new ArrayList<>(values.subList(0, limit));
        head.add("and " + (values.size() - limit) + " more");
        return head;
    }
}