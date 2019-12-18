package com.couchbase.demo.schema;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@ToString(of = {"name", "propertyType", "properties"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class PropertySchema implements Serializable {

    @JsonManagedReference
    private PropertySchema parent;
    private String name;
    private int occurrences;
    @Builder.Default
    private List samples = new ArrayList();
    private String propertyType;
    private float percentage;
    @Builder.Default
    @JsonBackReference
    private List<PropertySchema> properties = new ArrayList<>();

    private static String quote(String name) {
        return "`".concat(name).concat("`");
    }

    @Id
    public String getPropertyFullname() {
        return parent == null ? quote(getName()) : parent.getPropertyFullname().concat(".").concat(quote(getName()));
    }

    @Transient
    @JsonIgnore
    public PropertyType getType() {
        return propertyType == null || propertyType.isEmpty() ? null : PropertyType.valueOf(propertyType.replace("_", "").toUpperCase());
    }

    enum PropertyType {
        @JsonProperty("_null")
        NULL,
        @JsonProperty("_missing")
        MISSING,
        @JsonProperty("string")
        STRING,
        @JsonProperty("number")
        NUMBER,
        @JsonProperty("boolean")
        BOOLEAN,
        @JsonProperty("array")
        ARRAY,
        @JsonProperty("object")
        OBJECT,
        @JsonProperty("binary")
        BINARY
    }
}
