/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.model.request;

import com.adobe.mkto.assetcopy.model.FolderId;
import com.adobe.mkto.assetcopy.model.ProgramCost;
import com.adobe.mkto.assetcopy.model.Tag;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CreateProgramRequest {

    private String channel;
    private List<ProgramCost> costs;
    private String description;
    private FolderId folder;
    private String name;
    private List<Tag> tags;
    private String type;
}
