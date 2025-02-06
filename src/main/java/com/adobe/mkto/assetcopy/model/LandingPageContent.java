/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model;

import lombok.Data;

import java.util.Map;

@Data
public class LandingPageContent {

    private Object content;
    private String followupType;
    private String followupValue;
    private Map<String, Object> formattingOptions;
    private Object id;
    private Integer index;
    private String type;
}
