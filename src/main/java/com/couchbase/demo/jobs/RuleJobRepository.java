package com.couchbase.demo.jobs;

import org.springframework.data.couchbase.core.query.Query;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface RuleJobRepository extends ReactiveCouchbaseRepository<RuleJob, String> {

    @Query("#{#n1ql.selectEntity} WHERE #{#n1ql.filter}")
    Flux<RuleJob> findAll();
}
