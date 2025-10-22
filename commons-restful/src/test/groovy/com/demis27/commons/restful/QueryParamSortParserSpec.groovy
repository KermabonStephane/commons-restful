package com.demis27.commons.restful

import spock.lang.Specification
import spock.lang.Unroll

class QueryParamSortParserSpec extends Specification {


    def 'parse a simple sorts string'() {
        given:
        def sortsAsString = "firstName:asc,lastName:desc"

        when:
        def sorts = QueryParamSort.parse(sortsAsString)

        then:
        sorts.size() == 2
        sorts.get(0) == new QueryParamSort("firstName", QueryParamSort.SortOrder.ASC)
        sorts.get(1) == new QueryParamSort("lastName", QueryParamSort.SortOrder.DESC)
    }

    @Unroll
    def 'parse a single sort: #sortsAsString'() {
        when:
        def sorts = QueryParamSort.parse(sortsAsString)

        then:
        sorts.size() == 1
        sorts.get(0) == expectedSort

        where:
        sortsAsString  | expectedSort
        "firstName"       | new QueryParamSort("firstName", QueryParamSort.SortOrder.ASC)
        "firstName:asc"   | new QueryParamSort("firstName", QueryParamSort.SortOrder.ASC)
        "firstName:ASC"   | new QueryParamSort("firstName", QueryParamSort.SortOrder.ASC)
        "firstName:desc"  | new QueryParamSort("firstName", QueryParamSort.SortOrder.DESC)
        "firstName:DESC"  | new QueryParamSort("firstName", QueryParamSort.SortOrder.DESC)
        "first_name:DESC" | new QueryParamSort("first_name", QueryParamSort.SortOrder.DESC)
        "addressLine1:DESC" | new QueryParamSort("addressLine1", QueryParamSort.SortOrder.DESC)
    }

    def 'parse sort without order'() {
        given:
        def sortsAsString = "firstName,lastName"

        when:
        def sorts = QueryParamSort.parse(sortsAsString)

        then:
        sorts.size() == 2
        sorts.get(0) == new QueryParamSort("firstName", QueryParamSort.SortOrder.ASC)
        sorts.get(1) == new QueryParamSort("lastName", QueryParamSort.SortOrder.ASC)
    }

    def 'parse a complex valid sorts string with spaces'() {
        when:
        def sorts = QueryParamSort.parse("  firstName:asc  ,  lastName:DESC  ,  age  ")

        then:
        sorts.size() == 3
        sorts.get(0) == new QueryParamSort("firstName", QueryParamSort.SortOrder.ASC)
        sorts.get(1) == new QueryParamSort("lastName", QueryParamSort.SortOrder.DESC)
        sorts.get(2) == new QueryParamSort("age", QueryParamSort.SortOrder.ASC)
    }

    @Unroll
    def 'parse throw an IllegalArgumentException for: #sortsAsString'() {
        when:
        QueryParamSort.parse(sortsAsString)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Bad format of the sorts string '$sortsAsString'"

        where:
        sortsAsString << [null, "", " ", "firstname:toto", "firstname:asc,lastname:bad", ",firstname", "firstname,", "firstname,,lastname", ":asc", " , "]
    }
}
