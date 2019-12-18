package com.couchbase.demo.eventing;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class EventingFunction {
    private String appcode;
    private Config depcfg;
    private String version;
    private Long handleruuid;
    private Long id;
    private String appname;
    private Settings settings;
    private boolean usingTimer;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Config {
        private List<BucketInfo> buckets;
        private String metadataBucket;
        private String sourceBucket;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class BucketInfo {
        private String alias;
        private String bucketName;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
    public static class Settings {
        @Builder.Default
        private Boolean cleanupTimers = false;
        @Builder.Default
        private String dcpStreamBoundary= "everything";
        @Builder.Default
        private Long deadlineTimeout = 62L;
        @Builder.Default
        private Boolean deploymentStatus=false;
        private String description;
        @Builder.Default
        private Long executionTimeout=60L;
        @Builder.Default
        private String logLevel = "INFO"; // TODO check the LogLevel enum that matches here
        @Builder.Default
        private Boolean processingStatus=false;
        @Builder.Default
        private String userPrefix="eventing";
        @Builder.Default
        private Boolean usingTimer=false;
        @Builder.Default
        private Integer workerCount=3;
    }


}
