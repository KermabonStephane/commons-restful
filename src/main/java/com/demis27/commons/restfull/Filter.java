package com.demis27.commons.restfull;

import groovy.transform.builder.Builder;

@Builder
public record Filter(String property, FilterOperator operator, String value) {
}
