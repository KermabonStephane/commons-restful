package com.demis27.commons.restful.spring.infrastructure.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface JpaResourceRepository<T, I> extends JpaRepository<T, I>, JpaSpecificationExecutor<T> {
}
