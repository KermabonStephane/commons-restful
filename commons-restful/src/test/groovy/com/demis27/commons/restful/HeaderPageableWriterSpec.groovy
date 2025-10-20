package com.demis27.commons.restful

import spock.lang.Specification

class HeaderPageableWriterSpec extends Specification {

    def 'We write a Range Header'() {
        when: 'We write a Range Header'
        def header = headerPageable.toRangeHeader()

        then: 'The header is well'
        header == expectedHeader

        where:
        headerPageable                              || expectedHeader
        new HeaderPageable("elements", 0, 10, 100L) || "Range: elements=0-9"
        new HeaderPageable("elements", 0, 10, 10L)  || "Range: elements=0-9"
        new HeaderPageable("elements", 1, 10, 100L) || "Range: elements=10-19"
        new HeaderPageable("elements", 0, 10, 9L)   || "Range: elements=0-8"
    }

    def 'We write a Range Header with or without header name'() {
        when: 'We write a Range Header'
        def header = headerPageable.toRangeHeader(withHeaderName)

        then: 'The header is well'
        header == expectedHeader

        where:
        headerPageable                              | withHeaderName || expectedHeader
        new HeaderPageable("elements", 0, 10, 100L) | true           || "Range: elements=0-9"
        new HeaderPageable("elements", 0, 10, 100L) | false          || "elements=0-9"
    }

    def 'We write a Content-Range Header'() {
        when: 'We write a Content-Range Header'
        def header = headerPageable.toContentRangeHeader()

        then: 'The header is well'
        header == expectedHeader

        where:
        headerPageable                              || expectedHeader
        new HeaderPageable("elements", 0, 10, 100L) || "Content-Range: elements 0-9/100"
        new HeaderPageable("elements", 1, 10, 100L) || "Content-Range: elements 10-19/100"
        new HeaderPageable("elements", 1, 10, 15L)  || "Content-Range: elements 10-14/15"
    }

    def 'We write a Content-Range Header with or without header name'() {
        when: 'We write a Content-Range Header'
        def header = headerPageable.toContentRangeHeader(includeHeaderName)

        then: 'The header is well'
        header == expectedHeader

        where:
        headerPageable                              | includeHeaderName || expectedHeader
        new HeaderPageable("elements", 0, 10, 100L) | true              || "Content-Range: elements 0-9/100"
        new HeaderPageable("elements", 0, 10, 100L) | false             || "elements 0-9/100"
    }

    def 'We write a Accept-Ranges Header'() {
        when: 'We write a Accept-Ranges Header'
        def header = headerPageable.toAcceptRangesHeader()

        then: 'The header is well'
        header == expectedHeader

        where:
        headerPageable                              || expectedHeader
        new HeaderPageable("elements", 0, 10, 100L) || "Accept-Ranges: elements"
    }
}
