package com.couchbase.demo.rules;

import com.couchbase.demo.jobs.RuleTask;
import com.couchbase.demo.jobs.RuleTaskJob;
import com.couchbase.demo.request.RuleJobRequest;
import com.couchbase.demo.scheduler.SchedulerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.couchbase.demo.rules.Rule.PREFIX;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "rules", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class RuleController {
    private final Logger LOGGER = LoggerFactory.getLogger(RuleController.class);

    private final RuleService ruleService;
    private final SchedulerService schedulerService;

    @Autowired
    public RuleController(RuleService ruleService, SchedulerService schedulerService) {
        this.ruleService = ruleService;
        this.schedulerService = schedulerService;
    }

    @GetMapping
    public Flux<Rule> getAll() {
        return this.ruleService.getAll();
    }

    @GetMapping("/{name}")
    public Mono<Rule> getRule(@PathVariable String name) {
        return this.ruleService.get(name);
    }

    @PostMapping(consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
    public Flux<RuleTask> createRule(@RequestBody Rule rule) {
        LOGGER.info("Creating rule {}...",rule.toString());
        //TODO   checkNotNullOrEmptyOrSpaces(rule.getName());
        rule.setId((PREFIX+rule.getName()).toLowerCase());
        return ruleService.create(rule)
                .map(RuleJobRequest::from)
                .log("RULE-REQUEST")
                .flatMapMany(schedulerService::schedule);
    }


    @DeleteMapping("/{name}")
    public Mono<String> deleteRule(@PathVariable("name") String name) {
        LOGGER.info("Deleting rule {}...",name);
        //TODO check status, undeploy first, cancel future triggers and then delete function and rule
        return ruleService.delete(name);
    }

}
