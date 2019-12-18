package com.couchbase.demo.schema;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SchemaTypeService {
    private final SchemaTypeRepository repository;

    @Autowired
    public SchemaTypeService(SchemaTypeRepository repository) {
        this.repository = repository;
    }

    public Mono<SchemaType> save(SchemaType type) {
        return this.repository.save(type);
    }

    public Flux<SchemaType> getSchemaTypes() {
        return this.repository.findAll();
    }

    public Flux<SchemaType> getAllByBucket(String bucket) {
        return this.repository.findAllByBucket(bucket);
    }

    public Mono<SchemaType> getByBucket(String bucket, String type) {
        return this.repository.findByBucketAndType(bucket, type);
    }

    public Flux<String> getAvailableBuckets() {
        return this.repository.findAllAvailableBuckets();
    }
}
