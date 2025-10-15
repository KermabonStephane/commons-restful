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
