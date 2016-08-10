package jp.co.opentone.bsol.framework.core.google.vision;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by t-aoyagi on 2016/08/04.
 */
public class GoogleVisionApiClientTest {

    @Test
    public void testTextDetection() throws Exception {

        // 認証情報
        GoogleCredential credential = GoogleCredential.fromStream(
                    Files.newInputStream(Paths.get("/tmp/google-cloud-api-service-account.json")))
                    .createScoped(VisionScopes.all());
//        GoogleCredential credential = GoogleCredential.getApplicationDefault().createScoped(VisionScopes.all());

        // Google Cloud Vision APIクライアント
        Vision vision = new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(), credential)
                .setApplicationName("GoogleVisionApiClient/1.0")
                .build();

        // 解析対象画像ファイルごとに処理を行う
        ImmutableList<String> imageFiles = ImmutableList.<String>of(
                "/tmp/sample1.jpg",
                "/tmp/sample2.jpg",
                "/tmp/sample3.jpg",
                "/tmp/sample4.jpg"
                );
        ImmutableList.Builder<AnnotateImageRequest> requests = ImmutableList.builder();
        imageFiles.forEach(f -> {
            byte[] data = new byte[0];
            try {
                data = Files.readAllBytes(Paths.get(f));
                requests.add(
                        new AnnotateImageRequest()
                                .setImage(new Image().encodeContent(data))
                                .setFeatures(ImmutableList.of(
                                        new Feature()
                                                .setType("TEXT_DETECTION")
                                                .setMaxResults(1)
                                ))
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // 実行
        Vision.Images.Annotate annotate =
                vision.images().annotate(new BatchAnnotateImagesRequest().setRequests(requests.build()));
        annotate.setDisableGZipContent(true);
        BatchAnnotateImagesResponse batchResponse = annotate.execute();

        batchResponse.getResponses().forEach(response -> {
            List<EntityAnnotation> entityAnnotations =
                    MoreObjects.firstNonNull(response.getTextAnnotations(), ImmutableList.<EntityAnnotation>of());
            entityAnnotations.stream()
                    .findFirst()
                    .ifPresent(a -> {
                        System.out.println(a.getDescription());
                        System.out.println("----------------------------------------------------");
                    });
        });
    }
}