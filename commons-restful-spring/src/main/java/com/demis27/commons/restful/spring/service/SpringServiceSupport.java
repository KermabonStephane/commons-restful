package com.demis27.commons.restful.spring.service;

import com.demis27.commons.restful.spring.model.APIResourcesRequest;

import java.util.List;

public abstract class SpringServiceSupport<D> {

    public List<D> getAllResources(APIResourcesRequest request) {
        return getPort().getAllResources(request);
    }

    protected abstract SpringPortSupport<D> getPort();
}
