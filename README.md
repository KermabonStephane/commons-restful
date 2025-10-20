# commons-restful

## Module commons-restfull

Common classes to help build RESTful services. Read the [HowTo.md](HowTo.md) to know how integrates this library on your
project.

Read the [Release.md ](Release.md) for the releases.

Sonar: https://sonarcloud.io/

### Features

* **Pagination**: Handle pagination through HTTP headers (`Range`, `Content-Range`, and `Accept-Ranges`).
* **Sorting**: Easily parse sorting criteria from request parameters.
* **Filtering**: Parse filtering criteria from request parameters.

### Usage

#### Pagination

The `HeaderPageable` record helps with pagination based on HTTP Range headers.

**Parsing a `Range` header:**

```java
String rangeHeader = "Range: items=0-9";
HeaderPageable pageable = HeaderPageable.parseRangeHeader(rangeHeader);
// pageable.page() will be 1
// pageable.size() will be 10
```

**Creating a `Content-Range` header:**

```java
HeaderPageable pageable = new HeaderPageable("items", 1, 10, 100);
String contentRangeHeader = pageable.toContentRangeHeader();
// contentRangeHeader will be "Content-Range: items 0-9/100"
```

#### Sorting

The `QueryParamSort` record help with sorting.

**Parsing a sort string:**

```java
String sortString = "name,age:desc";
List<QueryParamSort> sorts = QueryParamSort.parse(sortString);
// sorts will contain [QueryParamSort[property=name, order=ASC], QueryParamSort[property=age, order=DESC]]
```

#### Filtering

The `QueryParamFilter` record help with filtering.

**Parsing a filter string:**

```java
String filterString = "name eq John,age gt 25";
List<QueryParamFilter> filters = QueryParamFilter.parse(filterString);
// filters will contain [QueryParamFilter[property=name, operator=EQUALS, value=John], QueryParamFilter[property=age, operator=GREATER, value=25]]
```

## Module commons-restful-spring

This module provides helper classes to integrate `commons-restful` with the Spring ecosystem, particularly Spring Data. It simplifies the conversion between the library's pagination/sorting objects and Spring Data's `PageRequest`.

To use it, add the `commons-restful-spring` dependency to your project.

### Usage

The `SpringPageableSupport` class can be injected as a Spring bean to handle conversions.

**Convert `HeaderPageable` and `QueryParamSort` to `PageRequest`:**
```java
@Autowired
private SpringPageableSupport springPageableSupport;

public PageRequest toPageRequest(HeaderPageable header, List<QueryParamSort> sorts) {
    return springPageableSupport.parse(header, sorts);
}
```

**Extract `HeaderPageable` from `PageRequest`:**
```java
@Autowired
private SpringPageableSupport springPageableSupport;

public HeaderPageable fromPageRequest(PageRequest pageRequest) {
    // The element name (e.g., "items") needs to be provided.
    return springPageableSupport.extractHeaderPageable(pageRequest, "items");
}
```

