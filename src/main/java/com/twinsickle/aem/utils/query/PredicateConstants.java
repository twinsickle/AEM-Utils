package com.twinsickle.aem.utils.query;

public final class PredicateConstants {
    private PredicateConstants(){}

    public static final String PATH = "path";
    public static final String TYPE = "type";

    public static final String PROPERTY = "property";
    public static final String PROPERTY_VALUE = "property.value";

    public static final String PREDICATE_GROUP = "group.";
    public static final String GROUP_OR = "p.or";
    public static final String GROUP_AND = "p.and";

    public static final String NODE_NAME = "nodename";

    public static final String DATE_RANGE_PROPERTY = "daterange.property";
    public static final String DATE_RANGE_LOWER_BOUND = "daterange.lowerBound";
    public static final String DATE_RANGE_LOWER_OPERATION = "daterange.lowerOperation";
    public static final String DATE_RANGE_UPPER_BOUND = "daterange.upperBound";
    public static final String DATE_RANGE_UPPER_OPERATION = "daterange.upperOperation";

    public static final String OPERATION_GREATER_THAN = ">";
    public static final String OPERATION_GREATER_THAN_EQUALS = ">=";
    public static final String OPERATION_LESS_THAN = "<";
    public static final String OPERATION_LESS_THAN_EQUALS = "<=";

    public static final String FULL_TEXT = "fulltext";
}
