package com.couchbase.demo.schema;

import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SchemaTypeRepository extends ReactiveCouchbaseRepository<SchemaType, String> {

    @Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter}")
    Flux<SchemaType> findAll();

    Flux<SchemaType> findAllByBucket(String name);

    Mono<SchemaType> findByBucketAndType(String bucket, String type);

    @Query("SELECT DISTINCT name FROM #{#n1ql.bucket} WHERE #{#n1ql.filter}")
    Flux<String> findAllAvailableBuckets();
}
