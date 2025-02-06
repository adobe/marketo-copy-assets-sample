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

import java.util.List;

@Data
public class ThankYouPage {

    @JsonProperty("default")
    private Boolean isDefault;
    private String followupType;
    private Object followupValue;
    private String operator;
    private String subjectField;
    private List<String> values;
}
