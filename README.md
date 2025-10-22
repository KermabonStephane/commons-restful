# commons-restful

## Module commons-restful

Common classes to help build RESTful services. Read the [HowTo.md](HowTo.md) to know how integrates this library on your
project.

Read the [Release.md ](Release.md) for the releases.

Sonar: https://sonarcloud.io/

### Features

* **Pagination**: Handle pagination through HTTP headers (`Range`, `Content-Range`, `Accept-Ranges`, and `Link`).
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

**Generating `Link` headers for pagination:**

The `HeaderPageable` can also generate `Link` headers for `first`, `prev`, `next`, and `last` pages, which is useful for API clients to navigate through paginated results.

```java
HeaderPageable pageable = new HeaderPageable("items", 1, 10, 100);
String linkHeaders = pageable.toLinkHeaders("/api/items").toString();
// linkHeaders will be:
// "</api/items>; rel=\"first\"; range=\"0-9\", </api/items>; rel=\"previous\"; range=\"0-9\", </api/items>; rel=\"next\"; range=\"20-29\", </api/items>; rel=\"last\"; range=\"90-99\""
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

The `SpringSupport` class can be injected as a Spring bean to handle conversions.

**Parsing a `Range` header string:**
```java
@Autowired
private SpringSupport springSupport;

public PageRequest toPageRequest(String rangeHeader) {
    // rangeHeader = "items=0-9"
    return springSupport.parseFromHeader(rangeHeader);
    // returns a PageRequest for page 1, size 10, unsorted.
}
```

**Parsing a sort query parameter string:**
```java
@Autowired
private SpringSupport springSupport;

public PageRequest toPageRequest(String sort) {
    // sort = "name,age:desc"
    return springSupport.parseFromQueryParam(sort);
    // returns a PageRequest for page 1, size 10, with sorting by name ASC and age DESC.
}
```

**Parsing both `Range` and sort strings:**
```java
@Autowired
private SpringSupport springSupport;

public PageRequest toPageRequest(String rangeHeader, String sort) {
    // rangeHeader = "items=20-29", sort = "name:desc"
    return springSupport.parseFromRest(rangeHeader, sort);
    // returns a PageRequest for page 3, size 10, with sorting by name DESC.
}
```

**Converting from `HeaderPageable` and `QueryParamSort` objects:**
```java
@Autowired
private SpringSupport springSupport;

public PageRequest toPageRequest(HeaderPageable header, List<QueryParamSort> sorts) {
    return springSupport.convert(header, sorts);
}
```

**Extracting `HeaderPageable` from `PageRequest`:**
```java
@Autowired
private SpringSupport springSupport;

public HeaderPageable fromPageRequest(PageRequest pageRequest) {
    // The element name (e.g., "items") needs to be provided.
    return springSupport.extractHeaderPageable(pageRequest, "items");
}
```

**Extracting `QueryParamSort` from `PageRequest`:**
```java
@Autowired
private SpringSupport springSupport;

public List<QueryParamSort> fromPageRequest(PageRequest pageRequest) {
    return springSupport.extractSort(pageRequest);
}
```
