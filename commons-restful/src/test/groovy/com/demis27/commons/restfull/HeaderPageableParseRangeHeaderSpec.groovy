package com.demis27.commons.restfull

import spock.lang.Specification

class HeaderPageableParseRangeHeaderSpec extends Specification {

    def 'We parse a Range Header'() {
        when: 'We parse a Range Header'
        def headerPageable = HeaderPageable.parseRangeHeader(header)

        then: 'Header is correctly parse'
        headerPageable.page() == page
        headerPageable.size() == size
        headerPageable.elementName() == elementName

        where: 'We parse header with different values'
        header                   || page | size | elementName
        'Range: elements=0-9'    || 0    | 10   | 'elements'
        'elements=0-9' || 0 | 10 | 'elements'
        'Range: elements=10-19'  || 1    | 10   | 'elements'
        'Range: records=20-29'   || 2    | 10   | 'records'
        'Range: records=100-199' || 1    | 100  | 'records'
        'Range: firstNames=0-99' || 0    | 100  | 'firstNames'
    }

    def 'We parse a null or empty Range Header'() {
        when: 'We parse a null or empty Range Header'
        HeaderPageable.parseRangeHeader(header)

        then: 'An exception is thrown'
        def e = thrown(IllegalArgumentException)
        e.message == "Header cannot be null or empty"

        where:
        header << [null, ""]
    }

    def 'We parse a Range header with a bad format'() {
        when: 'We parse a Range header with a bad format'
        HeaderPageable.parseRangeHeader(header)

        then: 'An exception is thrown'
        def e = thrown(IllegalArgumentException)
        e.message == "Header '%s' is not in the correct format. The format must be like 'Range: elements=0-9'".formatted(header)

        where: 'Header with bad format'
        header << ['Range:', "elements:0-9", "Range: elements=0-"]
    }

    def 'We parse a Range header with a bad range'() {
        when: 'We parse a Range header with a bad range'
        HeaderPageable.parseRangeHeader(header)

        then: 'An exception is thrown'
        def e = thrown(IllegalArgumentException)
        e.message == "Header '%s' is not in the correct format. The end must be greater than the start".formatted(header)

        where: 'Header with bad range'
        header << ['Range: elements=10-0', 'Range: elements=10-9']
    }
}
