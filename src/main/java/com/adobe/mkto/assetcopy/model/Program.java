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
public class Program {

    private String channel;
    private List<ProgramCost> costs;
    private String createdAt;
    private String description;
    private String endDate;
    private FolderId folder;
    private Integer id;
    private String name;
    private String sfdcId;
    private String sfdcName;
    private String startDate;
    private String status;
    private List<Tag> tags;
    private String type;
    private String updatedAt;
    private String url;
    private String workspace;
}
