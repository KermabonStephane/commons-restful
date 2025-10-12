package com.demis27.commons.restfull;

import groovy.transform.builder.Builder;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a filter criterion for a query.
 * A filter is composed of a `property`, an `operator`, and a `value`.
 * For example, to filter for users with the name "John", you would use:
 * new Filter("name", Filter.FilterOperator.EQUALS, "John");
 *
 * @param property The name of the property to filter on.
 * @param operator The operator to use for the comparison.
 * @param value The value to compare against.
 */
@Builder
public record Filter(String property, FilterOperator operator, String value) {

    /**
     * The supported filter operators.
     */
    public enum FilterOperator {
        /**
         * Represents an equality comparison (e.g., `property eq value`).
         */
        EQUALS,
        /**
         * Represents a "greater than" comparison (e.g., `property gt value`).
         */
        GREATER,
        /**
         * Represents a "greater than or equal to" comparison (e.g., `property gte value`).
         */
        GREATER_OR_EQUALS,
        /**
         * Represents a "less than" comparison (e.g., `property lt value`).
         */
        LESS,
        /**
         * Represents a "less than or equal to" comparison (e.g., `property lte value`).
         */
        LESS_OR_EQUALS,
        LIKE
    }

    /**
     * A parser for creating a list of `Filter` objects from a string.
     */
    public static class FilterParser {

        /**
         * Parses a filter string into a list of `Filter` objects.
         * The filter string should be a comma-separated list of individual filters.
         * Each individual filter should be in the format: `property operator value`.
         * For example:
         * "name eq John,age gt 25"
         *
         * @param filterString The filter string to parse.
         * @return A list of `Filter` objects.
         * @throws IllegalArgumentException if the filter string is null, blank, or invalid.
         */
        public List<Filter> parse(String filterString) {
            if (filterString == null || filterString.isBlank()) {
                throw new IllegalArgumentException("Filter string cannot be null or blank.");
            }
            String[] filterParts = filterString.split(",");

            return Arrays.stream(filterParts)
                .map(this::parseSingleFilter)
                    .toList();
        }

        /**
         * Parses a single filter string into a `Filter` object.
         *
         * @param filterString The single filter string to parse (e.g., "name eq John").
         * @return A `Filter` object.
         * @throws IllegalArgumentException if the filter string is null, blank, or invalid.
         */
        private Filter parseSingleFilter(String filterString) {
            if (filterString == null || filterString.isBlank()) {
                throw new IllegalArgumentException("Filter part cannot be null or blank");
            }

            String[] parts = filterString.split(" ");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid filter format for: '" + filterString + "'. Expected format is property operator value");
            }

            String property = parts[0];
            String operatorString = parts[1];
            String value = parts[2];

            if (property.isBlank() || operatorString.isBlank() || value.isBlank()) {
                throw new IllegalArgumentException("Property, operator, and value cannot be blank in filter: '" + filterString + "'");
            }

            FilterOperator operator = switch (operatorString) {
                case "eq" -> FilterOperator.EQUALS;
                case "gt" -> FilterOperator.GREATER;
                case "gte" -> FilterOperator.GREATER_OR_EQUALS;
                case "lt" -> FilterOperator.LESS;
                case "lte" -> FilterOperator.LESS_OR_EQUALS;
                case "like" -> FilterOperator.LIKE;
                default -> throw new IllegalArgumentException("Unknown operator: " + operatorString);
            };

            return new Filter(property, operator, value);
        }
    }
}
