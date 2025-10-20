package com.demis27.commons.restful

import spock.lang.Specification
import spock.lang.Unroll

class HeaderPageableNavSpec extends Specification {

    @Unroll
    def "Test nextPage navigation from page #currentPage"() {
        given:
        def headerPageable = new HeaderPageable("elements", currentPage, 10, 100L)

        when: 'We get the next page'
        def nextPage = headerPageable.nextPage()

        then: 'The next page is correctly'
        nextPage.page == expectedPage
        and: 'Others parameters still the same'
        nextPage.size == 10
        nextPage.total == 100L
        nextPage.elementName == "elements"

        where:
        currentPage | expectedPage
        1           | 2
        2           | 3
        8           | 9
    }

    def "Test nextPage navigation on last page throws exception"() {
        given:
        def headerPageable = new HeaderPageable("elements", 9, 10, 100L)

        when: 'We get the next page'
        headerPageable.nextPage()

        then:
        def e = thrown(IndexOutOfBoundsException)
        e.message == "Cannot move to next page from the last page"
    }

    @Unroll
    def "Test previousPage navigation from page #currentPage"() {
        given:
        def headerPageable = new HeaderPageable("elements", currentPage, 10, 100L)

        when: 'We get the previous page'
        def previousPage = headerPageable.previousPage()

        then: 'The previous page is correctly'
        previousPage.page == expectedPage
        and: 'Others parameters still the same'
        previousPage.size == 10
        previousPage.total == 100L
        previousPage.elementName == "elements"

        where:
        currentPage | expectedPage
        2           | 1
        9           | 8
        5           | 4
    }

    def "Test previousPage navigation on first page throws exception"() {
        given:
        def headerPageable = new HeaderPageable("elements", 1, 10, 100L)

        when: 'We get the previous page'
        headerPageable.previousPage()

        then:
        def e = thrown(IndexOutOfBoundsException)
        e.message == "Cannot move to previous page from the first page"
    }


    @Unroll
    def "Test firstPage navigation from page #currentPage"() {
        given:
        def headerPageable = new HeaderPageable("elements", currentPage, 10, 100L)

        when: 'We get the first page'
        def firstPage = headerPageable.firstPage()

        then: 'The first page is correctly'
        firstPage.page == 1
        and: 'Others parameters still the same'
        firstPage.size == 10
        firstPage.total == 100L
        firstPage.elementName == "elements"

        where:
        currentPage << [2, 9, 5, 1]
    }

    @Unroll
    def "Test lastPage navigation with total #total and size #size"() {
        given:
        def headerPageable = new HeaderPageable("elements", 1, size, total)

        when: 'We get the last page'
        def lastPage = headerPageable.lastPage()

        then: 'The last page is correctly'
        lastPage.page == expectedPage
        and: 'Others parameters still the same'
        lastPage.size == size
        lastPage.total == total
        lastPage.elementName == "elements"

        where:
        total | size || expectedPage
        100L  | 10   || 9
        101L  | 10   || 10
        99L   | 10   || 9
        100L  | 25   || 3
    }
}
