package com.demis27.commons.restfull

import spock.lang.Specification
import spock.lang.Unroll

class SortParserSpec extends Specification {

    def parser = new Sort.SortParser()

    def 'parse a simple sorts string'() {
        given:
        def sortsAsString = "firstName:asc,lastName:desc"

        when:
        def sorts = parser.parse(sortsAsString)

        then:
        sorts.size() == 2
        sorts.get(0) == new Sort("firstName", Sort.SortOrder.ASC)
        sorts.get(1) == new Sort("lastName", Sort.SortOrder.DESC)
    }

    @Unroll
    def 'parse a single sort: #sortsAsString'() {
        when:
        def sorts = parser.parse(sortsAsString)

        then:
        sorts.size() == 1
        sorts.get(0) == expectedSort

        where:
        sortsAsString  | expectedSort
        "firstName"      | new Sort("firstName", Sort.SortOrder.ASC)
        "firstName:asc"  | new Sort("firstName", Sort.SortOrder.ASC)
        "firstName:ASC"  | new Sort("firstName", Sort.SortOrder.ASC)
        "firstName:desc" | new Sort("firstName", Sort.SortOrder.DESC)
        "firstName:DESC" | new Sort("firstName", Sort.SortOrder.DESC)
    }

    def 'parse sort without order'() {
        given:
        def sortsAsString = "firstName,lastName"

        when:
        def sorts = parser.parse(sortsAsString)

        then:
        sorts.size() == 2
        sorts.get(0) == new Sort("firstName", Sort.SortOrder.ASC)
        sorts.get(1) == new Sort("lastName", Sort.SortOrder.ASC)
    }

    def 'parse a complex valid sorts string with spaces'() {
        when:
        def sorts = parser.parse("  firstName:asc  ,  lastName:DESC  ,  age  ")

        then:
        sorts.size() == 3
        sorts.get(0) == new Sort("firstName", Sort.SortOrder.ASC)
        sorts.get(1) == new Sort("lastName", Sort.SortOrder.DESC)
        sorts.get(2) == new Sort("age", Sort.SortOrder.ASC)
    }

    @Unroll
    def 'parse throw an IllegalArgumentException for: #sortsAsString'() {
        when:
        parser.parse(sortsAsString)

        then:
        def e = thrown(IllegalArgumentException)
        e.message == "Bad format of the sorts string '$sortsAsString'"

        where:
        sortsAsString << ["firstname:toto", "firstname:asc,lastname:bad", ",firstname", "firstname,", "firstname,,lastname", ":asc", " , "]
    }
}
