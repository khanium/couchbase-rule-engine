package com.couchbase.demo.scheduler;

import com.couchbase.demo.jobs.RuleJob;
import com.couchbase.demo.jobs.RuleJobService;
import com.couchbase.demo.jobs.RuleTask;
import com.couchbase.demo.jobs.RuleTaskJob;
import com.couchbase.demo.request.RuleJobRequest;
import com.couchbase.demo.request.RuleJobRequestService;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.UUID;

import static com.couchbase.demo.jobs.RuleTask.RuleTaskType.START;

@Service
public class QuartzSchedulerService implements SchedulerService {
    private final Logger LOGGER = LoggerFactory.getLogger(QuartzSchedulerService.class);

    private final Scheduler scheduler;
    private final RuleJobService ruleJobService;
    private final RuleJobRequestService requestService;
    private final RuleJob2QuartzConverter quartzConverter = new RuleJob2QuartzConverter();

    @Autowired
    public QuartzSchedulerService(Scheduler scheduler, RuleJobService ruleJobService, RuleJobRequestService requestService) {
        this.scheduler = scheduler;
        this.ruleJobService = ruleJobService;
        this.requestService = requestService;
    }

    @PostConstruct
    private void init() throws SchedulerException {
        LOGGER.info("Adding scheduler listener... ");
        scheduler.getListenerManager().addTriggerListener(new QuartzSchedulerService.QuartzTriggerListener());
        LOGGER.info("Added scheduler listener.");
        LOGGER.info("checking scheduler status: {}", scheduler.isStarted() ? "STARTED" : "STOPPED");
        if (!this.scheduler.isStarted()) {
            LOGGER.info("starting scheduler...");
            scheduler.start();
        }
    }

    private RuleJobRequest assignId(RuleJobRequest request) {
        request.setId("request:"+request.getRule().replace("rule:","")+":"+ UUID.randomUUID());
        return request;
    }

    public Flux<RuleTask> schedule(RuleJobRequest request) {
        return Mono.just(request)
                .doOnNext(this::assignId)
                .doOnNext(r -> LOGGER.info("schedule {}", r))
                .flatMap(requestService::save)
                .flatMap(ruleJobService::mapToRuleJob)
              //  .flatMap(ruleJobService::save)
                .flatMapIterable(RuleJob::getTasks)
                .flatMap(this::scheduleTask);
    }

    private Mono<RuleTask> scheduleTask(RuleTask task) {
        // TODO SimpleTrigger trigger = createTrigger(task);
        LOGGER.info("schedule Task {}",task.getName());
        return createTrigger(task);
    }

    private Mono<RuleTask> createTrigger(RuleTask task) {
        // TODO SimpleTrigger trigger = ... ;
        LOGGER.info("scheduling trigger '{}' at {} ...",task.getName(), task.getStartsAt());
        JobDetail jobDetail = quartzConverter.toJobDetail(task);
        SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .withIdentity(quartzConverter.buildTriggerName(task), task.getGroup())
                .startAt(task.getStartsAt())
                //  .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                  .withMisfireHandlingInstructionFireNow())
                .forJob(jobDetail)
                .build();
        try {
            Date nextFiredDate = scheduler.scheduleJob(jobDetail, trigger);
            LOGGER.info("scheduled trigger '{}' at {}",task.getName(),nextFiredDate);
            return Mono.just(task);
        } catch (SchedulerException e) {
            return Mono.<RuleTask>error(new Exception()).doOnError(th -> LOGGER.error("{} triggering {}",th.getClass().getSimpleName(), task.getName()));
        }
    }

    private static class RuleJob2QuartzConverter {

        public JobDetail toJobDetail(RuleTask task) {
            JobDataMap map = new JobDataMap();
            map.put("config", task);
            return JobBuilder.newJob(RuleTaskJob.class)
                    .withIdentity(task.getId(), task.getGroup())
                    .withDescription(task.getParent().getRule().getDescription())
                    .setJobData(map)
                  //  .requestRecovery(true)
                  //  .storeDurably(true)
                    .build();
        }

        private String buildTriggerName(RuleTask task) {
            return "trigger-" + task.getId() + "-"+UUID.randomUUID().toString();
        }

    }

    public static class QuartzTriggerListener implements TriggerListener {
        private final Logger LOGGER = LoggerFactory.getLogger(QuartzSchedulerService.class);
        @Override
        public String getName() {
            return "SYSTEM";
        }

        @Override
        public void triggerFired(Trigger trigger, JobExecutionContext jobExecutionContext) {
            //LOGGER.info("-- ------------------------- --");
            LOGGER.debug("-- trigger fired "+jobExecutionContext.getFireTime()+trigger.getJobDataMap().toString()+" : "+jobExecutionContext.getJobDetail().getDescription());
            //LOGGER.info("-- ------------------------- --");
        }

        @Override
        public boolean vetoJobExecution(Trigger trigger, JobExecutionContext jobExecutionContext) {
            return vetoCreateRuleTaskJobWithExpiredDate(jobExecutionContext);
        }

        private boolean vetoCreateRuleTaskJobWithExpiredDate(JobExecutionContext jobExecutionContext) {
            boolean veto = false;
            Object config = jobExecutionContext.getJobDetail().getJobDataMap().get("config");
            if(config instanceof RuleTask) {
                RuleTask task = (RuleTask) config;
                if(START.equals(task.getType())) {
                    veto = task.getParent().getExpiresAt() != null && task.getParent().getExpiresAt().before(new Date());
                    if(veto) {
                        LOGGER.warn("skipped '{}' trigger", jobExecutionContext.getTrigger().getKey().getName());
                    }
                }
            }
            return veto;
        }

        @Override
        public void triggerMisfired(Trigger trigger) {
            LOGGER.warn("++ Misfired "+trigger.getKey().getName());
        }

        @Override
        public void triggerComplete(Trigger trigger, JobExecutionContext jobExecutionContext, Trigger.CompletedExecutionInstruction completedExecutionInstruction) {
            LOGGER.info("== ======================== ==");
            LOGGER.info("== Job '{}' Completed!!",trigger.getKey().getName());
            LOGGER.info("== ======================== ==");
        }
    }


}
