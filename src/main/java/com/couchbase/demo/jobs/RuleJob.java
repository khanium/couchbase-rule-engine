package com.couchbase.demo.jobs;

import com.couchbase.demo.rules.Rule;
import com.fasterxml.jackson.annotation.*;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.couchbase.demo.config.RulerConfig.FORMAT_DATE;

@Data
@Builder
public class RuleJob {
    public static final String PREFIX="rulejob:";
    @Id
    private String id;
    private String name;
    private String displayname;
    private String group;
    private Rule rule;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT_DATE)
    private Date validFrom;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT_DATE)
    private Date expiresAt;
    @Builder.Default
    @JsonBackReference
    private List<RuleTask> tasks = new ArrayList<>();


    public static RuleJob from(Rule rule) {
        // TODO adding create and delete Tasks
        String ruleJobname= rule.getName().replace(" ","").toLowerCase();
        return RuleJob.builder().id(PREFIX+ruleJobname)
                .displayname(rule.getName())
                .name(ruleJobname).group(rule.getSourceBucket()).rule(rule).build();
    }


}
