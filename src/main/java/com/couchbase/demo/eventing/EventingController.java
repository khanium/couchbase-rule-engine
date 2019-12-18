package com.couchbase.demo.eventing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("eventing")
public class EventingController {
    private final Logger LOGGER = LoggerFactory.getLogger(EventingController.class);

    private final EventingClient eventingClient;

    public EventingController(EventingClient eventingClient) {
        this.eventingClient = eventingClient;
    }

    @GetMapping("/functions")
    public Flux<EventingFunction> getAll() {
        return eventingClient.getFunctions();
    }

    @GetMapping("/functions/{name}")
    public Mono<EventingFunction> getFunction(@PathVariable String name) {
        return eventingClient.getFunction(name);
    }

    @PostMapping("/function")
    public Mono<ResponseEntity<String>> createFunction(@RequestBody EventingFunction function) {
        return eventingClient.createFunction(function);
    }

    @PostMapping("/functions")
    public Flux<EventingFunction> createFunctions(@RequestBody List<EventingFunction> functions) {
        return eventingClient.createFunctions(functions);
    }

    @DeleteMapping("/functions/{name}")
    public Mono<String> deleteFunction(@PathVariable String name) {
        return eventingClient.deleteFunction(name);
    }

    @DeleteMapping("/functions")
    public Mono<ResponseEntity<String>> deleteFunctions(@RequestBody List<String> names) {
        //DELETE_FUNCTIONS(DELETE, "/api/v1/functions", "Deletes multiple Functions from the cluster."),
        return eventingClient.deleteFunctions(names);
    }
/*
    @PutMapping("/functions/{name}")
    public Mono<EventingFunction.Settings> updateFunctionSettings(@PathVariable String name, @RequestBody EventingFunction.Settings settings) {
        //UPDATE_FUNCTION_SETTINGS(POST, "/api/v1/functions/{function_name}/settings", "Edit Function settings. During an edit, settings provided are merged. Unspecified attributes retain their prior values."),
        return eventingClient.updateFunctionSettings(name, settings);
    }

    @PutMapping("/settings")
    public Mono<EventingSetting> updateGlobalSettings(@RequestBody EventingSetting globalSettings) {
        //UPDATE_GLOBAL_SETTINGS(POST, "/api/v1/config", "Modify global configuration. During an edit, settings provided are merged. Unspecified attributes retain their prior values. The response indicates whether the Eventing service must be restarted for the new changes to take effect."),
        return eventingClient.updateGlobalSettings(globalSettings);
    }
 */

    @PostMapping("/functions/{name}/deploy")
    public Mono<EventingFunction.Settings> deployFunction(@PathVariable String name) {
        //DEPLOY_FUNCTION(POST, "/api/v1/functions/{function_name}/settings", "Deploys a Function. A deploy CURL example is provided for reference.");
        return eventingClient.deployFunction(name);
    }

    @PostMapping("/functions/{name}/undeploy")
    public Mono<EventingFunction.Settings> undeployFunction(@PathVariable String name) {
        return eventingClient.undeployFunction(name);
    }

    @GetMapping("/functions/{functionName}/settings")
    public Mono<EventingFunction.Settings> getFunctionSettings(@PathVariable String functionName) {
        return eventingClient.getFunctionSettings(functionName);
    }
/*
    @PostMapping("/functions/{name}/create-and-deploy")
    public Mono<EventingFunction> deployFunctionAndCode(@PathVariable String name, @RequestBody EventingFunction function) {
        //DEPLOY_FUNCTION_WITH_CODE(POST, "/api/v1/functions/{function_name}/settings", "Deploys a Function with the provided code.");
        checkNotNullOrEmpty(name);
        checkNotNull(function);
        checkNotNullOrEmpty(function.getAppname());
        if(name.compareToIgnoreCase(function.getAppname())!=0) {
            throw new IllegalArgumentException(format("Path name '%s' is not equals to eventing function appname '%s' ",name, function.getAppname()));
        }
        return eventingClient.deployFunctionAndCode(function);
    }
*/

}
