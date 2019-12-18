package com.couchbase.demo.request;

import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RuleJobRequestRepository extends ReactiveCouchbaseRepository<RuleJobRequest, String> {

    @Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter}")
    Flux<RuleJobRequest> findAll();
}
