package com.couchbase.demo.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class RuleJobRequestService {

    private final RuleJobRequestRepository repository;

    @Autowired
    public RuleJobRequestService(RuleJobRequestRepository repository) {
        this.repository = repository;
    }

    public Flux<RuleJobRequest> getAll() {
        return repository.findAll();
    }

    public Mono<RuleJobRequest> save(RuleJobRequest request) {
        return repository.save(request);
    }
}
