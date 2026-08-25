package com.tech.wixblog.content.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.UUID;

public record StorySearchRequest(

    String q,

    UUID categoryId,

    String tag,

    @Min(0)
    Integer page,

    @Min(1)
    @Max(100)
    Integer size,

     String sort

) {

    public StorySearchRequest {

        if (page == null) {
            page = 0;
        }

        if (size == null) {
            size = 20;
        }

        if (q != null) {
            q = q.trim();
        }

        if (tag != null) {
            tag = tag.trim().toLowerCase();
        }
        if (sort == null) {
            sort = "latest";
        }

        sort = sort.toLowerCase();
    }
}