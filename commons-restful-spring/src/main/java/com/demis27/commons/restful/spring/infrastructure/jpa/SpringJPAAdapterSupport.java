package com.demis27.commons.restful.spring.infrastructure.jpa;

import com.demis27.commons.restful.spring.model.APIResourcesRequest;
import com.demis27.commons.restful.spring.service.SpringPortSupport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class SpringJPAAdapterSupport<D, E, K> implements SpringPortSupport<D> {


    @Override
    public List<D> getAllResources(APIResourcesRequest request) {
        return getRepository().findAll().stream().map(entity -> getMapper().toDomain(entity)).toList();
    }

    public abstract JpaRepository<E, K> getRepository();

    public abstract EntityMapper<E, D> getMapper();
}
