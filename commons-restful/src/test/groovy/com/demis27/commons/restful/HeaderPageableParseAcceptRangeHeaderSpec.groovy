package com.demis27.commons.restful

import spock.lang.Specification

class HeaderPageableParseAcceptRangeHeaderSpec extends Specification {

    def 'We parse a Accept-Range Header'() {
        when: 'We parse a Accept-Range Header'
        def headerPageable = HeaderPageable.parseAcceptRangesHeader(header)

        then: 'Header is correctly parse'
        headerPageable.elementName() == elementName

        where: 'We parse header with different values'
        header                      || elementName
        'Accept-Ranges: elements'   || 'elements'
        'Accept-Ranges: records'    || 'records'
        'Accept-Ranges: firstNames' || 'firstNames'
        'Accept-Ranges: first_names' || 'first_names'
        'Accept-Ranges: first-names' || 'first-names'
    }

    def 'We parse a null or empty Accept-Range Header'() {
        when: 'We parse a null or empty Accept-Range Header'
        HeaderPageable.parseAcceptRangesHeader(header)

        then: 'An exception is thrown'
        def e = thrown(IllegalArgumentException)
        e.message == "Header cannot be null or empty"

        where:
        header << [null, ""]
    }

    def 'We parse a Accept-Range header with a bad format'() {
        when: 'We parse a Accept-Range header with a bad format'
        HeaderPageable.parseAcceptRangesHeader(header)

        then: 'An exception is thrown'
        def e = thrown(IllegalArgumentException)
        e.message == "Header '%s' is not in the correct format. The format must be like 'Accept-Ranges: elements'".formatted(header)

        where: 'Header with bad format'
        header << ['Accept-Ranges:', ": elementsName"]
    }
}
