# commons-restful

Common classes to help build RESTful services. Read the [HowTo.md](HowTo.md) to know how integrates this library on your
project.

Read the [Release.md ](Release.md) for the releases.

Sonar: https://sonarcloud.io/

## Features

* **Pagination**: Handle pagination through HTTP headers (`Range`, `Content-Range`, and `Accept-Ranges`).
* **Sorting**: Easily parse sorting criteria from request parameters.
* **Filtering**: Parse filtering criteria from request parameters.

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

The `QueryParamSort` record help with sorting.

**Parsing a sort string:**

```java
String sortString = "name,age:desc";
List<QueryParamSort> sorts = QueryParamSort.parse(sortString);
// sorts will contain [QueryParamSort[property=name, order=ASC], QueryParamSort[property=age, order=DESC]]
```

### Filtering

The `QueryParamFilter` record help with filtering.

**Parsing a filter string:**

```java
String filterString = "name eq John,age gt 25";
List<Filter> filters = QueryParamFilter.parse(filterString);
// filters will contain [QueryParamFilter[property=name, operator=EQUALS, value=John], QueryParamFilter[property=age, operator=GREATER, value=25]]
```
