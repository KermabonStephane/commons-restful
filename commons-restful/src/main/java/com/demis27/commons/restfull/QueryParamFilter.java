package com.demis27.commons.restfull;

import groovy.transform.builder.Builder;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a filter criterion for a query.
 * A filter is composed of a `property`, an `operator`, and a `values`.
 * For example, to filter for users with the name "John", you would use:
 * new Filter("name", Filter.FilterOperator.EQUALS, "John");
 * This feature is in progress.
 *
 * @param property The name of the property to filter on.
 * @param operator The operator to use for the comparison.
 * @param values   The list of values to compare against.
 */
@Builder
public record QueryParamFilter(String property, FilterOperator operator, List<String> values) {

    /**
     * The supported filter operators.
     */
    public enum FilterOperator {
        /**
         * Represents an equality comparison (e.g., `property eq values`).
         */
        EQUALS,
        /**
         * Represents a "greater than" comparison (e.g., `property gt values`).
         */
        GREATER,
        /**
         * Represents a "greater than or equal to" comparison (e.g., `property gte values`).
         */
        GREATER_OR_EQUALS,
        /**
         * Represents a "less than" comparison (e.g., `property lt values`).
         */
        LESS,
        /**
         * Represents a "less than or equal to" comparison (e.g., `property lte values`).
         */
        LESS_OR_EQUALS,
        /**
         * Represents a "not equal to" comparison (e.g., `property ne values`).
         */
        NOT_EQUALS,
        /**
         * Represents an "in" comparison (e.g., `property in values`).
         */
        IN,
        /**
         * Represents a "like" comparison (e.g., `property like values`).
         */
        LIKE
    }

    /**
     * Parses a filter string into a list of `Filter` objects.
     * The filter string should be a comma-separated list of individual filters.
     * Each individual filter should be in the format: `property operator values`.
     * For example:
     * "name eq John,age gt 25"
     *
     * @param filterString The filter string to parse.
     * @return A list of `Filter` objects.
     * @throws IllegalArgumentException if the filter string is null, blank, or invalid.
     */
    public static List<QueryParamFilter> parse(String filterString) {
        if (filterString == null || filterString.isBlank()) {
            throw new IllegalArgumentException("Filter string cannot be null or blank.");
        }
        String[] filterParts = filterString.split(",");

        return Arrays.stream(filterParts)
                .map(QueryParamFilter::parseSingleFilter)
                .toList();
    }

    /**
     * Parses a single filter string into a `Filter` object.
     *
     * @param filterString The single filter string to parse (e.g., "name eq John").
     * @return A `Filter` object.
     * @throws IllegalArgumentException if the filter string is null, blank, or invalid.
     */
    private static QueryParamFilter parseSingleFilter(String filterString) {
        if (filterString == null || filterString.isBlank()) {
            throw new IllegalArgumentException("Filter part cannot be null or blank");
        }

        String[] parts = filterString.split(" ");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid filter format for: '" + filterString + "'. Expected format is property operator values");
        }

        String property = parts[0];
        String operatorString = parts[1];
        String value = parts[2];

        if (property.isBlank() || operatorString.isBlank() || value.isBlank()) {
            throw new IllegalArgumentException("Property, operator, and values cannot be blank in filter: '" + filterString + "'");
        }

        FilterOperator operator = switch (operatorString) {
            case "eq" -> FilterOperator.EQUALS;
            case "gt" -> FilterOperator.GREATER;
            case "gte" -> FilterOperator.GREATER_OR_EQUALS;
            case "lt" -> FilterOperator.LESS;
            case "lte" -> FilterOperator.LESS_OR_EQUALS;
            case "like" -> FilterOperator.LIKE;
            case "in" -> FilterOperator.IN;
            case "ne" -> FilterOperator.NOT_EQUALS;
            default -> throw new IllegalArgumentException("Unknown operator: " + operatorString);
        };

        return new QueryParamFilter(property, operator, List.of(value));
    }
}
