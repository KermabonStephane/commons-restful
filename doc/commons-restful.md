# Module commons-restful

Common classes to help build RESTful services.

## Features

* **Pagination**: Handle pagination through HTTP headers (`Range`, `Content-Range`, `Accept-Ranges`, and `Link`).
* **Sorting**: Easily parse sorting criteria from request parameters.
* **Filtering**: Parse filtering criteria from request parameters.

## Usage

### Pagination

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
List<QueryParamFilter> filters = QueryParamFilter.parse(filterString);
// filters will contain [QueryParamFilter[property=name, operator=EQUALS, value=John], QueryParamFilter[property=age, operator=GREATER, value=25]]
```
