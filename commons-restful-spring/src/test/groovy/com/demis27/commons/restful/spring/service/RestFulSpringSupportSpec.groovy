package com.demis27.commons.restful.spring.service

import com.demis27.commons.restful.HeaderPageable
import com.demis27.commons.restful.QueryParamSort
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import spock.lang.Specification

class RestFulSpringSupportSpec extends Specification {

    def springSupport = new RestFulSpringSupport()

    def "should parse header to page request"() {
        when:
        def pageRequest = springSupport.convertFromHeader(header)

        then:
        pageRequest == expectedPageRequest

        where:
        header                                    || expectedPageRequest
        null                                      || PageRequest.of(0, 10)
        new HeaderPageable("element", 0, 10, 100) || PageRequest.of(0, 10, Sort.unsorted())
        new HeaderPageable("element", 1, 20, 100) || PageRequest.of(1, 20, Sort.unsorted())
        new HeaderPageable("element", 2, 30, 100) || PageRequest.of(2, 30, Sort.unsorted())
    }

    def "should parse sorts to page request"() {
        when:
        def pageRequest = springSupport.convertFromQueryParamSorts(sorts)

        then:
        pageRequest == expectedPageRequest

        where:
        sorts                                                                                                                           || expectedPageRequest
        null                                                                                                                            || PageRequest.of(0, 10)
        []                                                                                                                              || PageRequest.of(0, 10)
        [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC)]                                                                 || PageRequest.of(0, 10, Sort.by(Sort.Order.asc("property1")))
        [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC), new QueryParamSort("property2", QueryParamSort.SortOrder.DESC)] || PageRequest.of(0, 10, Sort.by(Sort.Order.asc("property1"), Sort.Order.desc("property2")))
    }

    def "should extract header pageable from page request"() {
        given:
        def pageRequest = PageRequest.of(1, 20)

        when:
        def header = springSupport.extractHeaderPageable(pageRequest, "elements")

        then:
        header.elementName() == "elements"
        header.page() == 1
        header.size() == 20
        header.total() == -1
    }

    def "should extract single sort from page request"() {
        when:
        def queryParams = springSupport.extractSort(pageRequest)

        then:
        queryParams == expectedQueryParams

        where:
        pageRequest                                                                               || expectedQueryParams
        PageRequest.of(1, 20, Sort.by(Sort.Order.asc("property1")))                               || [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC)]
        PageRequest.of(1, 20, Sort.by(Sort.Order.asc("property1"), Sort.Order.desc("property2"))) || [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC), new QueryParamSort("property2", QueryParamSort.SortOrder.DESC)]
        PageRequest.of(1, 20)                                                                     || []
    }

    def "should parse from header string"() {
        when:
        def pageRequest = springSupport.parseFromHeader(rangeHeader)

        then:
        pageRequest == expectedPageRequest

        where:
        rangeHeader   || expectedPageRequest
        "items=10-19" || PageRequest.of(1, 10, Sort.unsorted())
        null          || PageRequest.of(0, 10)
    }

    def "should parse from query param string"() {
        when:
        def pageRequest = springSupport.parseFromQueryParam(sortString)

        then:
        pageRequest == expectedPageRequest

        where:
        sortString                 || expectedPageRequest
        "property1,property2:desc" || PageRequest.of(0, 10, Sort.by(Sort.Order.asc("property1"), Sort.Order.desc(
                "property2")))
        null                       || PageRequest.of(0, 10)
        ""                         || PageRequest.of(0, 10)
    }

    def "should parse from rest with both header and sorts"() {
        when:
        def pageRequest = springSupport.parseFromRest(rangeHeader, sorts)

        then:
        pageRequest == expectedPageRequest

        where:
        rangeHeader   | sorts       || expectedPageRequest
        "items=10-19" | "name:desc" || PageRequest.of(1, 10, Sort.by(Sort.Order.desc("name")))
        "items=0-4"   | null        || PageRequest.of(0, 5, Sort.unsorted())
        "items=0-4"   | ""          || PageRequest.of(0, 5, Sort.unsorted())
        null          | "name:desc" || PageRequest.of(0, 10, Sort.by(Sort.Order.desc("name")))
        null          | null        || PageRequest.of(0, 10)
        null          | ""          || PageRequest.of(0, 10)

    }

    def "should convert from header and sorts"() {
        when:
        def pageRequest = springSupport.convert(header, sorts)

        then:
        pageRequest == expectedPageRequest

        where:
        header                                      | sorts                                                           || expectedPageRequest
        new HeaderPageable("elements", 1, 20, 100L) | [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC)] || PageRequest.of(1, 20, Sort.by(Sort.Order.asc("property1")))
        null                                        | [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC)] || PageRequest.of(0, 10, Sort.by(Sort.Order.asc("property1")))
        new HeaderPageable("elements", 1, 20, 100L) | null                                                            || PageRequest.of(1, 20, Sort.unsorted())
        new HeaderPageable("elements", 1, 20, 100L) | []                                                              || PageRequest.of(1, 20, Sort.unsorted())
        null                                        | null                                                            || PageRequest.of(0, 10)
        null                                        | []                                                              || PageRequest.of(0, 10)
    }

}
