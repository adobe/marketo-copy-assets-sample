/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.client;

import com.adobe.mkto.assetcopy.model.request.BrowseRequest;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public final class APIClientHelper {

    private APIClientHelper() {}

    private static final int MAX_RETURN = 200;

    public static <T> List<T> browseAll(BrowseRequest browseRequest, Function<BrowseRequest, List<T>> browseRequestHandler) {

        browseRequest.setOffset(0);
        browseRequest.setMaxReturn(MAX_RETURN);

        List<T> allResults = new ArrayList<>();
        List<T> currentResults = browseRequestHandler.apply(browseRequest);

        while (!CollectionUtils.isEmpty(currentResults)) {

            allResults.addAll(currentResults);
            browseRequest.setOffset(browseRequest.getOffset() + MAX_RETURN);
            currentResults = browseRequestHandler.apply(browseRequest);
        }

        return allResults;
    }
}
