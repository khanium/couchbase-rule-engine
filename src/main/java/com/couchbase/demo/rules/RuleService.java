package com.couchbase.demo.rules;

import com.couchbase.demo.eventing.EventingClient;
import com.couchbase.demo.request.RuleJobRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.couchbase.demo.rules.Rule.PREFIX;

@Service
public class RuleService {
    private final Logger LOGGER = LoggerFactory.getLogger(RuleService.class);

    private final RuleRepository repository;
    private final EventingClient eventingClient;
    private RuleToEventingFunctionConverter mapper = new RuleToEventingFunctionConverter();

    @Autowired
    public RuleService(RuleRepository repository, EventingClient eventingClient) {
        this.repository = repository;
        this.eventingClient = eventingClient;
    }

    public Mono<Rule> get(String id) {
        return this.repository.findById(id);
    }

    public Flux<Rule> getAll() {
        return this.repository.findAll();
    }

    public Mono<Rule> getOrFail(String rule) {
        return repository.findById(rule).switchIfEmpty(Mono.error(new IllegalArgumentException("Missing Rule '"+rule+"'")));
    }

    public Mono<Rule> create(Rule rule) {
        return eventingClient.createFunction(mapper.mapTo(rule))
                .flatMap(x -> repository.save(rule));
    }

    public Mono<String> delete(String id) {
        String name = id.substring(PREFIX.length());
        return eventingClient.deleteFunction(name).flatMap(x -> repository.deleteById(id)).flatMap(x-> Mono.just(ResponseEntity.ok().build().toString()));
    }

}
