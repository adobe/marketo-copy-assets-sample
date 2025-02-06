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
public class FormattingOptions {

    private Boolean array;
    private Boolean bigDecimal;
    private Boolean bigInteger;
    private Boolean binary;
    private Boolean aBoolean;
    private Boolean containerNode;
    private Boolean aDouble;
    private Boolean aFloat;
    private Boolean floatingPointNumber;
    private Boolean aInt;
    private Boolean integralNumber;
    private Boolean aLong;
    private Boolean missingNode;
    private String nodeType;
    private Boolean aNull;
    private Boolean number;
    private Boolean object;
    private Boolean pojo;
    private Boolean aShort;
    private Boolean textual;
    private Boolean valueNode;
}
