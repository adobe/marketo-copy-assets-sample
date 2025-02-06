/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.client;

import com.adobe.mkto.assetcopy.exception.MktoRestClientRuntimeException;
import com.adobe.mkto.assetcopy.model.RestResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class MarketoRestAPIClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarketoRestAPIClient.class);

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public RestResponse get(String uri, Map<String, Object> queryParams, Object... uriVars) {
        return sendRequest(uri, HttpMethod.GET, uriVars, queryParams, null, null);
    }

    public <T> RestResponse get(String uri, T queryParamsObject, Object... uriVars) {
        return sendRequest(uri, HttpMethod.GET, uriVars, convertToMap(queryParamsObject), null, null);
    }

    public <T> RestResponse post(String uri, T requestBodyObject, Object... uriVars) {
        return sendRequest(uri, HttpMethod.POST, uriVars, null, convertToMultiValueMap(requestBodyObject), null);
    }

    public <T> RestResponse post(String uri, T requestBodyObject, MediaType mediaType, Object... uriVars) {
        Object bodyParams = MediaType.MULTIPART_FORM_DATA.equals(mediaType)
                ? convertToMultiValueMap(requestBodyObject)
                : convertToMap(requestBodyObject);
        return sendRequest(uri, HttpMethod.POST, uriVars, null, bodyParams, mediaType);
    }

    public RestResponse post(String uri, MultiValueMap<String, Object> requestBody, Object... uriVars) {
        return sendRequest(uri, HttpMethod.POST, uriVars, null, requestBody, MediaType.MULTIPART_FORM_DATA);
    }

    public RestResponse postWithJsonInput(String uri, Object requestBody) {
        String jsonBody = convertToJsonInput(requestBody);
        return sendRequest(uri, HttpMethod.POST, null, null, jsonBody, MediaType.APPLICATION_JSON);
    }

    public byte[] getResponseAsByteArray(String uri, Integer id) {
        try {
            return Objects.requireNonNull(RestClient.create()
                    .get()
                    .uri(uri, id)
                    .retrieve()
                    .body(Resource.class)).getContentAsByteArray();
        } catch (IOException e) {
            throw new MktoRestClientRuntimeException("unable to get content", e);
        }
    }

    public void validateResponse(RestResponse response) {
        if (response == null || !response.isSuccess()) {
            throw new MktoRestClientRuntimeException(String.format("Response is null or has errors: %s", response));
        }
    }

    public <T> List<T> getResultFromResponse(RestResponse response, Class<T> clazz) {
        validateResponse(response);
        if (!CollectionUtils.isEmpty(response.getWarnings())) {
            LOGGER.warn("Response has warnings: {}", response);
        }
        return objectMapper.convertValue(response.getResult(), objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
    }

    private RestResponse sendRequest(String uri, HttpMethod httpMethod, Object[] uriVars, Map<String, Object> queryParams,
                                     Object bodyParams, MediaType mediaType) {

        RestClient.RequestBodySpec requestBodySpec = restClient.method(httpMethod)
                .uri(uriBuilder -> {
                    UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uriBuilder.path(uri).build(uriVars));
                    if (!CollectionUtils.isEmpty(queryParams)) {
                        queryParams.forEach(builder::queryParam);
                    }
                    return builder.build().toUri();
                });

        if (!Objects.isNull(mediaType)) {
            requestBodySpec.contentType(mediaType);
        }

        if (isValidBodyParams(bodyParams)) {
            requestBodySpec.body(bodyParams);
        }

        return requestBodySpec.retrieve().body(RestResponse.class);
    }

    private <T> Map<String, Object> convertToMap(T paramObject) {
        return objectMapper.convertValue(paramObject, new TypeReference<>() {});
    }

    private <T> MultiValueMap<String, Object> convertToMultiValueMap(T request) {
        MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();
        requestBody.setAll(convertToMap(request));
        return requestBody;
    }

    private <T> String convertToJsonInput(T request) {
        try {
            return objectMapper.writeValueAsString(Map.of("input", request));
        } catch (JsonProcessingException e) {
            throw new MktoRestClientRuntimeException(e);
        }
    }

    private boolean isValidBodyParams(Object bodyParams) {
        if (bodyParams instanceof String stringParams) {
            return !stringParams.isEmpty();
        }
        else if (bodyParams instanceof Map<?,?> mapParams) {
            return !CollectionUtils.isEmpty(mapParams);
        }
        return false;
    }
}
