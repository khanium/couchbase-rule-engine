package com.couchbase.demo.eventing;

import com.couchbase.demo.scheduler.QuartzSchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.couchbase.demo.eventing.EventingEndpoints.*;
import static com.couchbase.demo.eventing.EventingFunction.Settings;
import static java.util.stream.Collectors.joining;

@Component
public class EventingClient {
    private static final Settings DEPLOY_SETTINGS = Settings.builder().deploymentStatus(true).processingStatus(true).build();
    private static final Settings UNDEPLOY_SETTINGS = Settings.builder().deploymentStatus(false).processingStatus(false).build();
    private final Logger LOGGER = LoggerFactory.getLogger(EventingClient.class);

    private final WebClient client;

    @Autowired
    public EventingClient(WebClient client) {
        this.client = client;
    }

    public Mono<ResponseEntity<String>> createFunction(EventingFunction function) {
        LOGGER.info("Creating '{}' function...",function.getAppname());
        LOGGER.debug("Creating function: {}", function);
        return client.method(CREATE_FUNCTION.getMethod()).uri(CREATE_FUNCTION.getEndpoint(), function.getAppname()).body(Mono.just(function), EventingFunction.class).retrieve().toEntity(String.class);
    }

    public Flux<EventingFunction> createFunctions(List<EventingFunction> functions) {
        LOGGER.info("Creating '{}' functions...",functions.stream().map(EventingFunction::getAppname).collect(joining()));
        return client.method(CREATE_FUNCTIONS.getMethod()).uri(CREATE_FUNCTIONS.getEndpoint()).body(Mono.just(functions), EventingFunction.class).retrieve().bodyToFlux(EventingFunction.class);
    }

    public Mono<EventingFunction> getFunction(String name) {
        LOGGER.debug("Get Function {}",name);
        return client.method(GET_FUNCTION.getMethod()).uri(GET_FUNCTION.getEndpoint(), name).retrieve().bodyToMono(EventingFunction.class);
    }

    public Flux<EventingFunction> getFunctions() {
        LOGGER.debug("Get Functions call");
        return client.method(GET_FUNCTIONS.getMethod()).uri(GET_FUNCTIONS.getEndpoint()).retrieve().bodyToFlux(EventingFunction.class);
    }

    public Mono<String> deleteFunction(String name) {
        return client.method(DELETE_FUNCTION.getMethod()).uri(DELETE_FUNCTION.getEndpoint(), name).retrieve().bodyToMono(String.class);
    }

    public Mono<ResponseEntity<String>> deleteFunctions(List<String> names) {
        LOGGER.info("Deleting functions: [{}]", String.join(", ", names));
        return client.method(DELETE_FUNCTIONS.getMethod()).uri(DELETE_FUNCTIONS.getEndpoint()).retrieve().toEntity(String.class);
    }

    /*
        public void updateFunctionSettings(String name) {
            //UPDATE_FUNCTION_SETTINGS(POST, "/api/v1/functions/{function_name}/settings", "Edit Function settings. During an edit, settings provided are merged. Unspecified attributes retain their prior values."),
            client.method(UPDATE_FUNCTION_SETTINGS.getMethod()).uri(UPDATE_FUNCTION_SETTINGS.getEndpoint(), name).retrieve().toBodilessEntity();
        }

        public void updateGlobalSettings() {
            //UPDATE_GLOBAL_SETTINGS(POST, "/api/v1/config", "Modify global configuration. During an edit, settings provided are merged. Unspecified attributes retain their prior values. The response indicates whether the Eventing service must be restarted for the new changes to take effect."),
            client.method(UPDATE_GLOBAL_SETTINGS.getMethod()).uri(UPDATE_GLOBAL_SETTINGS.getEndpoint()).retrieve().toBodilessEntity();
        }
    */
    public Mono<Settings> deployFunction(String name) {
        LOGGER.info("Deploying '{}' function...",name);
        return client.method(DEPLOY_FUNCTION.getMethod()).uri(DEPLOY_FUNCTION.getEndpoint(), name).body(Mono.just(DEPLOY_SETTINGS), Settings.class).retrieve().bodyToMono(Settings.class);
    }

    public Mono<Settings> undeployFunction(String name) {
        LOGGER.info("Undeploying '{}' function...",name);
        return client.method(UNDEPLOY_FUNCTION.getMethod()).uri(UNDEPLOY_FUNCTION.getEndpoint(), name).body(Mono.just(UNDEPLOY_SETTINGS), Settings.class).retrieve().bodyToMono(Settings.class);
    }

    public Mono<Settings> getFunctionSettings(String functionName) {
        LOGGER.debug("Get Function Settings '{}' function...",functionName);
        return client.method(GET_FUNCTION_SETTINGS.getMethod()).uri(GET_FUNCTION_SETTINGS.getEndpoint(), functionName).retrieve().bodyToMono(Settings.class);
    }
/*
    public void deployFunctionAndCode(EventingFunction function) {
        //DEPLOY_FUNCTION_WITH_CODE(POST, "/api/v1/functions/{function_name}/settings", "Deploys a Function with the provided code.");
        client.method(DEPLOY_FUNCTION_WITH_CODE.getMethod()).uri(DEPLOY_FUNCTION_WITH_CODE.getEndpoint(), function.getAppname()).body(function, EventingFunction.class).retrieve().toBodilessEntity();
    }

*/


}
