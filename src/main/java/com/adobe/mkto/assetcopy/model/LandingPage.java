/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LandingPage {

    @JsonProperty("URL")
    private String url;
    private String computedUrl;
    private String createdAt;
    private String customHeadHtml;
    private String description;
    private String facebookOgTags;
    private FolderId folder;
    private Boolean formPrefill;
    private Integer id;
    private String keywords;
    private Boolean mobileEnabled;
    private String name;
    private String robots;
    private String status;
    private Integer template;
    private String title;
    private String updatedAt;
    private String workspace;
    private Integer programId;
}
