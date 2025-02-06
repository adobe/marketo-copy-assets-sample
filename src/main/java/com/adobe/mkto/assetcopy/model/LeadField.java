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
public class LeadField {

    private String displayName;
    private String name;
    private String description;
    private String dataType;
    private Integer length;
    private Boolean isHidden;
    private Boolean isHtmlEncodingInEmail;
    private Boolean isSensitive;
    private Boolean isCustom;
    private Boolean isApiCreated;
    private String status;
}
