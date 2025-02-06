/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.constant;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public enum FolderType {

    @JsonAlias("Program")
    @JsonProperty("program")
    PROGRAM,
    @JsonAlias("Folder")
    @JsonProperty("folder")
    FOLDER;
}
