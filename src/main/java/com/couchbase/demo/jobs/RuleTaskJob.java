package com.couchbase.demo.jobs;

import com.couchbase.demo.eventing.EventingClient;
import lombok.Data;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;


@Data
@Component
public class RuleTaskJob implements Job {

    private transient final Logger LOGGER = LoggerFactory.getLogger(RuleTaskJob.class);
    private RuleTask config;

    @Autowired
    private final EventingClient client;


    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        String triggerKey = jobExecutionContext.getTrigger().getKey().getName();
        checkNotNull(config, "Missing '" + triggerKey + "' Job configuration");
        String name = config.getName();
        LOGGER.info("-- ------------------------------------------- --");
        LOGGER.info("-- TRIGGER TASK '{}' ", triggerKey);
        LOGGER.info("-- ------------------------------------------- --");
        LOGGER.info("-- starting '{}:{}' at {} ...", name, jobExecutionContext.getFireInstanceId(), jobExecutionContext.getFireTime());
        //
        String rulename = config.getParent().getRule().getName();
        switch (config.getType()) {
            case START:
                deployFunction(rulename);
                break;
            case PAUSE:
                pauseFunction(rulename);
                break;
            case STOP:
                undeployFunction(rulename);
                break;
            default:
                LOGGER.error("Task Job '{}' not supported", config.getType());
                break;
        }
        //
        LOGGER.info("-- Job Instance '{}:{}' completed at {}", name, jobExecutionContext.getFireInstanceId(), LocalDate.now());
        LOGGER.info("-- ------------------------------------------- --");

    }

    private void pauseFunction(String name) {
        throw new RuntimeException("Pause eventing function API has been not implemented yet into the client");
        //client.pauseFunction(name);
    }

    private void deployFunction(String name) {
        client.deployFunction(name).subscribe();
    }

    private void undeployFunction(String name) {
        client.undeployFunction(name).subscribe();
    }

    private void checkNotNull(Object obj, String msg) {
        if (obj == null) {
            throw new IllegalArgumentException(msg);
        }
    }


}
