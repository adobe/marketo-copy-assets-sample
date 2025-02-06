/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.constant;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum ContentType {
    RICH_TEXT("RichText"),
    IMAGE("Image"),
    RECTANGLE("Rectangle"),
    FORM("Form"),
    HTML("HTML"),
    SNIPPET("Snippet");

    @JsonValue
    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    public static ContentType fromValue(String value) {
        return Arrays.stream(ContentType.values())
                .filter(type -> type.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow();
    }
}
