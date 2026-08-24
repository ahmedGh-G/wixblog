package com.tech.wixblog.content.validation;

import com.tech.wixblog.common.exception.BusinessRuleException;
import com.tech.wixblog.content.domain.Story;
import org.springframework.stereotype.Component;

@Component
public class StoryPublicationValidator {

    public void validate(
            Story story
                        ) {

        if (isBlank(story.getTitle())) {

            throw new BusinessRuleException(
                    "A story must have a title before publishing."
            );
        }

        if (isBlank(story.getContent())) {

            throw new BusinessRuleException(
                    "A story must have content before publishing."
            );
        }

        if (story.getCategory() == null) {

            throw new BusinessRuleException(
                    "A category is required before publishing."
            );
        }
    }
    private boolean isBlank(
        String value
    ) {

        return value == null ||
               value.trim().isEmpty();
    }
}