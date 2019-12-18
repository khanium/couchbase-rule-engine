package com.couchbase.demo.rules;

import com.couchbase.demo.eventing.EventingFunction;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RuleToEventingFunctionConverter {
    private static final String TEMPLATE_CODE = "function OnUpdate(doc, meta) {  \n :onUpdateCode } \n" +
            " \n function OnDelete(meta) {  \n :onDeleteCode  \n }  \n";

    public EventingFunction mapTo(Rule rule) {
        return EventingFunction.builder()
                .appname(rule.getName())
                .appcode(extractFunctionCode(rule))
                .usingTimer(false)
                .depcfg(extractConfig(rule))
                .settings(extractSettings(rule))
                .build();
    }

    private EventingFunction.Settings extractSettings(Rule rule) {
        return EventingFunction.Settings.builder().description(rule.getDescription()).build();
    }

    private EventingFunction.Config extractConfig(Rule rule) {
        return EventingFunction.Config.builder().metadataBucket(rule.getMetadataBucket())
                .sourceBucket(rule.getSourceBucket())
                .buckets(Collections.singletonList(EventingFunction.BucketInfo.builder().bucketName(rule.getTargetBucket()).alias("tgt").build()))
                .build();
    }

    private String extractFunctionCode(Rule rule) {
        String code = TEMPLATE_CODE;
        String onUpdate = extractOnUpdateCode(rule);
        String onDelete = extractOnDeleteCode(rule);
        return code.replace(":onDeleteCode", onDelete).replace(":onUpdateCode", onUpdate);
    }

    private String extractOnDeleteCode(Rule rule) {
        //TODO
        return "";
    }

    private String extractOnUpdateCode(Rule rule) {
        String code = "";
        if(rule.getActions().isEmpty()) {
            throw new IllegalArgumentException("Missing `actions` into the rule");
        }

        code = extractActionsCode(rule.getActions());

        if(rule.getPredicates()!=null && !rule.getPredicates().isEmpty()) {
            code = "if ("+extractPredicatesFilters(rule.getPredicates())+") {\n ".concat(code).concat(" \n } \n ");
        }
        return code;
    }

    private String extractActionsCode(List<Rule.ActionRule> actions) {
        return actions.stream().map(Rule.ActionRule::getCode).collect(Collectors.joining(" \n   "));
    }

    private String extractPredicatesFilters(List<Rule.FilterRule> predicates) {
        return predicates.stream().map(Rule.FilterRule::toFilterExpression).collect(Collectors.joining(" && "));
    }

}