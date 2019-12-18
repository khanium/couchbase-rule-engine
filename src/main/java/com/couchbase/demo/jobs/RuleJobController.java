package com.couchbase.demo.jobs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/job")
public class RuleJobController {

    private final RuleJobService service;

    @Autowired
    public RuleJobController(RuleJobService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<RuleJob> saveRuleJob(RuleJob job) {
        return service.save(job);
    }
}
