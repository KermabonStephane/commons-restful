package com.demis27.commons.restful.spring;

import com.demis27.commons.restful.QueryParamSort;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.data.domain.PageRequest;
import com.demis27.commons.restful.HeaderPageable;

import java.util.List;
import java.util.Spliterators;
import java.util.stream.StreamSupport;

/**
 * A Spring component that provides utility methods to convert between commons-restful pagination/sorting objects
 * and Spring Data's {@link PageRequest}.
 * This class simplifies the integration of HTTP header-based pagination and query parameter-based sorting
 * with Spring Data repositories.
 */
@Component
public class SpringSupport {

    private static final PageRequest DEFAULT_SIMPLE_PAGE_REQUEST = PageRequest.of(1, 10);

    public PageRequest parseFromHeader(String rangeHeader) {
        if (rangeHeader == null) {
            return DEFAULT_SIMPLE_PAGE_REQUEST;
        }
        return convertFromHeader(HeaderPageable.parseRangeHeader(rangeHeader));
    }

    /**
     * Converts a {@link HeaderPageable} object into a Spring Data {@link PageRequest}.
     * If the header is null, a default page request (page 1, size 10) is returned.
     *
     * @param header The {@link HeaderPageable} object parsed from HTTP headers.
     * @return A {@link PageRequest} with pagination information and unsorted order.
     */
    public PageRequest convertFromHeader(HeaderPageable header) {
        if (header == null) {
            return DEFAULT_SIMPLE_PAGE_REQUEST;
        }
        return PageRequest.of(header.page(), header.size(), Sort.unsorted());
    }

    public PageRequest parseFromQueryParam(String sorts) {
        if (sorts == null || sorts.isEmpty()) {
            return DEFAULT_SIMPLE_PAGE_REQUEST;
        }
        return convertFromQueryParamSorts(QueryParamSort.parse(sorts));
    }

    /**
     * Converts a list of {@link QueryParamSort} objects into a Spring Data {@link PageRequest} with sorting.
     * If the list is null or empty, a default page request (page 1, size 10) is returned.
     *
     * @param sorts The list of {@link QueryParamSort} objects parsed from query parameters.
     * @return A {@link PageRequest} with default pagination and specified sorting.
     */
    public PageRequest convertFromQueryParamSorts(List<QueryParamSort> sorts) {
        if (sorts == null || sorts.isEmpty()) {
            return DEFAULT_SIMPLE_PAGE_REQUEST;
        }
        return PageRequest.of(1, 10, toSort(sorts));
    }

    public PageRequest parseFromRest(String rangeHeader, String sorts) {
        if (rangeHeader == null && sorts == null) {
            return DEFAULT_SIMPLE_PAGE_REQUEST;
        }
        if (rangeHeader == null) {
            return parseFromHeader(sorts);
        }
        if (sorts == null) {
            return parseFromHeader(rangeHeader);
        }
        return convert(HeaderPageable.parseRangeHeader(rangeHeader), QueryParamSort.parse(sorts));
    }

    /**
     * Combines a {@link HeaderPageable} and a list of {@link QueryParamSort} to create a {@link PageRequest}.
     * It handles cases where either or both inputs are null by falling back to defaults or partial parsing.
     *
     * @param header The {@link HeaderPageable} for pagination.
     * @param sorts  The list of {@link QueryParamSort} for sorting.
     * @return A {@link PageRequest} containing both pagination and sorting information.
     */
    public PageRequest convert(HeaderPageable header, List<QueryParamSort> sorts) {
        if (header == null && sorts == null) {
            return DEFAULT_SIMPLE_PAGE_REQUEST;
        }
        if (header == null) {
            return convertFromQueryParamSorts(sorts);
        }
        if (sorts == null) {
            return convertFromHeader(header);
        }
        return PageRequest.of(header.page(), header.size(), toSort(sorts));
    }

    private Sort toSort(List<QueryParamSort> sorts) {
        List<Sort.Order> orders = sorts.stream().map(this::toSort).toList();
        return Sort.by(orders);
    }

    private Sort.Order toSort(QueryParamSort sort) {
        return new Sort.Order(QueryParamSort.SortOrder.ASC.equals(sort.order()) ? Sort.Direction.ASC : Sort.Direction.DESC, sort.property());
    }

    /**
     * Creates a {@link HeaderPageable} from a Spring Data {@link PageRequest}.
     * The total number of elements is set to -1, as it is typically unknown at this stage.
     *
     * @param pageRequest The {@link PageRequest} from Spring Data.
     * @param elementName The name of the resource being paginated (e.g., "items").
     * @return A {@link HeaderPageable} representing the pagination details.
     */
    public HeaderPageable extractHeaderPageable(PageRequest pageRequest, String elementName){
        return new HeaderPageable(elementName, pageRequest.getPageNumber(), pageRequest.getPageSize(), -1);
    }

    /**
     * Extracts a list of {@link QueryParamSort} from the {@link Sort} information within a {@link PageRequest}.
     *
     * @param pageRequest The {@link PageRequest} containing sorting information.
     * @return A list of {@link QueryParamSort} objects.
     */
    public List<QueryParamSort> extractSort(PageRequest pageRequest) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(pageRequest.getSort().iterator(), 0), false).map(order -> new QueryParamSort(order.getProperty(), order.getDirection() == Sort.Direction.ASC ? QueryParamSort.SortOrder.ASC : QueryParamSort.SortOrder.DESC)).toList();
    }
}
