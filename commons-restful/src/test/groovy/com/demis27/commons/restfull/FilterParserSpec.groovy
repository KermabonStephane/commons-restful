package com.demis27.commons.restfull

import spock.lang.Specification
import spock.lang.Unroll

class FilterParserSpec extends Specification {

    def parser = new Filter.FilterParser()

    @Unroll
    def "Should parse a single filter with operator '#operator'"() {
        given: "A filter string for operator #operator"
        def filterString = "property ${operator} value"

        when: "The string is parsed"
        def filters = parser.parse(filterString)

        then: "A single Filter object is created with the correct operator"
        filters.size() == 1
        filters[0].property == "property"
        filters[0].operator == expectedOperator
        filters[0].value == "value"

        where:
        operator | expectedOperator
        "eq"     | Filter.FilterOperator.EQUALS
        "gt"     | Filter.FilterOperator.GREATER
        "gte"    | Filter.FilterOperator.GREATER_OR_EQUALS
        "lt"     | Filter.FilterOperator.LESS
        "lte"    | Filter.FilterOperator.LESS_OR_EQUALS
        "like" | Filter.FilterOperator.LIKE
    }

    def "Should parse multiple filters"() {
        given: "A filter string with two filters"
        def filterString = "firstname eq John,lastname eq Doe"

        when: "The string is parsed"
        def filters = parser.parse(filterString)

        then: "Two Filter objects are created"
        filters.size() == 2
        filters[0].property == "firstname"
        filters[0].operator == Filter.FilterOperator.EQUALS
        filters[0].value == "John"
        filters[1].property == "lastname"
        filters[1].operator == Filter.FilterOperator.EQUALS
        filters[1].value == "Doe"
    }

    @Unroll
    def "Should throw exception for invalid filter string '#filterString'"() {
        when: "An invalid string is parsed"
        parser.parse(filterString)

        then: "An IllegalArgumentException is thrown"
        thrown(IllegalArgumentException)

        where:
        filterString << [
                null,
                "",
                " ",
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
