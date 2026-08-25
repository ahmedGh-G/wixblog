package com.tech.wixblog.content.dto;

import java.util.UUID;

public record CategoryDetailsResponse(

    UUID id,

    String name,

    String slug,

    long storyCount

) {}