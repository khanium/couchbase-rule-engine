package com.couchbase.demo.request;

import com.couchbase.demo.rules.Rule;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import java.util.Date;

import static com.couchbase.demo.config.RulerConfig.FORMAT_DATE;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RuleJobRequest {
    private static final String PREFIX = "request:";

    @Id
    private String id;
    private String name;
    private String rule;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT_DATE)
    private Date startsAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT_DATE)
    private Date endsAt;
    @Builder.Default
    @JsonProperty(defaultValue = "ONCE", required = true)
    private Recurrence recurrence = Recurrence.ONCE;
    private String cronExpression;
    @CreatedDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT_DATE)
    private Date createdAt;

    enum Recurrence {
        ONCE,
        DAILY,
        WEEKLY,
        MONTHLY
    }

    public static RuleJobRequest from(Rule rule) {
        return RuleJobRequest.builder()
                .id(PREFIX+rule.getName())
                .name(rule.getName())
                .startsAt(rule.getAvailableFrom())
                .endsAt(rule.getExpiresAt())
                .rule(rule.getId())
                .build();
    }

}
