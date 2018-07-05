package com.twinsickle.aem.utils.query;


import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.SearchResult;
import com.twinsickle.aem.utils.resource.AdapterUtil;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.Session;
import java.util.Map;
import java.util.Optional;

public final class PredicateQueryBuilder {
    private PredicateQueryBuilder(){}

    public static PredicateQuery build(Map<String, String> predicates, ResourceResolver resolver){
        return new PredicateQuery(predicates, resolver);
    }

    public static class PredicateQuery {
        private PredicateGroup predicateGroup;
        private ResourceResolver resolver;

        private PredicateQuery(Map<String, String> predicates, ResourceResolver resolver){
            this.predicateGroup = PredicateGroup.create(predicates);
            this.resolver = resolver;
        }

        public Optional<SearchResult> apply(){
            return AdapterUtil.adaptTo(resolver, QueryBuilder.class)
                    .flatMap(this::build)
                    .map(Query::getResult);
        }

        private Optional<Query> build(QueryBuilder queryBuilder){
            return AdapterUtil.adaptTo(resolver, Session.class)
                    .map(session -> queryBuilder.createQuery(predicateGroup, session));
        }
    }
}
