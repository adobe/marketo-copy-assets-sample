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

@Data
@Builder
public class BrowseSnippetsRequest {

    private String status;
    private Integer maxReturn;
    private Integer offset;
}
