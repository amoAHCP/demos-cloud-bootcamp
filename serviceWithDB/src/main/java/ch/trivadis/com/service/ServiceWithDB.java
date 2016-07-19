package ch.trivadis.com.service;

import ch.trivadis.com.util.DefaultResponses;
import ch.trivadis.com.util.InitMongoDB;
import io.vertx.core.*;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * Created by Andy Moncsek on 19.05.16. This is an alternate version of the VxmsGateway using "plain"
 */
public class ServiceWithDB extends AbstractVerticle {
    MongoClient mongo;

    @Override
    public void start(Future<Void> startFuture) throws Exception {

        // for demo purposes
        mongo = InitMongoDB.initMongoData(vertx, config());

        Router router = Router.router(vertx);

        // allow pody content in post requests
        router.route().handler(BodyHandler.create());


        // define some REST API
        router.get("/api/users").handler(this::getUsers);

        router.get("/api/users/:id").handler(this::getUserById);

        router.post("/api/users").handler(this::postUser);

        router.put("/api/users/:id").handler(this::updateUser);

        router.delete("/api/users/:id").handler(this::deleteUser);

        // serve static content
        router.route().handler(StaticHandler.create());

        vertx.createHttpServer().requestHandler(router::accept).listen(config().getInteger("port",8080), config().getString("host","0.0.0.0"));

        startFuture.complete();
    }


    private void updateUser(RoutingContext ctx) {
        // update the user properties
        JsonObject update = ctx.getBodyAsJson();
        JsonObject message = DefaultResponses.mapToUser(update, ctx.request().getParam("id"));
        mongo.findOne("users", new JsonObject().put("_id", message.getString("id")), null, lookup -> {
            // error handling
            if (lookup.failed()) {
                ctx.fail(500);
                return;
            }

            JsonObject user = lookup.result();

            if (user == null) {
                // does not exist
                ctx.fail(404);
            } else {

                // update the user properties
                user.put("username", message.getString("username"));
                user.put("firstName", message.getString("firstName"));
                user.put("lastName", message.getString("lastName"));
                user.put("address", message.getString("address"));

                mongo.replace("users", new JsonObject().put("_id", message.getString("id")), user, replace -> {
                    // error handling
                    if (replace.failed()) {
                        ctx.fail(500);
                        return;
                    }
                    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                    ctx.response().end(user.put("_id", replace.result()).encode());
                });
            }
        });
    }


    private void postUser(RoutingContext ctx) {
        JsonObject newUser = ctx.getBodyAsJson();
        mongo.findOne("users", new JsonObject().put("username", newUser.getString("username")), null, lookup -> {
            // error handling
            if (lookup.failed()) {
                ctx.fail(500);
                return;
            }
            JsonObject user = lookup.result();
            if (user != null) {
                // already exists
                ctx.fail(404);
            } else {
                mongo.insert("users", newUser, insert -> {
                    // error handling
                    if (insert.failed()) {
                        ctx.fail(500);
                        return;
                    }
                    ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
                    ctx.response().end(newUser.put("_id", insert.result()).encode());
                });
            }
        });
    }

    private void getUserById(RoutingContext ctx) {
        mongo.findOne("users", new JsonObject().put("_id", ctx.request().getParam("id")), null, lookup -> getResultAndReply(ctx, lookup));
    }


    private void getUsers(RoutingContext ctx) {
        mongo.find("users", new JsonObject(), lookup -> {
            // error handling
            if (lookup.failed()) {
                ctx.fail(404);
                return;
            }
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            ctx.response().end(new JsonArray(lookup.result()).encode());
        });
    }

    private void deleteUser(RoutingContext ctx) {
        mongo.findOne("users", new JsonObject().put("_id", ctx.request().getParam("id")), null, lookup -> {
            // error handling
            if (lookup.failed()) {
                ctx.fail(500);
                return;
            }

            JsonObject user = lookup.result();
            if (user == null) {
                // does not exist
                ctx.fail(404);
            } else {
                mongo.remove("users", new JsonObject().put("_id", ctx.request().getParam("id")), remove -> {
                    // error handling
                    if (remove.failed()) {
                        ctx.fail(500);
                        return;
                    }
                    ctx.response().end("end");
                });
            }
        });

    }

    private void getResultAndReply(RoutingContext ctx, AsyncResult<JsonObject> lookup) {
        if (lookup.failed()) {
            ctx.fail(500);
            return;
        }
        JsonObject user = lookup.result();
        if (user == null) {
            ctx.fail(404);
        } else {
            ctx.response().putHeader(HttpHeaders.CONTENT_TYPE, "application/json");
            ctx.response().end(user.encode());
        }
    }



    public static void main(String[] args) {
        Vertx.vertx().deployVerticle(ServiceWithDB.class.getName(), new DeploymentOptions().setInstances(2).setConfig(new JsonObject().put("local", true)));
    }

}