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
public class RestResponse {

    private List<RestResponseError> errors;
    private String requestId;
    private Object result;
    private boolean success;
    private List<String> warnings;
}
