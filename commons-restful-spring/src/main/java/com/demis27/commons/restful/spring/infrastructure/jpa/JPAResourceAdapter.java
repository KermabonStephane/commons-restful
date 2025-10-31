package com.demis27.commons.restful.spring.infrastructure.jpa;

import com.demis27.commons.restful.spring.SpringSupport;
import com.demis27.commons.restful.spring.model.APIResourcesRequest;
import com.demis27.commons.restful.spring.service.ResourcePort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public abstract class JPAResourceAdapter<D, E, K> implements ResourcePort<D> {

    @Autowired
    protected JpaRepository<E, K> repository;

    @Autowired
    protected EntityMapper<E, D> mapper;

    @Override
    public List<D> getAllResources(APIResourcesRequest request) {
        PageRequest pageRequest = (new SpringSupport()).parseFromRest(request.rangeHeaderValue(), request.sortQueryParam());
        return repository.findAll(pageRequest).stream().map(entity -> mapper.toDomain(entity)).toList();
    }
}
