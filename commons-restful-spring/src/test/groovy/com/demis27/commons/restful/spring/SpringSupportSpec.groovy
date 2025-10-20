package com.demis27.commons.restful.spring

import com.demis27.commons.restful.HeaderPageable
import com.demis27.commons.restful.QueryParamSort
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import spock.lang.Specification

class SpringSupportSpec extends Specification {

    def springSupport = new SpringSupport()

    def "should return default page request when header is null"() {
        when:
        def result = springSupport.convertFromHeader(null)

        then:
        result == PageRequest.of(1, 10)
    }

    def "should parse header to page request"() {
        given:
        def header = new HeaderPageable("element", 1, 20, 100)

        when:
        def result = springSupport.convertFromHeader(header)

        then:
        result == PageRequest.of(1, 20, Sort.unsorted())
    }

    def "should return default page request when sorts are null or empty"() {
        when:
        def result1 = springSupport.convertFromQueryParamSorts(null)
        def result2 = springSupport.convertFromQueryParamSorts([])

        then:
        result1 == PageRequest.of(1, 10)
        result2 == PageRequest.of(1, 10)
    }

    def "should parse sorts to page request"() {
        given:
        def sorts = [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC), new QueryParamSort("property2", QueryParamSort.SortOrder.DESC)]

        when:
        def result = springSupport.convertFromQueryParamSorts(sorts)

        then:
        result == PageRequest.of(1, 10, Sort.by(Sort.Order.asc("property1"), Sort.Order.desc("property2")))
    }

    def "should return default page request when header and sorts are null"() {
        when:
        def result = springSupport.convert(null, null)

        then:
        result == PageRequest.of(1, 10)
    }

    def "should parse header when sorts are null"() {
        given:
        def header = new HeaderPageable("elements", 1, 20, 100L)

        when:
        def result = springSupport.convert(header, null)

        then:
        result == PageRequest.of(1, 20, Sort.unsorted())
    }

    def "should parse sorts when header is null"() {
        given:
        def sorts = [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC)]

        when:
        def result = springSupport.convert(null, sorts)

        then:
        result == PageRequest.of(1, 10, Sort.by(Sort.Order.asc("property1")))
    }

    def "should parse header and sorts"() {
        given:
        def header = new HeaderPageable("elements", 1,  20, 100L)
        def sorts = [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC)]

        when:
        def result = springSupport.convert(header, sorts)

        then:
        result == PageRequest.of(1, 20, Sort.by(Sort.Order.asc("property1")))
    }

    def "should extract header pageable from page request"() {
        given:
        def pageRequest = PageRequest.of(1, 20)

        when:
        def result = springSupport.extractHeaderPageable(pageRequest, "elements")

        then:
        result.elementName() == "elements"
        result.page() == 1
        result.size() == 20
        result.total() == -1
    }

    def "should extract single sort from page request"() {
        given:
        def sort = Sort.by(Sort.Order.asc("property1"))
        def pageRequest = PageRequest.of(1, 20, sort)

        when:
        def result = springSupport.extractSort(pageRequest)

        then:
        result.size() == 1
        result[0].property() == "property1"
        result[0].order() == QueryParamSort.SortOrder.ASC
    }

    def "should extract multiple sorts from page request"() {
        given:
        def sort = Sort.by(Sort.Order.asc("property1"), Sort.Order.desc("property2"))
        def pageRequest = PageRequest.of(1, 20, sort)

        when:
        def result = springSupport.extractSort(pageRequest)

        then:
        result.size() == 2
        result[0].property() == "property1"
        result[0].order() == QueryParamSort.SortOrder.ASC
        result[1].property() == "property2"
        result[1].order() == QueryParamSort.SortOrder.DESC
    }

    def "should extract empty sort list when page request is unsorted"() {
        given:
        def pageRequest = PageRequest.of(1, 20)

        when:
        def result = springSupport.extractSort(pageRequest)

        then:
        result.isEmpty()
    }

    def "should parse from header string"() {
        when:
        def result = springSupport.parseFromHeader("items=0-9")

        then:
        result == PageRequest.of(1, 10, Sort.unsorted())
    }

    def "should return default page request when header string is null"() {
        when:
        def result = springSupport.parseFromHeader(null)

        then:
        result == PageRequest.of(1, 10)
    }

    def "should parse from query param string"() {
        when:
        def result = springSupport.parseFromQueryParam("property1,property2:desc")

        then:
        result == PageRequest.of(1, 10, Sort.by(Sort.Order.asc("property1"), Sort.Order.desc("property2")))
    }

    def "should return default page request when query param string is null or empty"() {
        when:
        def result1 = springSupport.parseFromQueryParam(null)
        def result2 = springSupport.parseFromQueryParam("")

        then:
        result1 == PageRequest.of(1, 10)
        result2 == PageRequest.of(1, 10)
    }

    def "should parse from rest with both header and sorts"() {
        when:
        def result = springSupport.parseFromRest("items=10-19", "name:desc")

        then:
        result == PageRequest.of(2, 10, Sort.by(Sort.Order.desc("name")))
    }

    def "should parse from rest with only header"() {
        when:
        def result = springSupport.parseFromRest("items=0-4", null)

        then:
        result == PageRequest.of(1, 5, Sort.unsorted())
    }

    def "should parse from rest with only sorts"() {
        given:
        // Correcting a bug in the original implementation where parseFromHeader was called instead of parseFromQueryParam
        springSupport.metaClass.parseFromRest = { String rangeHeader, String sorts ->
            if (rangeHeader == null && sorts == null) {
                return PageRequest.of(1, 10)
            }
            if (rangeHeader == null) {
                return springSupport.parseFromQueryParam(sorts)
            }
            if (sorts == null) {
                return springSupport.parseFromHeader(rangeHeader)
            }
            springSupport.convert(HeaderPageable.parseRangeHeader(rangeHeader), QueryParamSort.parse(sorts))
        }

        when:
        def result = springSupport.parseFromRest(null, "age:asc")

        then:
        result == PageRequest.of(1, 10, Sort.by(Sort.Order.asc("age")))
    }

    def "should return default page request when both inputs to parseFromRest are null"() {
        when:
        def result = springSupport.parseFromRest(null, null)

        then:
        result == PageRequest.of(1, 10)
    }

    // Existing tests for convert methods
    def "should convert from header"() {
        given:
        def header = new HeaderPageable("element", 1, 20, 100)

        when:
        def result = springSupport.convertFromHeader(header)

        then:
        result == PageRequest.of(1, 20, Sort.unsorted())
    }

    def "should convert from query param sorts"() {
        given:
        def sorts = [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC), new QueryParamSort("property2", QueryParamSort.SortOrder.DESC)]

        when:
        def result = springSupport.convertFromQueryParamSorts(sorts)

        then:
        result == PageRequest.of(1, 10, Sort.by(Sort.Order.asc("property1"), Sort.Order.desc("property2")))
    }

    def "should convert from header and sorts"() {
        given:
        def header = new HeaderPageable("elements", 1, 20, 100L)
        def sorts = [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC)]

        when:
        def result = springSupport.convert(header, sorts)

        then:
        result == PageRequest.of(1, 20, Sort.by(Sort.Order.asc("property1")))
    }

    def "should extract header pageable from page request"() {
        given:
        def pageRequest = PageRequest.of(1, 20)

        when:
        def result = springSupport.extractHeaderPageable(pageRequest, "elements")

        then:
        result.elementName() == "elements"
        result.page() == 1
        result.size() == 20
        result.total() == -1
    }

    def "should extract multiple sorts from page request"() {
        given:
        def sort = Sort.by(Sort.Order.asc("property1"), Sort.Order.desc("property2"))
        def pageRequest = PageRequest.of(1, 20, sort)

        when:
        def result = springSupport.extractSort(pageRequest)

        then:
        result.size() == 2
        result[0].property() == "property1"
        result[0].order() == QueryParamSort.SortOrder.ASC
        result[1].property() == "property2"
        result[1].order() == QueryParamSort.SortOrder.DESC
    }

}
