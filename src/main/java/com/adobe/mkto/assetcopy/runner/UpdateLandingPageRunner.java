/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.runner;

import com.adobe.mkto.assetcopy.client.APIClientHelper;
import com.adobe.mkto.assetcopy.client.LandingPageAPIClient;
import com.adobe.mkto.assetcopy.constant.Subscription;
import com.adobe.mkto.assetcopy.model.LandingPage;
import com.adobe.mkto.assetcopy.model.request.BrowseRequest;
import com.adobe.mkto.assetcopy.model.request.UpdateLandingPageRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Profile("update-lp")
public class UpdateLandingPageRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateLandingPageRunner.class);

    private final LandingPageAPIClient sourceLandingPageAPIClient;
    private final LandingPageAPIClient destinationLandingPageAPIClient;

    @Override
    public void run(String... args) {

        Map<String, LandingPage> sourcePagesByName = buildPagesByName("approved", Subscription.SOURCE);
        Map<String, LandingPage> destinationPagesByName = buildPagesByName("draft", Subscription.DESTINATION);
//        Map<String, LandingPage> sourcePagesByName = getPageById(30413, Subscription.SOURCE);
//        Map<String, LandingPage> destinationPagesByName = getPageById(1646, Subscription.DESTINATION);

        destinationPagesByName.values()
                .forEach(destinationPage -> {
                    String sourcePageKey = destinationPage.getName()
                            .substring(destinationPage.getName().indexOf(".") + 1);
                    LandingPage sourcePage = sourcePagesByName.getOrDefault(sourcePageKey, null);

                    if (sourcePage == null) {
                        LOGGER.debug("Unable to locate source page for {} with id {}", destinationPage.getName(), destinationPage.getId());
                    }
                    else if (StringUtils.hasLength(sourcePage.getTitle()) || StringUtils.hasLength(sourcePage.getKeywords())) {

                        UpdateLandingPageRequest updateRequest = UpdateLandingPageRequest.builder()
                                .title(sourcePage.getTitle())
                                .keywords(sourcePage.getKeywords())
                                .build();

                        destinationLandingPageAPIClient.updateMetadata(destinationPage.getId(), updateRequest);
                    }
                });
    }

//    private Map<String, LandingPage> getPageById(Integer id, Subscription subscription) {
//
//        LandingPage page = landingPageClient.getById(id, subscription);
//        return Map.of(page.getName(), page);
//    }

    private Map<String, LandingPage> buildPagesByName(String status, Subscription subscription)  {

        BrowseRequest browseRequest = BrowseRequest.builder()
                .status(status)
                .maxReturn(200)
                .offset(0)
                .build();

        LandingPageAPIClient landingPageAPIClient = subscription == Subscription.DESTINATION
                ? destinationLandingPageAPIClient
                : sourceLandingPageAPIClient;

        List<LandingPage> pages = APIClientHelper.browseAll(browseRequest, landingPageAPIClient::browse);

        return pages.stream()
                .collect(Collectors.toMap(LandingPage::getName, Function.identity()));
    }
}
