package ch.trivadis.com.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.model.*;
import com.google.api.services.vision.v1.model.Image;
import com.google.common.collect.ImmutableList;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by Andy Moncsek on 19.07.16.
 */
public class SimpleService extends AbstractVerticle {
    private static final String APPLICATION_NAME = "Trivadis-VisionFaceDetectSample/1.0";
    private static final int MAX_RESULTS = 30;
    private Vision vision;

    public void start(Future<Void> startFuture) throws Exception {

        vision = getVisionService();

        final Router router = Router.router(vertx);

        router.route().handler(BodyHandler.create().setUploadsDirectory("uploads"));

        router.post("/").blockingHandler(context -> handleImagePost(context));

        router.get("/").handler(context -> context.response().end(handleMainPage("https://media.giphy.com/media/I4tKfKcuynb4Q/giphy.gif").toString()));

        router.get("/images/:img").handler(context -> handleImageRequest(context));

        listen(startFuture, router);

    }

    private void handleImagePost(RoutingContext context) {
        final Set<FileUpload> uploads = context.fileUploads();
        final Optional<FileUpload> first = uploads.stream().findFirst();
        first.ifPresent(img -> {
            createImagesAndDetectDaces(img);
            context.response().end(handleMainPage("/images/out.jpg").toString());
        });
        if (!first.isPresent()) context.response().end(handleMainPage("https://media.giphy.com/media/I4tKfKcuynb4Q/giphy.gif").toString());
    }

    private void handleImageRequest(RoutingContext context) {
        final File image = new File("uploads/" + context.request().getParam("img"));
        if (image.exists()) {
            context.response().sendFile(image.getAbsolutePath());
            image.delete();
        } else {
            context.response().end();
        }
    }

    private void createImagesAndDetectDaces(FileUpload img) {
        final File inputFile = new File(img.uploadedFileName());
        final Path input = new File("uploads/input.jpg").toPath();
        try {
            if (input.toFile().exists()) input.toFile().delete();
            Files.move(inputFile.toPath(), input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final File out = new File("uploads/out.jpg");
        try {
            final List<FaceAnnotation> faceAnnotations = detectFaces(input, MAX_RESULTS);
            writeWithFaces(input, out.toPath(), faceAnnotations);
            clearFiles(inputFile, input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clearFiles(File inputFile, Path input) {
        if (input.toFile().exists()) input.toFile().delete();
        if (inputFile.exists()) inputFile.delete();
    }

    /**
     * Connects to the Vision API using Application Default Credentials.
     */
    public Vision getVisionService() throws IOException, GeneralSecurityException {
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        return new Vision.Builder(GoogleNetHttpTransport.newTrustedTransport(), jsonFactory, null)//credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Gets up to {@code maxResults} faces for an image stored at {@code path}.
     */
    public List<FaceAnnotation> detectFaces(Path path, int maxResults) throws IOException {
        byte[] data = Files.readAllBytes(path);

        AnnotateImageRequest request =
                new AnnotateImageRequest()
                        .setImage(new Image().encodeContent(data))
                        .setFeatures(ImmutableList.of(
                                new Feature()
                                        .setType("FACE_DETECTION")
                                        .setMaxResults(maxResults)));
        Vision.Images.Annotate annotate =
                vision.images()
                        .annotate(new BatchAnnotateImagesRequest().setRequests(ImmutableList.of(request)));
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotate.setDisableGZipContent(true);
        annotate.setKey(config().getString("apikey", ""));

        BatchAnnotateImagesResponse batchResponse = annotate.execute();
        assert batchResponse.getResponses().size() == 1;
        AnnotateImageResponse response = batchResponse.getResponses().get(0);
        if (response.getFaceAnnotations() == null) {
            throw new IOException(
                    response.getError() != null
                            ? response.getError().getMessage()
                            : "Unknown error getting image annotations");
        }
        return response.getFaceAnnotations();
    }

    /**
     * Reads image {@code inputPath} and writes {@code outputPath} with {@code faces} outlined.
     */
    private void writeWithFaces(Path inputPath, Path outputPath, List<FaceAnnotation> faces)
            throws IOException {
        BufferedImage img = ImageIO.read(inputPath.toFile());
        annotateWithFaces(img, faces);
        ImageIO.write(img, "jpg", outputPath.toFile());
    }

    /**
     * Annotates an image {@code img} with a polygon around each face in {@code faces}.
     */
    public void annotateWithFaces(BufferedImage img, List<FaceAnnotation> faces) {
        for (FaceAnnotation face : faces) {
            annotateWithFace(img, face);
        }
    }

    /**
     * Annotates an image {@code img} with a polygon defined by {@code face}.
     */
    private void annotateWithFace(BufferedImage img, FaceAnnotation face) {
        Graphics2D gfx = img.createGraphics();
        Polygon poly = new Polygon();
        for (Vertex vertex : face.getFdBoundingPoly().getVertices()) {
            poly.addPoint(vertex.getX(), vertex.getY());
        }
        gfx.setStroke(new BasicStroke(5));
        gfx.setColor(new Color(0x00ff00));
        gfx.draw(poly);
    }


    private void listen(Future<Void> startFuture, Router router) {
        HttpServer server = vertx.
                createHttpServer(new HttpServerOptions().setHost(config().getString("host", "0.0.0.0")).setPort(config().getInteger("port", 8080)));

        server.requestHandler(router::accept).listen(status -> {
            if (status.succeeded()) {
                startFuture.complete();
                System.out.println("deployment of translate instance:" + this + " done");
            } else {
                startFuture.fail(status.cause());
            }

        });
    }

    private StringBuilder handleMainPage(String img) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><head><title> Face Detection example</title></head><body>");

        // upload
        builder.append("<hr>");
        imageFileUpload(builder);
        builder.append("</hr>");

        // show
        builder.append("<hr>");
        showImage(builder, img);
        builder.append("</hr>");


        builder.append("</body></html>");
        return builder;
    }


    private void imageFileUpload(StringBuilder builder) {
        builder.append("<p>");
        builder.append("<h2> upload an image containing one or more faces</h2> \n");
        builder.append("<p>");
        builder.append("<form action=\"/\" method=\"post\" enctype=\"multipart/form-data\">\n");

        builder.append("<input name=\"Datei\" type=\"file\" size=\"50\" accept=\"*.jpg\" />");
        builder.append("<input type=\"submit\" value=\"Submit\" />\n");
        builder.append("</form>");
        builder.append("</p>");
        builder.append("</p>");
    }


    private void showImage(StringBuilder builder, String img) {
        builder.append("<p>");
        builder.append("<h2> the resulting image</h2> \n");

        builder.append("<img src=\"" + img + "\" alt=\"your face\" style=\"width:800px;height:600px;\">");
        builder.append("</p>");
    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(SimpleService.class.getName(), new DeploymentOptions().setInstances(2));
    }
}
