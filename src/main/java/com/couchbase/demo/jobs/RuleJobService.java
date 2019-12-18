package com.couchbase.demo.jobs;

import com.couchbase.demo.request.RuleJobRequest;
import com.couchbase.demo.rules.RuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class RuleJobService {
    private final Logger LOGGER = LoggerFactory.getLogger(RuleJobService.class);

    private final static String DEFAULT_GROUP = "group";
    private final RuleJobRepository ruleJobRepository;
    private final RuleService ruleService;

    @Autowired
    public RuleJobService(RuleJobRepository ruleJobRepository, RuleService ruleService) {
        this.ruleJobRepository = ruleJobRepository;
        this.ruleService = ruleService;
    }

    private String buildSystemRuleJobName(String displayName) {
        return DEFAULT_GROUP+":"+displayName.replace(" ","").toLowerCase();
    }

    public Mono<RuleJob> mapToRuleJob(RuleJobRequest request) {
        LOGGER.info("::mapToRuleJob:: {}",request.getName());
        return ruleService.getOrFail(request.getRule())
                .map(rule -> RuleJob.builder().rule(rule)
                        .name(buildSystemRuleJobName(request.getName()))
                        .displayname(request.getName())
                        .group(DEFAULT_GROUP)
                        .expiresAt(request.getEndsAt())
                        .validFrom(request.getStartsAt()).build())
                .map(this::buildToRuleTaskJob);
    }

    private RuleJob buildToRuleTaskJob(RuleJob job) {
        LOGGER.info("building CREATE task job {}",job.getName());
        job.getTasks().add(RuleTask.createStartRuleTask(ruleService, job));
        if(job.getExpiresAt()!=null) {
            LOGGER.info("building DELETE task job {}",job.getName());
            job.getTasks().add(RuleTask.createStopRuleTask(ruleService, job));
        }
        return job;
    }

    public Mono<RuleJob> save(RuleJob job) {
        LOGGER.info("Saving RuleJob {}...",job.getName());
        return this.ruleJobRepository.save(job);
    }

    public Flux<RuleJob> getAll() {
        return this.ruleJobRepository.findAll();
    }

    public Flux<RuleTask> getAllTasks() {
        // TODO
        return getAll().flatMap(x -> Flux.fromIterable(x.getTasks()));
    }
}
