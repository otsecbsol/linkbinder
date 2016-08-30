/*
 * Copyright 2016 OPEN TONE Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.co.opentone.bsol.framework.core.google.vision;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Google Cloud Vision APIを操作するクラス.
 * @author opentone
 */
public class GoogleVisionApiClient {

    private GoogleVisionApiConfiguration config;
    private List<GoogleVisionApiRequestFile> requestFiles = Lists.newArrayList();

    public GoogleVisionApiClient(GoogleVisionApiConfiguration config) {
        this.config = config;
    }

    public void addRequestFile(GoogleVisionApiRequestFile requestFile) {
        requestFiles.add(requestFile);
    }

    public GoogleVisionApiResponse execute() throws GoogleVisionApiException {
        try {
            Vision vision = createVision();
            Vision.Images.Annotate annotate = null;

            annotate = vision.images().annotate(createRequest());
            annotate.setDisableGZipContent(true);
            BatchAnnotateImagesResponse batchResponse = annotate.execute();

            return handleResponse(batchResponse);
        } catch (IOException |GeneralSecurityException e) {
            throw new GoogleVisionApiException(e);
        }
    }

    private GoogleVisionApiResponse handleResponse(BatchAnnotateImagesResponse batchResponse) {
        GoogleVisionApiResponse result = new GoogleVisionApiResponse();
        for (int i = 0; i < requestFiles.size(); i++) {
            GoogleVisionApiRequestFile file = requestFiles.get(i);
            AnnotateImageResponse annotateImageResponse = batchResponse.getResponses().get(i);
            List<EntityAnnotation> entityAnnotations =
                    MoreObjects.firstNonNull(
                            annotateImageResponse.getTextAnnotations(),
                            ImmutableList.<EntityAnnotation>of());

            entityAnnotations.stream()
                    .findFirst()
                    .ifPresent(a -> {
                        result.addDescription(file.getFileId(), a.getDescription());
                    });
        }
        return result;
    }

    private BatchAnnotateImagesRequest createRequest() {
        ImmutableList.Builder<AnnotateImageRequest> requests = ImmutableList.builder();
        requestFiles.forEach(f -> {
            requests.add(
                    new AnnotateImageRequest()
                            .setImage(new Image().encodeContent(f.getContent()))
                            .setFeatures(ImmutableList.of(
                                    new Feature()
                                            .setType("TEXT_DETECTION")
                                            .setMaxResults(config.getMaxResult())
                            ))
            );
        });

        return new BatchAnnotateImagesRequest().setRequests(requests.build());
    }

    private Vision createVision() throws IOException, GeneralSecurityException {
        GoogleCredential credential = GoogleCredential.fromStream(
                Files.newInputStream(Paths.get(config.getAccountFilePath())))
                .createScoped(VisionScopes.all());
        Vision vision = new Vision.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName(config.getApplicationName())
                .build();

        return vision;
    }

    public boolean isReady() {
        return config.isUse() && !requestFiles.isEmpty();
    }
}
