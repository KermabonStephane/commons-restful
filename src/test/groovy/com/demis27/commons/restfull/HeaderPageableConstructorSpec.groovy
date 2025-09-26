package com.demis27.commons.restfull

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
        0L   | 10L  | 100L
        1L   | 1L   | 0L
        -1L  | -1L  | -1L
        0L   | 10L  | -1L
        -1L  | 10L  | 100L
    }

    @Unroll
    def "Test invalid page argument: #page"() {
        when:
        new HeaderPageable("elements", page, 10L, 100L)

        then:
        thrown(IllegalArgumentException)

        where:
        page << [-2L, -100L]
    }

    @Unroll
    def "Test invalid size argument: #size"() {
        when:
        new HeaderPageable("elements", 1L, size, 100L)

        then:
        thrown(IllegalArgumentException)

        where:
        size << [0L, -2L, -10L]
    }

    @Unroll
    def "Test invalid total argument: #total"() {
        when:
        new HeaderPageable("elements", 1L, 10L, total)

        then:
        thrown(IllegalArgumentException)

        where:
        total << [-2L, -100L]
    }
}
