package com.demis27.commons.restfull

import spock.lang.Specification
import spock.lang.Unroll

class HeaderPageableNavSpec extends Specification {

    @Unroll
    def "Test nextPage navigation from page #currentPage"() {
        given:
        def headerPageable = new HeaderPageable("elements", currentPage, 10L, 100L)

        when: 'We get the next page'
        def nextPage = headerPageable.nextPage()

        then: 'The next page is correctly'
        nextPage.page == expectedPage
        and: 'Others parameters still the same'
        nextPage.size == 10L
        nextPage.total == 100L
        nextPage.elementName == "elements"

        where:
        currentPage | expectedPage
        1L          | 2L
        0L          | 1L
        8L          | 9L
    }

    def "Test nextPage navigation on last page throws exception"() {
        given:
        def headerPageable = new HeaderPageable("elements", 9L, 10L, 100L)

        when: 'We get the next page'
        headerPageable.nextPage()

        then:
        def e = thrown(IndexOutOfBoundsException)
        e.message == "Cannot move to next page from the last page"
    }

    @Unroll
    def "Test previousPage navigation from page #currentPage"() {
        given:
        def headerPageable = new HeaderPageable("elements", currentPage, 10L, 100L)

        when: 'We get the previous page'
        def previousPage = headerPageable.previousPage()

        then: 'The previous page is correctly'
        previousPage.page == expectedPage
        and: 'Others parameters still the same'
        previousPage.size == 10L
        previousPage.total == 100L
        previousPage.elementName == "elements"

        where:
        currentPage | expectedPage
        1L          | 0L
        9L          | 8L
        5L          | 4L
    }

    def "Test previousPage navigation on first page throws exception"() {
        given:
        def headerPageable = new HeaderPageable("elements", 0L, 10L, 100L)

        when: 'We get the previous page'
        headerPageable.previousPage()

        then:
        def e = thrown(IndexOutOfBoundsException)
        e.message == "Cannot move to previous page from the first page"
    }


    @Unroll
    def "Test firstPage navigation from page #currentPage"() {
        given:
        def headerPageable = new HeaderPageable("elements", currentPage, 10L, 100L)

        when: 'We get the first page'
        def firstPage = headerPageable.firstPage()

        then: 'The first page is correctly'
        firstPage.page == 0L
        and: 'Others parameters still the same'
        firstPage.size == 10L
        firstPage.total == 100L
        firstPage.elementName == "elements"

        where:
        currentPage << [1L, 9L, 5L, 0L]
    }

    @Unroll
    def "Test lastPage navigation with total #total and size #size"() {
        given:
        def headerPageable = new HeaderPageable("elements", 1L, size, total)

        when: 'We get the last page'
        def lastPage = headerPageable.lastPage()

        then: 'The last page is correctly'
        lastPage.page == expectedPage
        and: 'Others parameters still the same'
        lastPage.size == size
        lastPage.total == total
        lastPage.elementName == "elements"

        where:
        total | size | expectedPage
        100L  | 10L  | 9L
        101L  | 10L  | 10L
        99L   | 10L  | 9L
        100L  | 25L  | 3L
    }
}
