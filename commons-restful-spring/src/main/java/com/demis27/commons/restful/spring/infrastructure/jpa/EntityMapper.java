package com.demis27.commons.restful.spring.infrastructure.jpa;

public interface EntityMapper<E, D> {
    D toDomain(E entity);
}
