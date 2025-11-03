package com.demis27.commons.restful.spring.model;

/**
 * All information needed to get the list of a specific API resource.
 *
 * @param resourceName The name of the resource, e.g. countries.
 * @param baseURI The base URI, e.g. /api/v1/countries.
 * @param rangeHeaderValue The range from the header 'Range', e.g.
 *                         countries=0-19.
 * @param sortQueryParam The sort params from the query params, e.g. code:asc.
 * @param filterQueryParam The filter params from the query params, e.g.
 *                         'code eq 4'.
 */
public record APIResourcesRequest(
        String resourceName,
        String baseURI,
        String rangeHeaderValue,
        String sortQueryParam,
        String filterQueryParam) {
}
