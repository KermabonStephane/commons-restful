package com.demis27.commons.restful.spring.service;

import com.demis27.commons.restful.spring.model.APIResourcesRequest;

import java.util.List;

public abstract class ResourceService<D> {

    protected final ResourcePort<D> support;

    protected ResourceService(ResourcePort<D> support) {
        this.support = support;
    }

    public List<D> getAllResources(APIResourcesRequest request) {
        return support.getAllResources(request);
    }

    public Long countResources(APIResourcesRequest request) {
        return support.countResources(request);
    }
}
