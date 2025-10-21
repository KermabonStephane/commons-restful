package com.demis27.commons.restful

import spock.lang.Specification
import spock.lang.Unroll

class QueryParamFilterParserSpec extends Specification {

    @Unroll
    def "Should parse a single filter with operator '#operator'"() {
        given: "A filter string for operator #operator"
        def filterString = "property ${operator} values"

        when: "The string is parsed"
        def filters = QueryParamFilter.parse(filterString)

        then: "A single Filter object is created with the correct operator"
        filters.size() == 1
        filters[0].property == "property"
        filters[0].operator == expectedOperator
        filters[0].values[0] == "values"

        where:
        operator | expectedOperator
        "eq"   | QueryParamFilter.FilterOperator.EQUALS
        "gt"   | QueryParamFilter.FilterOperator.GREATER
        "gte"  | QueryParamFilter.FilterOperator.GREATER_OR_EQUALS
        "lt"   | QueryParamFilter.FilterOperator.LESS
        "lte"  | QueryParamFilter.FilterOperator.LESS_OR_EQUALS
        "like" | QueryParamFilter.FilterOperator.LIKE
        "ne"   | QueryParamFilter.FilterOperator.NOT_EQUALS
        "in"   | QueryParamFilter.FilterOperator.IN
    }

    def "Should parse multiple filters"() {
        given: "A filter string with two filters"
        def filterString = "firstname eq John,lastname eq Doe"

        when: "The string is parsed"
        def filters = QueryParamFilter.parse(filterString)

        then: "Two Filter objects are created"
        filters.size() == 2
        filters[0].property == "firstname"
        filters[0].operator == QueryParamFilter.FilterOperator.EQUALS
        filters[0].values[0] == "John"
        filters[1].property == "lastname"
        filters[1].operator == QueryParamFilter.FilterOperator.EQUALS
        filters[1].values[0] == "Doe"
    }

    @Unroll
    def "Should throw exception for invalid filter string '#filterString'"() {
        when: "An invalid string is parsed"
        QueryParamFilter.parse(filterString)

        then: "An IllegalArgumentException is thrown"
        thrown(IllegalArgumentException)

        where:
        filterString << [
                null,
                "",
                " ",
                " , ",
                "firstname eq ",
                "firstname eq John extra",
                "firstname foo John",
                " eq John",
                "firstname  John",
                "firstname eq ",
                "firstname eq John,   "
        ]
    }
}
