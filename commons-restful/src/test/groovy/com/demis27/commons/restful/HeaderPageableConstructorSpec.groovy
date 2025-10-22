package com.demis27.commons.restful

import spock.lang.Specification
import spock.lang.Unroll

class HeaderPageableConstructorSpec extends Specification {

    @Unroll
    def "Test valid constructor arguments: page=#page, size=#size, total=#total"() {
        when:
        new HeaderPageable("elements", page, size, total)

        then:
        noExceptionThrown()

        where:
        page | size | total
        -1   | -1   | -1L
        0    | 10   | -1L
        1    | 1    | 0L
        -1   | 10   | 100L
    }

    @Unroll
    def "Test invalid page argument: #page"() {
        when:
        new HeaderPageable("elements", page, 10, 100L)

        then:
        thrown(IllegalArgumentException)

        where:
        page << [-2, -100]
    }

    @Unroll
    def "Test invalid size argument: #size"() {
        when:
        new HeaderPageable("elements", 1, size, 100L)

        then:
        thrown(IllegalArgumentException)

        where:
        size << [0, -2, -10]
    }

    @Unroll
    def "Test invalid total argument: #total"() {
        when:
        new HeaderPageable("elements", 1, 10, total)

        then:
        thrown(IllegalArgumentException)

        where:
        total << [-2L, -100L]
    }
}
