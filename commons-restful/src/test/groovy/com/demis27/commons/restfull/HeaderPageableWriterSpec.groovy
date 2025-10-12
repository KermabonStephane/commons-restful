package com.demis27.commons.restfull

import spock.lang.Specification

class HeaderPageableWriterSpec extends Specification {

    def 'We write a Range Header'() {
        when: 'We write a Range Header'
        def header = headerPageable.toRangeHeader()

        then: 'The header is well'
        header == expectedHeader

        where:
        headerPageable                                || expectedHeader
        new HeaderPageable("elements", 0L, 10L, 100L) || "Range: elements=0-9"
        new HeaderPageable("elements", 0L, 10L, 10L)  || "Range: elements=0-9"
        new HeaderPageable("elements", 1L, 10L, 100L) || "Range: elements=10-19"
        new HeaderPageable("elements", 0L, 10L, 9L)   || "Range: elements=0-8"
    }

    def 'We write a Content-Range Header'() {
        when: 'We write a Content-Range Header'
        def header = headerPageable.toContentRangeHeader()

        then: 'The header is well'
        header == expectedHeader

        where:
        headerPageable                                || expectedHeader
        new HeaderPageable("elements", 0L, 10L, 100L) || "Content-Range: elements 0-9/100"
        new HeaderPageable("elements", 1L, 10L, 100L) || "Content-Range: elements 10-19/100"
        new HeaderPageable("elements", 1L, 10L, 15L)  || "Content-Range: elements 10-14/15"
    }

    def 'We write a Accept-Ranges Header'() {
        when: 'We write a Accept-Ranges Header'
        def header = headerPageable.toAcceptRangesHeader()

        then: 'The header is well'
        header == expectedHeader

        where:
        headerPageable                                || expectedHeader
        new HeaderPageable("elements", 0L, 10L, 100L) || "Accept-Ranges: elements"
    }
}
