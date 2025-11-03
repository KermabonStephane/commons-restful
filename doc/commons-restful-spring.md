# Using `commons-restful-spring` in a Clean Architecture Project

This document explains how the `countries-api` project leverages the `commons-restful-spring` library to build a robust
and maintainable RESTful API following the principles of Clean Architecture.

## 1. Overview: Clean Architecture and `commons-restful-spring`

This project is built upon **Clean Architecture**, which organizes the codebase into distinct layers with a strict dependency rule: dependencies only point inwards.

1.  **Entities
    (Domain):** Core business objects.
2.  **Use Cases (Service):** Application-specific business rules.
3.  **Interface Adapters (Infrastructure):** Gateways to external systems (e.g., UI, database, web).
4.  **Frameworks & Drivers:** The outermost layer containing
    frameworks and tools.

The `commons-restful-spring` library fits into the **Interface Adapters** layer. Specifically, it provides a framework for our REST controllers in the `infrastructure.web.controller` package. Its primary goal is to abstract away the boilerplate code associated with implementing RESTful best practices, such as
pagination and sorting, allowing controllers to be leaner and more focused on their primary role: adapting HTTP requests to use case interactions.

## 2. The Role of `commons-restful-spring`

The library provides a set of tools to streamline the creation of REST controllers that handle collections of resources. The key components used
in this project are:

*   `ResourceController<T>`: A generic base controller that provides a standardized implementation for handling paginated and sorted resource collections.
*   `APIResourcesRequest`: A model class that encapsulates all the parameters of a request for a collection of resources, including pagination (`Range` header
    ), sorting (`sort` parameter), and filtering (`filters` parameter).

By using these components, we avoid repetitive and complex code for:
*   Parsing the `Range` HTTP header for pagination.
*   Parsing the `sort` and `filters` query parameters.
*   Building the `Content-Range
` and `Link` headers in the HTTP response.
*   Structuring the controller logic in a consistent way.

## 3. Code Example: `RegionController`

The `RegionController` is a perfect example of how `commons-restful-spring` simplifies the codebase while respecting Clean Architecture principles.

###
The Controller Code

Here is a snippet from `RegionController.java`:


```java
@RestController
@RequestMapping(value = "/api/v1/regions", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class RegionController extends ResourceController<RegionDto> implements RegionApi {

    private final RegionService service; // Use Case (inner layer)
    private final RegionDtoMapper mapper; // Adapter-specific mapper

    @GetMapping
    public ResponseEntity<List<RegionDto>> getAllRegions(
            @RequestHeader(name = "Range", required = false) String rangeHeader,
            @RequestParam(name = "sort", required = false) String sortsQueryParam,
            @RequestParam(name = "filters", required = false) String filterQueryParam) {
        
        // 1. Create a standardized request object
        APIResourcesRequest request = new APIResourcesRequest(
                "regions",
                "/api/v1/regions",
                rangeHeader,
                sortsQueryParam,
                filterQueryParam);

        // 2. Delegate to the base controller's `getAll` method
        return getAll(
                request,
                resourceRequest -> service.getAllResources(resourceRequest).stream().map(mapper::toDto).toList(),
                service::countResources);
    }
    // ... other methods
}
```


### Explanation

1.  **Inheritance:** `RegionController` extends `ResourceController<RegionDto>`, immediately gaining access to the standardized `getAll` method.


2.  **Dependency Injection:** The controller depends on `RegionService` (the Use Case layer) to fetch business data. This adheres to the Clean Architecture dependency rule, as the outer layer (Infrastructure) depends on the inner layer (Service).

3.  **`getAllResources` Method:**
    *
It captures the raw HTTP request parameters (`Range`, `sort`, `filters`).
*   It instantiates `APIResourcesRequest`, which parses and validates these parameters. This encapsulates all the information needed to query the collection.
*   It calls the `getAll()` method inherited from `ResourceController`.


4.  **The `getAll()` Method Call:**
    The magic of the library is in this call. We provide three things:
    *   `request`: The `APIResourcesRequest` object containing the parsed request details.
    *   A **data fetching lambda**: `resourceRequest -> service.getAll
Regions(resourceRequest).stream().map(mapper::toDto).toList()`. This is the core of the operation. The `ResourceController` invokes this lambda, passing its own `Pageable` object. The lambda, in turn:
        *   Calls the `RegionService` to get the domain objects
            (`Region`).
        *   Uses a `RegionDtoMapper` to convert the domain objects into DTOs (`RegionDto`) for the presentation layer.
    *   A **counting function**: `service::countRegions`. The `ResourceController` uses this function to get the total number of available resources,
        which is necessary to build the `Content-Range` header.

## 4. Deep Dive: Services, Ports, and Adapters

Let's explore how the concepts of services, ports, and adapters are implemented in this project, using the "region" feature as an example.

### Services (Use Cases)

The **Service** layer (the `service` package) contains the core application logic. It orchestrates the flow of data between the domain entities and the application's boundaries. A service is a direct representation of a use case.

In our example, `RegionService` is responsible for all business operations related to regions. It is completely independent of the web layer or the database implementation.

```java
// com.demis27.countries.service.RegionService

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionEntityRepository repository; // Injected Port
    private final RegionEntityMapper mapper;
    
    // ... other business methods
}
```

### Ports

**Ports** are the boundaries of the application. They are interfaces that define how the core logic communicates with the outside world, without knowing any implementation details. There are two types of ports:

1.  **Driving Ports (Input Ports):** These define how external actors (like a UI or a script) can interact with the application. In our architecture, the public methods of the `RegionService` act as the driving ports. They define the available operations for regions.

2.  **Driven Ports (Output Ports):** These define the requirements of the application for external services, such as a database or a message queue. The `RegionEntityRepository` is a perfect example of a driven port. The `RegionService` depends on this interface to get data, but it has no knowledge of the underlying database technology.

```java
// com.demis27.countries.infrastructure.jpa.repository.RegionEntityRepository

public interface RegionEntityRepository extends JpaRepository<RegionEntity, Integer> {
    // Spring Data JPA will provide the implementation at runtime
}
```

### Adapters

**Adapters** are the concrete implementations of the ports. They are the glue that connects the ports to the external world (frameworks and drivers).

1.  **Driving Adapters:** These are the components that call the driving ports. The `RegionController` is a driving adapter. It adapts incoming HTTP requests into method calls on the `RegionService`.

    ```java
    // com.demis27.countries.infrastructure.web.controller.RegionController

    @RestController
    // ...
    public class RegionController extends ResourceController<RegionDto> {
        private final RegionService service; // The driving port
        // ...
    }
    ```

2.  **Driven Adapters:** These are the components that implement the driven port interfaces. In our case, the implementation of the `RegionEntityRepository` is provided automatically by **Spring Data JPA**. It adapts the interface's methods into actual SQL queries against a PostgreSQL database. This adapter lives entirely in the outermost layer (Frameworks & Drivers), and we don't even have to write the code ourselves.

By structuring the application this way, we could easily swap out PostgreSQL for another database by simply changing the Spring Data JPA configuration, without ever touching the `RegionService` or the `Region` domain entity.

## 5. Conclusion: Benefits in a Clean Architecture

Using the `commons-restful-spring` library in this project provides several key advantages:

*   **Adherence to Clean Architecture:** The library helps create a clean separation of concerns. The controller
    's responsibility is strictly limited to HTTP-to-application-logic adaptation. The complex logic of handling RESTful pagination and sorting is abstracted away by the base `ResourceController`, and the business logic remains purely in the `RegionService`.
*   **Reduced Boilerplate:** The controller is incredibly lean. Without the
    library, the `getAllRegions` method would be cluttered with code for parsing headers, handling `Pageable` objects, and building response headers manually.
*   **Consistency and Maintainability:** All resource collection endpoints (`/regions`, `/sub-regions`, `/countries`) are implemented in the same standardized way. This makes the
    API predictable for clients and easier to maintain for developers.
