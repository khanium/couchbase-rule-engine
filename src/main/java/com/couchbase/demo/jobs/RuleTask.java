package com.couchbase.demo.jobs;

import com.couchbase.demo.rules.RuleService;
import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.util.Date;

import static com.couchbase.demo.config.RulerConfig.FORMAT_DATE;
import static com.couchbase.demo.jobs.RuleTask.RuleTaskType.START;
import static com.couchbase.demo.jobs.RuleTask.RuleTaskType.STOP;

@Builder
@Data
public class RuleTask implements Serializable {
    public static final String PREFIX = "ruleTask:";
    @Id
    private String id;
    @JsonBackReference
    private RuleJob parent;
    private RuleTaskType type;
    private String name;
    private String group;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT_DATE)
    private Date startsAt;
    private Status status;

    public static RuleTask createStartRuleTask(RuleService service, RuleJob parentJob) {
        String taskName = (parentJob.getName() + ":" + START.name()).toLowerCase();
        return new RuleTask(PREFIX+taskName, parentJob, START, taskName, parentJob.getGroup(), parentJob.getValidFrom(), Status.QUEUED);
    }

    public static RuleTask createStopRuleTask(RuleService service, RuleJob parentJob) {
        String taskName = (parentJob.getName() + ":" + STOP.name()).toLowerCase();
        return new RuleTask(PREFIX+taskName, parentJob, STOP, taskName , parentJob.getGroup(), parentJob.getExpiresAt(), Status.QUEUED);
    }

    enum Status {
        NOT_SET,
        QUEUED,
        INITILIAZED,
        DEPLOYING,
        DEPLOYED,
        UNDEPLOYING,
        UNDEPLOYED,
        COMPLETED
    }


    public enum RuleTaskType {
        START,
        PAUSE,
        STOP
    }
}
