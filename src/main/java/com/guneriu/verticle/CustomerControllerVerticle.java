package com.guneriu.verticle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.guneriu.model.Customer;
import com.guneriu.model.CustomerEvent;
import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;

/**
 * Created by ugur on 20.05.2016.
 */
@Slf4j
@Component
public class CustomerControllerVerticle {

    @Autowired
    private Vertx vertx;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void initializeVertx() {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());

        router.route(HttpMethod.GET, "/api/customers").produces("application/json")
                .handler(this::getCustomers);

        router.route(HttpMethod.GET, "/api/customers/:customerId").produces("application/json")
                .handler(this::getCustomer);

        router.route(HttpMethod.DELETE, "/api/customers/:customerId").produces("application/json")
                .handler(this::deleteCustomer);

        router.route(HttpMethod.POST, "/api/customers").consumes("application/json").produces("application/json")
                .handler(this::addCustomer);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private void getCustomers(RoutingContext routingContext) {
        vertx.eventBus().send(CustomerEvent.GETALL.name(), null, reply -> handleReply(reply, routingContext));
    }

    private void getCustomer(RoutingContext routingContext) {
        vertx.eventBus().send(CustomerEvent.GET.name(), routingContext.request().getParam("customerId"), reply -> handleReply(reply, routingContext));
    }

    private void deleteCustomer(RoutingContext routingContext) {
        vertx.eventBus().send(CustomerEvent.DELETE.name(), routingContext.request().getParam("customerId"), reply -> handleReply(reply, routingContext));
    }

    private void addCustomer(RoutingContext routingContext) {
        vertx.eventBus().send(CustomerEvent.CREATE.name(), routingContext.getBodyAsString(), reply -> handleReply(reply, routingContext));
    }

    private <T> void handleReply(AsyncResult<Message<T>> reply, RoutingContext routingContext) {
        Message<T> replyMsg = reply.result();
        if (reply.succeeded()) {
            routingContext.response()
                    .setStatusMessage("OK")
                    .setStatusCode(200);
            if (replyMsg.body() != null) {
                routingContext.response().end(replyMsg.body().toString());
            } else {
                routingContext.response().end();
            }
        } else {
            errorResponse(routingContext, 500, reply.cause().getLocalizedMessage());
        }
    }

    private void errorResponse(RoutingContext routingContext, int errorCode, String message) {
        routingContext.response().setStatusCode(errorCode).end(message);
    }
}
