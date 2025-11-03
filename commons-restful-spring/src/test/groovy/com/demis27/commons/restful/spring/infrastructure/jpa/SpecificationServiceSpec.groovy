package com.demis27.commons.restful.spring.infrastructure.jpa


import jakarta.persistence.criteria.*
import spock.lang.Specification

class SpecificationServiceSpec extends Specification {

    private SpecificationService<MyEntity> service

    // Mocks for JPA Criteria API
    private CriteriaBuilder criteriaBuilder
    private CriteriaQuery criteriaQuery
    private Root<MyEntity> root
    private Path path

    def setup() {
        service = new SpecificationService<>()
        criteriaBuilder = Mock()
        criteriaQuery = Mock()
        root = Mock()
        path = Mock()
    }

    def "should correctly parse a single '#operator' filter"() {
        given: "A filter string"
        String filters = "field $operator value"
        root.get("field") >> path

        and: "The specification is created"
        def spec = service.fromFilters(filters)

        when: "The specification is applied"
        spec.get().toPredicate(root, criteriaQuery, criteriaBuilder)

        then: "The correct criteria builder method is called"
        if (operator == "eq") {
            1 * criteriaBuilder.equal(path, "value")
        } else if (operator == "ne") {
            1 * criteriaBuilder.notEqual(path, "value")
        } else if (operator == "gt") {
            1 * criteriaBuilder.greaterThan(path, "value")
        } else if (operator == "gte") {
            1 * criteriaBuilder.greaterThanOrEqualTo(path, "value")
        } else if (operator == "lt") {
            1 * criteriaBuilder.lessThan(path, "value")
        } else if (operator == "lte") {
            1 * criteriaBuilder.lessThanOrEqualTo(path, "value")
        } else if (operator == "like") {
            1 * criteriaBuilder.like(path, "value")
        }

        where:
        operator << ["eq", "ne", "gt", "gte", "lt", "lte", "like"]
    }

    def "should correctly parse a single 'in' filter with multiple values"() {
        given:
        String filters = "status in ACTIVE PENDING"
        root.get("status") >> path
        def spec = service.fromFilters(filters)

        when:
        spec.get().toPredicate(root, criteriaQuery, criteriaBuilder)

        then:
        1 * path.in(["ACTIVE", "PENDING"])
    }

    def "should correctly parse multiple filters combined with AND"() {
        given:
        String filters = "name eq John,age gt 30"
        def namePath = Mock(Path)
        def agePath = Mock(Path)
        root.get("name") >> namePath
        root.get("age") >> agePath

        def namePredicate = Mock(Predicate)
        def agePredicate = Mock(Predicate)

        def spec = service.fromFilters(filters)

        when:
        spec.get().toPredicate(root, criteriaQuery, criteriaBuilder)

        then: "The individual predicates are created"
        1 * criteriaBuilder.equal(namePath, "John") >> namePredicate
        1 * criteriaBuilder.greaterThan(agePath, "30") >> agePredicate

        and: "The predicates are combined with AND"
        1 * criteriaBuilder.and(namePredicate, agePredicate)
    }

    def "should handle property names with dots for nested objects"() {
        given:
        String filters = "address.city eq London"
        def addressPath = Mock(Path)
        def cityPath = Mock(Path)
        root.get("address") >> addressPath
        addressPath.get("city") >> cityPath
        def spec = service.fromFilters(filters)

        when:
        spec.get().toPredicate(root, criteriaQuery, criteriaBuilder)

        then:
        1 * criteriaBuilder.equal(cityPath, "London")
    }

    // Dummy entity for generic type
    private static class MyEntity {}
}
