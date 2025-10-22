package com.demis27.commons.restful

import spock.lang.Specification

class HeaderPageableLinkHeaderSpec extends Specification {

    def "generate links header"() {
        given:
        HeaderPageable pageable = new HeaderPageable("elements", 1, 10, 100)

        when:
        def linkHeaders = pageable.toLinkHeaders("/api/v1/countries")

        then:
        linkHeaders.toString() == "</api/v1/countries>; rel=\"first\"; range=\"0-9\", </api/v1/countries>; rel=\"previous\"; range=\"0-9\", </api/v1/countries>; rel=\"next\"; range=\"20-29\", </api/v1/countries>; rel=\"last\"; range=\"90-99\""
    }

    def "generate links header for the first page"() {
        given:
        HeaderPageable pageable = new HeaderPageable("elements", 0, 10, 99)

        when:
        def linkHeaders = pageable.toLinkHeaders("/api/v1/countries")

        then:
        linkHeaders.toString() == "</api/v1/countries>; rel=\"first\"; range=\"0-9\", </api/v1/countries>; rel=\"previous\"; range=\"0-9\", </api/v1/countries>; rel=\"next\"; range=\"10-19\", </api/v1/countries>; rel=\"last\"; range=\"90-98\""
    }

    def "generate links header for the last page"() {
        given:
        HeaderPageable pageable = new HeaderPageable("elements", 9, 10, 99)

        when:
        def linkHeaders = pageable.toLinkHeaders("/api/v1/countries")

        then:
        linkHeaders.toString() == "</api/v1/countries>; rel=\"first\"; range=\"0-9\", </api/v1/countries>; rel=\"previous\"; range=\"80-89\", </api/v1/countries>; rel=\"next\"; range=\"90-98\", </api/v1/countries>; rel=\"last\"; range=\"90-98\""
    }
}
