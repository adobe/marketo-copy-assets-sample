/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class AddFormFieldRequest {
    private Integer blankFields;
    private String defaultValue;
    private String fieldType;
    private String fieldId;
    private Integer fieldWidth;
    private Boolean formPrefill;
    private Boolean isSensitive;
    private String hintText;
    private Boolean initiallyChecked;
    private String instructions;
    private String label;
    private Boolean labelToRight;
    private Integer labelWidth;
    private String maskInput;
    private Integer maxLength;
    private Float maxValue;
    private Float minValue;
    private Boolean multiSelect;
    private Boolean required;
    private String validationMessage;
    private String values;
    private Integer visibleLines;
}
