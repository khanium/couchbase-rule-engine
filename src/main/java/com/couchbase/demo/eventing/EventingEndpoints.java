package com.couchbase.demo.eventing;

import lombok.Getter;
import org.springframework.http.HttpMethod;

import static org.springframework.http.HttpMethod.*;

@Getter
public enum EventingEndpoints {
    CREATE_FUNCTION(POST, "/api/v1/functions/{function_name}", "Create a single Function. The Function name in the body must match that on the URL. Function definition includes current settings."),
    CREATE_FUNCTIONS(POST, "/api/v1/functions/", "Creates multiple Functions. Function names must be unique. When multiple Functions have the same name, an error is reported."),
    GET_FUNCTION(GET, "/api/v1/functions/{function_name}", "View a list of Functions. Provides a list of Functions available in the cluster. The list includes both the deployed and the undeployed Functions."),
    GET_FUNCTIONS(GET, "/api/v1/functions", "View a list of Functions. Provides a list of Functions available in the cluster. The list includes both the deployed and the undeployed Functions."),
    DELETE_FUNCTION(DELETE, "/api/v1/functions/{function_name}", "Deletes a specific Functions from the cluster."),
    DELETE_FUNCTIONS(DELETE, "/api/v1/functions", "Deletes multiple Functions from the cluster."),
    GET_FUNCTION_SETTINGS(GET, "/api/v1/functions/{function_name}/settings", "Get Function settings"),
    UPDATE_FUNCTION_SETTINGS(POST, "/api/v1/functions/{function_name}/settings", "Edit Function settings. During an edit, settings provided are merged. Unspecified attributes retain their prior values."),
    UPDATE_GLOBAL_SETTINGS(POST, "/api/v1/config", "Modify global configuration. During an edit, settings provided are merged. Unspecified attributes retain their prior values. The response indicates whether the Eventing service must be restarted for the new changes to take effect."),
    DEPLOY_FUNCTION(POST, "/api/v1/functions/{function_name}/settings", "Deploys a Function. A deploy CURL example is provided for reference."),
    UNDEPLOY_FUNCTION(POST, "/api/v1/functions/{function_name}/settings", "Undeploys a Function. An undeploy CURL example is provided for reference."),
    DEPLOY_FUNCTION_WITH_CODE(POST, "/api/v1/functions/{function_name}/settings", "Deploys a Function with the provided code.");

    private final HttpMethod method;
    private final String endpoint;
    private final String description;

    EventingEndpoints(HttpMethod method, String endpoint, String description) {
        this.method = method;
        this.endpoint = endpoint;
        this.description = description;
    }

}
