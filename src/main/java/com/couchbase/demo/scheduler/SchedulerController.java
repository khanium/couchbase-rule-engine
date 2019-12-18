package com.couchbase.demo.scheduler;

import com.couchbase.demo.jobs.RuleJob;
import com.couchbase.demo.jobs.RuleJobService;
import com.couchbase.demo.jobs.RuleTask;
import com.couchbase.demo.request.RuleJobRequest;
import com.couchbase.demo.request.RuleJobRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
@RequestMapping("scheduler")
public class SchedulerController {

    private final RuleJobService ruleJobService;
    private final SchedulerService schedulerService;
    private final RuleJobRequestService ruleJobRequestService;

    @Autowired
    public SchedulerController(RuleJobService ruleJobService, SchedulerService schedulerService, RuleJobRequestService ruleJobRequestService) {
        this.ruleJobService = ruleJobService;
        this.schedulerService = schedulerService;
        this.ruleJobRequestService = ruleJobRequestService;
    }

    @GetMapping("/requests")
    public Flux<RuleJobRequest> getAllRequests() {
        return ruleJobRequestService.getAll();
    }

    @GetMapping("/jobs")
    public Flux<RuleJob> getAllRuleJobs() {
        // TODO
        return ruleJobService.getAll();
    }

    @GetMapping("/jobs/include/tasks")
    public Flux<RuleTask> getAllRuleTaskJobs() {
        // TODO
        return ruleJobService.getAllTasks();
    }

    @PostMapping("/schedule")
    public Flux<RuleTask> schedule(@RequestBody RuleJobRequest request) {
        // TODO validate(request);
        return schedulerService.schedule(request);
    }


}
