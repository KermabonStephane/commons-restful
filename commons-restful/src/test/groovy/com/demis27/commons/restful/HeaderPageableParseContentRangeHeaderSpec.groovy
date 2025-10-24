package com.demis27.commons.restful

import spock.lang.Specification

class HeaderPageableParseContentRangeHeaderSpec extends Specification {

    def 'We parse a Content-Range Header'() {
        when: 'We parse a Content-Range Header'
        def headerPageable = HeaderPageable.parseContentRangeHeader(header)

        then: 'Header is correctly parse'
        headerPageable.page() == page
        headerPageable.size() == size
        headerPageable.total() == total
        headerPageable.elementName() == elementName

        where: 'We parse header with different values'
        header                                 || page | size | total | elementName
        'Content-Range: elements 0-9/100'      || 1    | 10   | 100   | 'elements'
        'Content-Range: records 10-19/200'     || 2    | 10   | 200   | 'records'
        'Content-Range: firstNames 10-19/200'  || 2    | 10   | 200   | 'firstNames'
        'Content-Range: first_names 10-19/200' || 2    | 10   | 200   | 'first_names'
        'Content-Range: first-names 10-19/200' || 2    | 10   | 200   | 'first-names'
    }

    def 'We parse a null or empty Content-Range Header'() {
        when: 'We parse a null or empty Content-Range Header'
        HeaderPageable.parseContentRangeHeader(header)

        then: 'An exception is thrown'
        def e = thrown(IllegalArgumentException)
        e.message == "Header cannot be null or empty"

        where:
        header << [null, ""]
    }

    def 'We parse a Content-Range header with a bad format'() {
        when: 'We parse a Content-Range header with a bad format'
        HeaderPageable.parseContentRangeHeader(header)

        then: 'An exception is thrown'
        def e = thrown(IllegalArgumentException)
        e.message == "Header '%s' is not in the correct format. The format must be like 'Content-Range: elements 0-9/100'".formatted(header)

        where: 'Header with bad format'
        header << ['Content-Range:', "element 0-9"]
    }

    def 'We parse a Content-Range header with a bad range'() {
        when: 'We parse a Content-Range header with a bad range'
        HeaderPageable.parseContentRangeHeader(header)

        then: 'An exception is thrown'
        def e = thrown(IllegalArgumentException)
        e.message == "Header '%s' is not in the correct format. The end must be greater than the start".formatted(header)

        where: 'Header with bad range'
        header << ['Content-Range: elements 10-0/100', 'Content-Range: elements 10-9/100']
    }
}
