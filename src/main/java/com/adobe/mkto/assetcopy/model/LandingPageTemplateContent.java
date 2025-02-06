/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model;

import lombok.Data;

@Data
public class LandingPageTemplateContent {

    private String content;
    private Boolean enableMunchkin;
    private Integer id;
    private String status;
    private String templateType;
}
