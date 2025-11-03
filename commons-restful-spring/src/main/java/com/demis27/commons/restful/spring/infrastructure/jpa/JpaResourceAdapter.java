package com.demis27.commons.restful.spring.infrastructure.jpa;

import com.demis27.commons.restful.spring.model.APIResourcesRequest;
import com.demis27.commons.restful.spring.service.ResourcePort;
import com.demis27.commons.restful.spring.service.RestFulSpringSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public abstract class JpaResourceAdapter<D, E, K> implements ResourcePort<D> {

    private final SpecificationService<E> specificationService = new SpecificationService<>();

    protected final JpaResourceRepository<E, K> repository;

    protected final EntityMapper<E, D> mapper;

    protected JpaResourceAdapter(JpaResourceRepository<E, K> repository, EntityMapper<E, D> mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public List<D> getAllResources(APIResourcesRequest request) {
        PageRequest pageRequest = new RestFulSpringSupport().parseFromRest(request.rangeHeaderValue(), request.sortQueryParam());
        Optional<Specification<E>> optionalSpecification = specificationService.fromFilters(request.filterQueryParam());

        return optionalSpecification.map(eSpecification -> repository.findAll(eSpecification, pageRequest)
                        .stream()
                        .map(mapper::toDomain)
                        .toList())
                .orElseGet(() -> repository.findAll(pageRequest)
                        .stream()
                        .map(mapper::toDomain)
                        .toList());
    }

    @Override
    public Long countResources(APIResourcesRequest request) {
        Optional<Specification<E>> optionalSpecification = specificationService.fromFilters(request.filterQueryParam());

        return optionalSpecification.map(eSpecification -> repository.count(eSpecification))
                .orElseGet(() -> repository.count());
    }
}
