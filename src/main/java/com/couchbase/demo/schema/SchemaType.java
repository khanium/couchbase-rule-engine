package com.couchbase.demo.schema;

import com.couchbase.client.java.repository.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchemaType {
    static final String PREFIX = "schema";
    static final String DELIMITER = ":";

    @Id
    private String id;
    private String type;
    private int total;
    private String bucket;
    @Builder.Default
    private List<PropertySchema> properties = new ArrayList<>();

    public static SchemaType from(String bucket, String type, List<PropertySchema> properties) {
        return builder().bucket(bucket)
                .type(type)
                .id(PREFIX + DELIMITER + bucket + DELIMITER + type)
                .properties(properties)
                .total(calculateTotal(properties))
                .build();
    }

    private static int calculateTotal(List<PropertySchema> properties) {
        return properties.stream().findFirst()
                .map(propertySchema -> (int) (propertySchema.getOccurrences() + ((100.0 - propertySchema.getPercentage()) / 100.0) * propertySchema.getOccurrences()))
                .orElse(0);
    }

}
