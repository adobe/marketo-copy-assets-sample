/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.constant;

import com.adobe.mkto.assetcopy.model.FolderId;

public class AssetCopyConstants {
    private AssetCopyConstants() {}

    public static final String QUERY_PARAM_STATUS = "status";
    public static final String APPROVED = "approved";
    public static final String FOLDER = "folder";
    public static final String ID = "id";
    public static final FolderId MARKETING_ACTIVITIES_ROOT_FOLDER = new FolderId(3, FolderType.FOLDER);
    public static final FolderId DESIGN_STUDIO_ROOT_FOLDER = new FolderId(2, FolderType.FOLDER);
}
