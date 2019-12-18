package com.couchbase.demo.schema;

import com.couchbase.client.java.query.N1qlQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.couchbase.core.CouchbaseTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.couchbase.demo.schema.PropertySchema.PropertyType.OBJECT;
import static java.lang.String.format;

@Service
public class InferSchemaService {
    private final Logger LOGGER = LoggerFactory.getLogger(InferSchemaService.class);
    private final String FIND_ALL_BY_TYPE = "SELECT CONCAT('property:',b.name) as `_ID`, 0 as `_CAS`, b.name , count(distinct a.docId) occurrences, count(1) total, \n " +
            "       ARRAY_AGG(DISTINCT b.val)[1:5] samples, \n " +
            "       MAX(REPLACE(TYPE(b.val),\"null\", \"_null\")) as propertyType, \n " +
            "       (count(distinct a.docId)/count(1))*100.0 as percentage \n " +
            "  FROM (\n " +
            "       SELECT  meta().id as docId, OBJECT_PAIRS(%s) properties \n " +
            "         FROM %s t \n " +
            "        WHERE t.`%s` = %s \n " +
            "        LIMIT 10000 ) a UNNEST a.properties b \n " +
            "GROUP BY b.name \n " +
            "ORDER BY percentage DESC, b.name ";
    private final CouchbaseTemplate couchbaseTemplate;
    @Value("${spring.couchbase.document.type}")
    private String docType;

    @Autowired
    public InferSchemaService(CouchbaseTemplate couchbaseTemplate) {
        this.couchbaseTemplate = couchbaseTemplate;
    }

    public Mono<SchemaType> infer(String bucketname, String type) {
        LOGGER.debug("infering `{}`...", type);
        return findAllByCustomType(bucketname, docType, type)
                .flatMap(prop -> inferObject(bucketname, type, prop))
                .log()
                .collectList()
                .map(properties -> SchemaType.from(bucketname, type, properties));
    }

    private Mono<PropertySchema> inferObject(String bucketname, String type, PropertySchema prop) {
        LOGGER.debug("infering `{}`...", type);
        return !OBJECT.equals(prop.getType()) ? Mono.just(prop) :
                findAllByCustomType(bucketname, docType, type, "t." + prop.getPropertyFullname())
                        .flatMap(p -> {
                            p.setParent(prop);
                            return inferObject(bucketname, type, p);
                        })
                        .collectList()
                        .map(p -> {
                            prop.setProperties(p);
                            return prop;
                        });
    }


    private Flux<PropertySchema> findAllByCustomType(String bucketname, String docType, String typeValue) {
        return findAllByCustomType(bucketname, docType, typeValue, "t");
    }

    private Flux<PropertySchema> findAllByCustomType(String bucketname, String docType, String typeValue, String path) {
        N1qlQuery statement = N1qlQuery.simple(format(FIND_ALL_BY_TYPE, path, bucketname, docType, stringQuote(typeValue)));
        LOGGER.info("Query: {}", statement.n1ql().toString());
        return Flux.fromIterable(couchbaseTemplate.findByN1QL(statement, PropertySchema.class));
    }

    private String stringQuote(String fieldStringValue) {
        return "\"" + fieldStringValue + "\"";
    }


}
