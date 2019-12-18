package com.couchbase.demo.schema;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("schemas")
public class SchemaController {
    private final Logger LOGGER = LoggerFactory.getLogger(SchemaController.class);
    private final InferSchemaService inferService;
    private final SchemaTypeService schemaTypeService;


    @Autowired
    public SchemaController(InferSchemaService inferService, SchemaTypeService schemaTypeService) {
        this.inferService = inferService;
        this.schemaTypeService = schemaTypeService;
    }

    @GetMapping("/{bucket}/types/{type}/infer")
    public Mono<SchemaType> infer(@PathVariable String bucket, @PathVariable String type) {
        LOGGER.info("infering `{}`.`{}` document type properties...", bucket, type);
        return inferService.infer(bucket, type);
    }

    @PostMapping("/{bucket}/types/{type}/infer")
    public Mono<SchemaType> inferAndStore(@PathVariable String bucket, @PathVariable String type) {
        LOGGER.info("infering and storing `{}`.`{}` document type properties...", bucket, type);
        return inferService.infer(bucket, type).map(schemaTypeService::save).flatMap(x -> x);
    }

    @PostMapping
    public Mono<SchemaType> store(@RequestBody SchemaType type) {
        return schemaTypeService.save(type);
    }

    @GetMapping
    public Flux<SchemaType> getAll() {
        return schemaTypeService.getSchemaTypes();
    }

    @GetMapping("/{bucket}")
    public Flux<SchemaType> getAllBySchema(@PathVariable String bucket) {
        return schemaTypeService.getAllByBucket(bucket);
    }


    @GetMapping("/{bucket}/type/{type}")
    public Mono<SchemaType> getTypeBySchema(@PathVariable String bucket, @PathVariable String type) {
        return schemaTypeService.getByBucket(bucket, type);
    }


}
