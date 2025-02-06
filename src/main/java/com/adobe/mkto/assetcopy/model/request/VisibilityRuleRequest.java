/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model.request;

import com.adobe.mkto.assetcopy.model.PickList;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VisibilityRuleRequest {

    private String altLabel;
    private String operator;
    private List<PickList> pickListValues;
    private String subjectField;
    private List<String> values;
}
