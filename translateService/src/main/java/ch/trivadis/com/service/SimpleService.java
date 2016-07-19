package ch.trivadis.com.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Created by Andy Moncsek on 19.07.16.
 */
public class SimpleService extends AbstractVerticle {
    private Translate translate;

    public void start(Future<Void> startFuture) throws Exception {

        HttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

        translate = new Translate.Builder(HTTP_TRANSPORT, JSON_FACTORY, null).build();


        final Router router = Router.router(vertx);

        // we use a blocking handler here, since goolge API is blocking
        router.get("/translate/:lang/:message").blockingHandler(context -> {
            final String lang = context.request().getParam("lang");
            final String message = context.request().getParam("message");
            TranslationsListResponse response = getTranslationsListResponse(translate, lang, message);
            Optional.ofNullable(response).ifPresent(resp -> resp.
                    getTranslations().
                    stream().
                    map(TranslationsResource::getTranslatedText).
                    reduce((left, right) -> left.concat(right)).
                    ifPresent(translatedText ->
                            context.response().end(translatedText)
                    ));


        });

        listen(startFuture, router);

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

    private TranslationsListResponse getTranslationsListResponse(Translate translate, String lang, String message) {
        try {
            return translate.translations()
                    .list(Arrays.asList(message), lang)
                    .setKey(config().getString("apikey", "")) //
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(SimpleService.class.getName(), new DeploymentOptions().setInstances(2));
    }
}
