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
import java.util.Map;

@Data
public class FormField {
    private Integer blankFields;
    private Integer columnNumber;
    private String dataType;
    private String defaultValue;
    private Map<String, Object> fieldMetaData;
    private Integer fieldWidth;
    private List<String> fields;
    private Boolean formPrefill;
    private Boolean isSensitive;
    private String hintText;
    private String id;
    private String instructions;
    private String label;
    private Integer labelWidth;
    private Integer maxLength;
    private Boolean required;
    private Integer rowNumber;
    private String text;
    private Object validationMessage;
    private FormFieldVisibilityRuleResponse visibilityRules;
    private AutoFill autoFill;
}
