package com.couchbase.demo.navigation;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.cluster.BucketSettings;
import com.couchbase.demo.schema.SchemaTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class NavigationRulesEngineService {
    private final Cluster cluster;
    private final SchemaTypeService schemaTypeService;

    @Autowired
    public NavigationRulesEngineService(Cluster cluster, SchemaTypeService schemaTypeService) {
        this.cluster = cluster;
        this.schemaTypeService = schemaTypeService;
    }

    public Flux<String> getBuckets(boolean onlyActive) {
        return onlyActive ? Flux.fromIterable(this.cluster.clusterManager().getBuckets()).map(BucketSettings::name) :
                schemaTypeService.getAvailableBuckets();
    }


}
