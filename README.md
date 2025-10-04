# commons-restfull

Common classes to help build RESTful services.

Sonar: https://sonarcloud.io/

## Features

* **Pagination**: Handle pagination through HTTP headers (`Range`, `Content-Range`, and `Accept-Ranges`).
* **Sorting**: Easily parse and apply sorting criteria from request parameters.
* **Filtering**: Parse and apply filtering criteria from request parameters.

## Usage

### Pagination

The `HeaderPageable` record helps with pagination based on HTTP Range headers.

**Parsing a `Range` header:**

```java
String rangeHeader = "Range: items=0-9";
HeaderPageable pageable = HeaderPageable.parseRangeHeader(rangeHeader);
// pageable.page() will be 0
// pageable.size() will be 10
```

**Creating a `Content-Range` header:**

```java
HeaderPageable pageable = new HeaderPageable("items", 0, 10, 100);
String contentRangeHeader = pageable.toContentRangeHeader();
// contentRangeHeader will be "Content-Range: items 0-9/100"
```

### Sorting

The `Sort` record and `SortParser` class help with sorting.

**Parsing a sort string:**

```java
String sortString = "name,age:desc";
Sort.SortParser sortParser = new Sort.SortParser();
List<Sort> sorts = sortParser.parse(sortString);
// sorts will contain [Sort[property=name, order=ASC], Sort[property=age, order=DESC]]
```

### Filtering

The `Filter` record and `FilterParser` class help with filtering.

**Parsing a filter string:**

```java
String filterString = "name eq John,age gt 25";
Filter.FilterParser filterParser = new Filter.FilterParser();
List<Filter> filters = filterParser.parse(filterString);
// filters will contain [Filter[property=name, operator=EQUALS, value=John], Filter[property=age, operator=GREATER, value=25]]
```

### Spring Boot Integration

You can use these classes in your Spring Boot controllers to handle pagination, sorting, and filtering.

```java

@GetMapping("/users")
public ResponseEntity<List<User>> getUsers(
        @RequestHeader(value = "Range", required = false) String range,
        @RequestParam(value = "sort", required = false) String sort,
        @RequestParam(value = "filter", required = false) String filter
) {
    HeaderPageable pageable = range != null ? HeaderPageable.parseRangeHeader(range) : new HeaderPageable("users", 0, 10, -1);
    List<Sort> sorts = sort != null ? new Sort.SortParser().parse(sort) : Collections.emptyList();
    List<Filter> filters = filter != null ? new Filter.FilterParser().parse(filter) : Collections.emptyList();

    // Use pageable, sorts, and filters to query your data
    // ...

    // Return a response with Content-Range header
    return ResponseEntity.ok()
            .header("Content-Range", pageable.toContentRangeHeader())
            .body(users);
}
```
