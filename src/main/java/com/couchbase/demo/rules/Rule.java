package com.couchbase.demo.rules;

import com.couchbase.client.java.repository.annotation.Id;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.couchbase.demo.config.RulerConfig.FORMAT_DATE;
import static com.couchbase.demo.rules.Rule.LogicalOperatorType.AND;
import static com.couchbase.demo.rules.Rule.OperatorType.*;

@Data
@Builder
@ToString
public class Rule {
    public static final String PREFIX="rule:";

    @Id
    private String id;
    private String sourceBucket;
    private String metadataBucket;
    private String targetBucket;
    private String name;
    private String description;
    @JsonProperty(defaultValue = "MEDIUM", required = true)
    private QualityOfService priority;
    @Builder.Default
    @JsonProperty(defaultValue = "AND", required = true)
    private LogicalOperatorType type = AND;
    @Builder.Default
    private List<FilterRule> predicates = new ArrayList<>();
    @Builder.Default
    private List<ActionRule> actions = new ArrayList<>();
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT_DATE)
    private Date availableFrom;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = FORMAT_DATE)
    private Date expiresAt;

    enum QualityOfService {
        HIGH,
        MEDIUM,
        LOW
    }

    enum LogicalOperatorType {
        AND,
        OR
    }

    @Builder
    @Data
    public static class FilterRule {
       private String property;
       private OperatorType operator;
       @Builder.Default
       @JsonProperty(defaultValue = "[]", required = true)
       private List values = new ArrayList<>();

       public String toFilterExpression() {
           //TODO nested & complex operators expressions and checks
           if(values!=null && values.size() > 1 && (!(IN.equals(operator) || NOT_IN.equals(operator)))) {
               throw new IllegalArgumentException("Operator '"+operator+"' doesn't support array values. Expected array's operator values are ['IN','NOT_IN']");
           }

           String valuesArray = values ==null || values.isEmpty() ? "" : (String) values.stream().map(this::quote).collect(Collectors.joining(", "));

           return operator.getCode()
                   .replace(":property", "doc."+property)
                   .replace(":values", valuesArray);
       }

       private String quote(Object value) {
           return value instanceof Integer || value instanceof Long ? ""+value : "'"+value+"'";
       }

    }


    @Data
    @Builder
    public static class ActionRule {
        private String name;
        private String code;
        @Builder.Default
        @JsonProperty(defaultValue = "[]", required = true)
        private List<ActionRule> actions = new ArrayList<>();
    }

    public enum OperatorType {
        EQUAL_TO(":property == :values"),
        GREATER_THAN(":property > :values"),
        LESS_THAN(":property < :values"),
        EQUAL_OR_GREATER_THAN(":property >= :values"),
        EQUAL_OR_LESS_THAN(":property <= :values"),
        EXISTS(":property"),
        NOT_EXISTS("!:property"),
        IN("[:values].includes(:property)"),
        NOT_IN("![:values].includes(:property)"),
        CONTAINS(":property.includes(:values)"),
        NOT_CONTAIN("!:property.includes(:values)");
        private final String code;

        OperatorType(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

    }

/*
    public static List<ActionRule> createActions() {
        List<ActionRule> rules = new ArrayList<>();
        rules.add(ActionRule.builder()
                .name("move")
                .build());
        return rules;
    }

    public static List<FilterRule> createPredicates() {
        List<FilterRule> filter = new ArrayList<>();
        filter.add(FilterRule.builder().property("propertyName").operator(OperatorType.EQUAL_TO).values(Arrays.asList("test1","test2")).build());
        return filter;
    }

    public static void main(String... args) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        Rule sampleRule = Rule.builder()
                .sourceBucket("demo")
                .actions(createActions())
                .availableFrom(new Date())
                .description("muy bonito")
                .expiresAt(new Date(Instant.now().toEpochMilli()+300000))
                .id("rule:myfirstrule")
                .metadataBucket("metadata")
                .name("My First Rule")
                .predicates(createPredicates())
                .priority(QualityOfService.MEDIUM)
                .targetBucket("destination")
                .build();
        String value = mapper.writeValueAsString(sampleRule);
        System.out.println("-- ---------------------------------- --");
        System.out.println("-- Output: ");
        System.out.println(value);

    }
*/
}
