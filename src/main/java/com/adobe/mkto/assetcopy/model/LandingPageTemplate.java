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
public class LandingPageTemplate {

    private String createdAt;
    private String description;
    private Boolean enableMunchkin;
    private FolderId folder;
    private Integer id;
    private String name;
    private String status;
    private String templateType;
    private String updatedAt;
    private String url;
    private String workspace;
}
