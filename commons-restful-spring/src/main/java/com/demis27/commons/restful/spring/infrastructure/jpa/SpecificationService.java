package com.demis27.commons.restful.spring.infrastructure.jpa;

import com.demis27.commons.restful.QueryParamFilter;
import jakarta.persistence.criteria.Path;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This service class is responsible for converting a filter string into a Spring Data JPA {@link Specification}.
 * The filter string is a comma-separated list of individual filter criteria.
 * Each criterion is composed of a property name, an operator, and one or more values.
 *
 * @param <T> The type of the JPA entity to which the filter will be applied.
 */
public class SpecificationService<T> {

    /**
     * Converts a filter string into a {@link Specification}.
     *
     * @param filters The filter string to convert.
     * @return A {@link Specification} that can be used to query the database.
     */
    public Optional<Specification<T>> fromFilters(String filters) {
        if (filters == null || filters.trim().isEmpty()) {
            return Optional.empty();
        }

        List<QueryParamFilter> parsedFilters = QueryParamFilter.parse(filters);
        return fromFilters(parsedFilters);
    }

    public Optional<Specification<T>> fromFilters(List<QueryParamFilter> filters) {
        List<Specification<T>> specifications = new ArrayList<>();
        filters.forEach(filter -> specifications.add(parseFilter(filter)));

        return Optional.of(Specification.allOf(specifications));
    }

    private Specification<T> parseFilter(QueryParamFilter filter) {
        return (root, query, criteriaBuilder) -> {
            Path<Object> path = null;
            if (filter.property().contains(".")) {
                String[] nestedProperties = filter.property().split("\\.");
                path = root.get(nestedProperties[0]);
                for (int i = 1; i < nestedProperties.length; i++) {
                    path = path.get(nestedProperties[i]);
                }
            } else {
                path = root.get(filter.property());
            }

            return switch (filter.operator()) {
                case EQUALS -> criteriaBuilder.equal(path, filter.values().getFirst());
                case NOT_EQUALS -> criteriaBuilder.notEqual(path, filter.values().getFirst());
                case GREATER -> criteriaBuilder.greaterThan((Path) path, filter.values().getFirst());
                case GREATER_OR_EQUALS -> criteriaBuilder.greaterThanOrEqualTo((Path) path, filter.values().getFirst());
                case LESS -> criteriaBuilder.lessThan((Path) path, filter.values().getFirst());
                case LESS_OR_EQUALS -> criteriaBuilder.lessThanOrEqualTo((Path) path, filter.values().getFirst());
                case IN -> path.in(filter.values());
                case LIKE -> criteriaBuilder.like((Path) path, filter.values().getFirst());
            };
        };
    }
}
