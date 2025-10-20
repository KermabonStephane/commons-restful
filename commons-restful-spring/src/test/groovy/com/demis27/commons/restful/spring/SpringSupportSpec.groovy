package com.demis27.commons.restful.spring

import com.demis27.commons.restful.HeaderPageable
import com.demis27.commons.restful.QueryParamSort
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import spock.lang.Specification

class SpringSupportSpec extends Specification {

    def "should return default page request when header is null"() {
        given:
        def springHeaderPageable = new SpringSupport()

        when:
        def result = springHeaderPageable.parseHeader(null)

        then:
        result == PageRequest.of(1, 10)
    }

    def "should parse header to page request"() {
        given:
        def springHeaderPageable = new SpringSupport()
        def header = new HeaderPageable("element", 1, 20, 100)

        when:
        def result = springHeaderPageable.parseHeader(header)

        then:
        result == PageRequest.of(1, 20)
    }

    def "should return default page request when sorts are null or empty"() {
        given:
        def springHeaderPageable = new SpringSupport()

        when:
        def result1 = springHeaderPageable.parseSort(null)
        def result2 = springHeaderPageable.parseSort([])

        then:
        result1 == PageRequest.of(1, 10)
        result2 == PageRequest.of(1, 10)
    }

    def "should parse sorts to page request"() {
        given:
        def springHeaderPageable = new SpringSupport()
        def sorts = [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC), new QueryParamSort("property2", QueryParamSort.SortOrder.DESC)]

        when:
        def result = springHeaderPageable.parseSort(sorts)

        then:
        result == PageRequest.of(1, 10, Sort.by(Sort.Order.asc("property1"), Sort.Order.desc("property2")))
    }

    def "should return default page request when header and sorts are null"() {
        given:
        def springHeaderPageable = new SpringSupport()

        when:
        def result = springHeaderPageable.parse(null, null)

        then:
        result == PageRequest.of(1, 10)
    }

    def "should parse header when sorts are null"() {
        given:
        def springHeaderPageable = new SpringSupport()
        def header = new HeaderPageable("elements", 1, 20, 100L)

        when:
        def result = springHeaderPageable.parse(header, null)

        then:
        result == PageRequest.of(1, 20, Sort.unsorted())
    }

    def "should parse sorts when header is null"() {
        given:
        def springHeaderPageable = new SpringSupport()
        def sorts = [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC)]

        when:
        def result = springHeaderPageable.parse(null, sorts)

        then:
        result == PageRequest.of(1, 10, Sort.by(Sort.Order.asc("property1")))
    }

    def "should parse header and sorts"() {
        given:
        def springHeaderPageable = new SpringSupport()
        def header = new HeaderPageable("elements", 1,  20, 100L)
        def sorts = [new QueryParamSort("property1", QueryParamSort.SortOrder.ASC)]

        when:
        def result = springHeaderPageable.parse(header, sorts)

        then:
        result == PageRequest.of(1, 20, Sort.by(Sort.Order.asc("property1")))
    }

    def "should extract header pageable from page request"() {
        given:
        def springPageableSupport = new SpringSupport()
        def pageRequest = PageRequest.of(1, 20)

        when:
        def result = springPageableSupport.extractHeaderPageable(pageRequest, "elements")

        then:
        result.elementName() == "elements"
        result.page() == 1
        result.size() == 20
        result.total() == -1
    }

    def "should extract empty sort list"() {
        given:
        def springPageableSupport = new SpringSupport()
        def sort = Sort.by(Sort.Order.asc("property1"))
        def pageRequest = PageRequest.of(1, 20, sort)

        when:
        def result = springPageableSupport.extractSort(pageRequest)

        then:
        result.size() == 1
        result[0].property() == "property1"
        result[0].order() == QueryParamSort.SortOrder.ASC
    }

}
