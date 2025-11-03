package com.demis27.commons.restful.spring.service;

import com.demis27.commons.restful.spring.model.APIResourcesRequest;

import java.util.List;

public interface ResourcePort<D> {

    List<D> getAllResources(APIResourcesRequest request);

    Long countResources(APIResourcesRequest request);
}
