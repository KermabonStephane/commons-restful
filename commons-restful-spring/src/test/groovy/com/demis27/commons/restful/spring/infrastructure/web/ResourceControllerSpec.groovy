package com.demis27.commons.restful.spring.infrastructure.web

import com.demis27.commons.restful.spring.model.APIResourcesRequest
import spock.lang.Specification

import java.util.function.Function
import java.util.function.ToLongFunction

class ResourceControllerSpec extends Specification {

    def "getAll should call getAllFunction and countFunction"() {
        given: "a resource controller"
        def controller = new ResourceController<String>()

        and: "a request"
        def request = new APIResourcesRequest("elements", "/api/v1/elements", "elements=0-9", null, null)

        and: "mocked functions"
        def getAllFunction = Mock(Function)
        def countFunction = Mock(ToLongFunction)

        when: "getAll is called"
        controller.getAll(request, getAllFunction, countFunction)

        then: "the functions are called"
        1 * countFunction.applyAsLong(request)
        1 * getAllFunction.apply(request)
    }

    def "getAll should use default pageable when range header is null"() {
        given: "a resource controller"
        def controller = new ResourceController<String>()

        and: "a request with a null range header"
        def request = new APIResourcesRequest("items", "/api/v1/elements", null, null, null)

        and: "functions that return values"
        def getAllFunction = { req -> ["item1", "item2"] } as Function
        def countFunction = { req -> 100L } as ToLongFunction

        when: "getAll is called"
        def response = controller.getAll(request, getAllFunction, countFunction)

        then: "the response is correct"
        response.statusCode.value() == 200
        response.headers.get("Content-Range") == ["items 0-9/100"]
        response.body == ["item1", "item2"]
    }
}
