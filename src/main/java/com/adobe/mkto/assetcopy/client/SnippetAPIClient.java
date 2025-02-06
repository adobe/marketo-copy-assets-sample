/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.client;

import com.adobe.mkto.assetcopy.model.RestResponse;
import com.adobe.mkto.assetcopy.model.Snippet;
import com.adobe.mkto.assetcopy.model.SnippetContent;
import com.adobe.mkto.assetcopy.model.request.BrowseRequest;
import com.adobe.mkto.assetcopy.model.request.CreateSnippetRequest;
import com.adobe.mkto.assetcopy.model.request.UpdateSnippetContentRequest;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.adobe.mkto.assetcopy.constant.AssetCopyConstants.QUERY_PARAM_STATUS;

public class SnippetAPIClient {

    private static final String GET_SNIPPET = "/rest/asset/v1/snippet/{id}.json";
    private static final String GET_SNIPPET_CONTENT = "/rest/asset/v1/snippet/{id}/content.json";
    private static final String SNIPPETS = "/rest/asset/v1/snippets.json";
    private static final String UPDATE_SNIPPET_CONTENT = "/rest/asset/v1/snippet/{id}/content.json";
    private static final String APPROVE_SNIPPET = "/rest/asset/v1/snippet/{id}/approveDraft.json";

    private final MarketoRestAPIClient marketoRestAPIClient;
    //Snippet API does not have a get by name, so we build a map of snippet name -> snippet using browse endpoint
    private final Map<String, Snippet> snippetsByName;

    public SnippetAPIClient(MarketoRestAPIClient marketoRestAPIClient) {
        this.marketoRestAPIClient = marketoRestAPIClient;
        this.snippetsByName = initializeSnippetsByName();
    }

    public Snippet getByName(String name) {
        return snippetsByName != null ? snippetsByName.get(name) : null;
    }
    
    public List<Snippet> getById(Integer id, String status)  {
        RestResponse response = marketoRestAPIClient.get(GET_SNIPPET,
                status != null ? Map.of(QUERY_PARAM_STATUS, status) : null, id);
        return marketoRestAPIClient.getResultFromResponse(response, Snippet.class);
    }

    public List<Snippet> browse(BrowseRequest requestBody)  {
        RestResponse response = marketoRestAPIClient.get(SNIPPETS, requestBody);
        return marketoRestAPIClient.getResultFromResponse(response, Snippet.class);
    }

    public SnippetContent getContentById(Integer id, String status)  {
        RestResponse response = marketoRestAPIClient.get(GET_SNIPPET_CONTENT,
                status != null ? Map.of(QUERY_PARAM_STATUS, status) : null, id);
        return marketoRestAPIClient.getResultFromResponse(response, SnippetContent.class).getFirst();
    }

    public void updateSnippetContent(Integer id, UpdateSnippetContentRequest requestBody)  {
        RestResponse response = marketoRestAPIClient.post(UPDATE_SNIPPET_CONTENT, requestBody, id);
        marketoRestAPIClient.validateResponse(response);
    }

    public void approveSnippet(Integer id)  {
        RestResponse response = marketoRestAPIClient.post(APPROVE_SNIPPET, null, id);
        marketoRestAPIClient.validateResponse(response);
    }

    public Snippet createSnippet(CreateSnippetRequest requestBody)  {
        RestResponse response = marketoRestAPIClient.post(SNIPPETS, requestBody);
        return marketoRestAPIClient.getResultFromResponse(response, Snippet.class).getFirst();
    }

    private Map<String, Snippet> initializeSnippetsByName()  {

        List<Snippet> snippets = APIClientHelper.browseAll(new BrowseRequest(), this::browse);

        return snippets
                .stream()
                .collect(Collectors.toMap(Snippet::getName, Function.identity()));
    }
}
