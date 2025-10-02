package com.demis27.commons.restfull;

import groovy.transform.builder.Builder;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Builder
public record Filter(String property, FilterOperator operator, String value) {
    public enum FilterOperator {

        EQUALS,GREATER,GREATER_OR_EQUALS,LESS,LESS_OR_EQUALS
    }

    public static class FilterParser {

        public List<Filter> parse(String filterString) {
            if (filterString == null || filterString.isBlank()) {
                throw new IllegalArgumentException("Filter string cannot be null or blank.");
            }
            String[] filterParts = filterString.split(",");

            return Arrays.stream(filterParts)
                .map(this::parseSingleFilter)
                .collect(Collectors.toList());
        }

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
                default -> throw new IllegalArgumentException("Unknown operator: " + operatorString);
            };

            return new Filter(property, operator, value);
        }
    }
}
