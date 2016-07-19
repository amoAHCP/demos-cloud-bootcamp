package ch.trivadis.com.service;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.ext.web.Router;

/**
 * Created by Andy Moncsek on 19.07.16.
 */
public class SimpleService extends AbstractVerticle {

    public void start(Future<Void> startFuture) throws Exception {
        final Router router = Router.router(vertx);

        router.get("/hallo/:name").handler(context -> {
           context.response().end("Hello "+context.request().getParam("name"));
        });

        HttpServer server = vertx.
                createHttpServer(new HttpServerOptions().setHost(config().getString("host","0.0.0.0")).setPort(config().getInteger("port",8080)));

        server.requestHandler(router::accept).listen(status -> {
            if (status.succeeded()) {
                startFuture.complete();
                System.out.println("deployment of instance:"+ this+ " done");
            } else {
                startFuture.fail(status.cause());
            }

        });

    }

    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(SimpleService.class.getName(), new DeploymentOptions().setInstances(2));
    }
}
