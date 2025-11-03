package com.demis27.commons.restful.spring.service;

import com.demis27.commons.restful.spring.model.APIResourcesRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public abstract class ResourceService<D> {

    @Autowired
    protected ResourcePort<D> support;

    public List<D> getAllResources(APIResourcesRequest request) {
        return support.getAllResources(request);
    }

    public Long countResources(APIResourcesRequest request) {
        return support.countResources(request);
    }
}
