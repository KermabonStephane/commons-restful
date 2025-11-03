package com.demis27.commons.restful.spring.infrastructure.web;

import com.demis27.commons.restful.HeaderPageable;
import com.demis27.commons.restful.spring.model.APIResourcesRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Function;

public class ResourceController<T> {

    public ResponseEntity<List<T>> getAll(APIResourcesRequest resourcesRequest, Function<APIResourcesRequest, List<T>> getAllFunction, Function<APIResourcesRequest, Long> countFunction) {
        HeaderPageable resultRange = HeaderPageable.parseRangeHeader(resourcesRequest.rangeHeaderValue());
        resultRange = HeaderPageable.toBuilder(resultRange).total(countFunction.apply(resourcesRequest)).build();
        return ResponseEntity
                .ok()
                .header(HeaderPageable.CONTENT_RANGE_HEADER_NAME, resultRange.toContentRangeHeader(false))
                .header("link", resultRange.toLinkHeaders("/api/v1/regions").toString())
                .body(getAllFunction.apply(resourcesRequest));
    }
}
