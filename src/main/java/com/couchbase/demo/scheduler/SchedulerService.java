package com.couchbase.demo.scheduler;

import com.couchbase.demo.jobs.RuleJob;
import com.couchbase.demo.jobs.RuleTask;
import com.couchbase.demo.request.RuleJobRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SchedulerService {

    Flux<RuleTask> schedule(RuleJobRequest jobRequest);

}
