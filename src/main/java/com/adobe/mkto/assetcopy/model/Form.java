/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model;

import lombok.Data;

import java.util.List;

@Data
public class Form {

    private Integer id;
    private String name;
    private String buttonLabel;
    private Integer buttonLocation;
    private String createdAt;
    private String description;
    private FolderId folder;
    private String fontFamily;
    private String fontSize;
    private KnownVisitor knownVisitor;
    private String labelPosition;
    private String language;
    private String locale;
    private Boolean progressiveProfiling;
    private String status;
    private List<ThankYouPage> thankYouList;
    private String theme;
    private String updatedAt;
    private String url;
    private String waitingLabel;
}
