/*
Copyright 2025 Adobe
All Rights Reserved.

NOTICE: Adobe permits you to use, modify, and distribute this file in
accordance with the terms of the Adobe license agreement accompanying
it.
*/
package com.adobe.mkto.assetcopy.runner;

import com.adobe.mkto.assetcopy.model.LandingPage;
import com.adobe.mkto.assetcopy.model.LandingPageCopyDestinationData;
import com.adobe.mkto.assetcopy.service.CopyLandingPageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Profile("lp-copy")
@RequiredArgsConstructor
public class LandingPageCopyRunner implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(LandingPageCopyRunner.class);

    private final Map<String, LandingPageCopyDestinationData> landingPageCopyDestinations;
    private final Map<String, List<LandingPage>> landingPagesByWorkspace;
    private final CopyLandingPageService copyLandingPageService;

    @Override
    public void run(String... args)  {

        AtomicInteger copiedPages = new AtomicInteger(0);

        landingPageCopyDestinations.values().forEach(
                destinationData -> {
                    landingPagesByWorkspace.get(destinationData.getWorkspaceName()).forEach(
                            landingPage -> {
                                try {
                                    copyLandingPageService.copyLandingPage(landingPage,
                                            destinationData.getLocalAsset().getLandingPageFolder(),
                                            destinationData.getLocalAsset().getFormFolder(),
                                            destinationData.getGlobalAsset().getImageAndFileFolder(),
                                            true, destinationData.getUrlFormat());
                                    copiedPages.getAndIncrement();
                                }
                                catch (Exception e) {
                                    LOGGER.error("Error copying LP with lpId: {} and name: {}",
                                            landingPage.getId(), landingPage.getName(), e);
                                    try {
                                        copyLandingPageService.discardLandingPage(landingPage, true);
                                    }
                                    catch (Exception ee) {
                                        LOGGER.warn("LP was not created or unable to discard draft of partially copied LP in destination", ee);
                                    }
                                }
                            });
                    LOGGER.debug("Successfully copied {} landing pages to destination workspace {}",
                            copiedPages.get(), destinationData.getWorkspaceName());
                });
    }
}
