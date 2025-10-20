package com.demis27.commons.restful;

import java.util.regex.Pattern;

/**
 * Represents pagination information extracted from or intended for HTTP headers like `Range`, `Content-Range`, and `Accept-Ranges`.
 * This record provides a structured way to handle pagination based on HTTP range requests.
 * Example Usage:
 * Parsing a `Range` header:
 * String rangeHeader = "Range: items=0-9";
 * HeaderPageable pageable = HeaderPageable.parseRangeHeader(rangeHeader);
 * // pageable.page() will be 0
 * // pageable.size() will be 10
 * Creating a `Content-Range` header:
 * HeaderPageable pageable = new HeaderPageable("items", 0, 10, 100);
 * String contentRangeHeader = pageable.toContentRangeHeader();
 * // contentRangeHeader will be "Content-Range: items 0-9/100"
 *
 * @param elementName The name of the elements being paginated (e.g., "items", "users").
 * @param page The current page number (0-indexed). -1 if unknown.
 * @param size The number of elements per page. -1 if unknown.
 * @param total The total number of elements. -1 if unknown.
 */
public record HeaderPageable(String elementName, int page, int size, long total) {

    /**
     * Regex pattern for a `Range` header. E.g., `Range: items=0-9`.
     */
    public static final Pattern RANGE_HEADER_PATTERN = Pattern.compile("(Range: ){0,1}[a-zA-Z]+=(\\d+)-(\\d+)");
    /**
     * Regex pattern for a `Content-Range` header. E.g., `Content-Range: items 0-9/100`.
     */
    public static final Pattern CONTENT_RANGE_HEADER_PATTERN = Pattern.compile("Content-Range: [a-zA-Z]+ (\\d+)-(\\d+)/(\\d+)");
    /**
     * Regex pattern for an `Accept-Ranges` header. E.g., `Accept-Ranges: items`.
     */
    public static final Pattern ACCEPT_RANGES_HEADER_PATTERN = Pattern.compile("Accept-Ranges: [a-zA-Z]+");
    /**
     * The name of the `Range` HTTP header.
     */
    public static final String RANGE_HEADER_NAME = "Range";
    /**
     * The name of the `Content-Range` HTTP header.
     */
    public static final String CONTENT_RANGE_HEADER_NAME = "Content-Range";
    /**
     * The name of the `Accept-Ranges` HTTP header.
     */
    public static final String ACCEPT_RANGES_HEADER_NAME = "Accept-Ranges";


    /**
     * Compact constructor to validate the arguments.
     *
     * @throws IllegalArgumentException if page, size, or total have invalid values.
     */
    public HeaderPageable {
        if (page < 0 && page != -1) {
            throw new IllegalArgumentException("Page must be greater than to 0, or -1 for unknown.");
        }
        if (size == 0 || size < -1) {
            throw new IllegalArgumentException("Size must be greater than 0, or -1 for unknown.");
        }
        if (total < -1) {
            throw new IllegalArgumentException("Total must be greater than or equal to 0, or -1 for unknown.");
        }
    }

    /**
     * Parses a `Range` header string into a `HeaderPageable` object.
     * The total number of elements is set to -1 (unknown).
     *
     * @param header The `Range` header string (e.g., "Range: items=0-9").
     * @return A new `HeaderPageable` instance.
     * @throws IllegalArgumentException if the header is null, empty, or has an invalid format.
     */
    public static HeaderPageable parseRangeHeader(String header) {
        if (header == null || header.isEmpty()) {
            throw new IllegalArgumentException("Header cannot be null or empty");
        }
        if (!RANGE_HEADER_PATTERN.matcher(header).matches()) {
            throw new IllegalArgumentException("Header '" + header + "' is not in the correct format. The format must be like 'Range: elements=0-9'");
        }

        String cleanHeader = header;
        if (header.startsWith(RANGE_HEADER_NAME)) {
            cleanHeader = header.substring(RANGE_HEADER_NAME.length() + 2);
        }
        String[] parts = cleanHeader.split("=");
        String elementName = parts[0];
        String[] range = parts[1].split("-");
        long start = Long.parseLong(range[0]);
        long end = Long.parseLong(range[1]);
        if (end <= start) {
            throw new IllegalArgumentException("Header '" + header + "' is not in the correct format. The end must be greater than the start");
        }
        int size = Math.toIntExact(end - start + 1);
        int page = Math.toIntExact((start / size) + 1);

        return new HeaderPageable(elementName, page, size, -1);
    }

    /**
     * Parses a `Content-Range` header string into a `HeaderPageable` object.
     *
     * @param header The `Content-Range` header string (e.g., "Content-Range: items 0-9/100").
     * @return A new `HeaderPageable` instance.
     * @throws IllegalArgumentException if the header is null, empty, or has an invalid format.
     */
    public static HeaderPageable parseContentRangeHeader(String header) {
        if (header == null || header.isEmpty()) {
            throw new IllegalArgumentException("Header cannot be null or empty");
        }
        if (!CONTENT_RANGE_HEADER_PATTERN.matcher(header).matches()) {
            throw new IllegalArgumentException("Header '" + header + "' is not in the correct format. The format must be like 'Content-Range: elements 0-9/100'");
        }

        String[] parts = header.substring(CONTENT_RANGE_HEADER_NAME.length() + 2).split(" ");
        String elementName = parts[0];
        String[] range = parts[1].split("[-/]");
        long start = Long.parseLong(range[0]);
        long end = Long.parseLong(range[1]);
        long total = Long.parseLong(range[2]);
        if (end <= start) {
            throw new IllegalArgumentException("Header '" + header + "' is not in the correct format. The end must be greater than the start");
        }
        int size = Math.toIntExact(end - start + 1);
        int page = Math.toIntExact((start / size) + 1);

        return new HeaderPageable(elementName, page, size, total);
    }

    /**
     * Parses an `Accept-Ranges` header string into a `HeaderPageable` object.
     * The page, size, and total are set to -1 (unknown).
     *
     * @param header The `Accept-Ranges` header string (e.g., "Accept-Ranges: items").
     * @return A new `HeaderPageable` instance.
     * @throws IllegalArgumentException if the header is null, empty, or has an invalid format.
     */
    public static HeaderPageable parseAcceptRangesHeader(String header) {
        if (header == null || header.isEmpty()) {
            throw new IllegalArgumentException("Header cannot be null or empty");
        }
        if (!ACCEPT_RANGES_HEADER_PATTERN.matcher(header).matches()) {
            throw new IllegalArgumentException("Header '" + header + "' is not in the correct format. The format must be like 'Accept-Ranges: elements'");
        }

        String elementName = header.substring(ACCEPT_RANGES_HEADER_NAME.length() + 2);
        return new HeaderPageable(elementName, -1, -1, -1L);
    }

    /**
     * Converts this `HeaderPageable` object to a `Range` header string.
     *
     * @return The formatted `Range` header string (e.g., "Range: items=0-9").
     */
    public String toRangeHeader() {
        return toRangeHeader(true);
    }

    /**
     * Converts this `HeaderPageable` object to a `Range` header string.
     *
     * @param includeHeaderName Choose if you want to add the header name or not.
     * @return The formatted `Range` header string (e.g., "Range: items=0-9").
     */
    public String toRangeHeader(boolean includeHeaderName) {
        int start = page * size;
        long end = Math.min((long) (page + 1) * size - 1, total - 1);
        if (includeHeaderName) {
            return "%s: %s=%d-%d".formatted(RANGE_HEADER_NAME, elementName, start, end);
        } else {
            return "%s=%d-%d".formatted(elementName, start, end);
        }
    }

    /**
     * Converts this `HeaderPageable` object to a `Content-Range` header string.
     *
     * @return The formatted `Content-Range` header string (e.g., "Content-Range: items 0-9/100").
     */
    public String toContentRangeHeader() {
        return toContentRangeHeader(true);
    }

    /**
     * Converts this `HeaderPageable` object to a `Content-Range` header string.
     *
     * @return The formatted `Content-Range` header string (e.g., "Content-Range: items 0-9/100").
     */
    public String toContentRangeHeader(boolean includeHeaderName) {
        int start = page * size;
        long end = Math.min((long) (page + 1) * size - 1, total - 1);
        if (includeHeaderName) {
            return "%s: %s %d-%d/%d".formatted(CONTENT_RANGE_HEADER_NAME, elementName, start, end, total);
        } else {
            return "%s %d-%d/%d".formatted(elementName, start, end, total);
        }
    }

    /**
     * Converts this `HeaderPageable` object to an `Accept-Ranges` header string.
     *
     * @return The formatted `Accept-Ranges` header string (e.g., "Accept-Ranges: items").
     */
    public String toAcceptRangesHeader() {
        return "%s: %s".formatted(ACCEPT_RANGES_HEADER_NAME, elementName);
    }

    /**
     * Creates a `Builder` instance from an existing `HeaderPageable` object.
     *
     * @param headerPageable The `HeaderPageable` to copy.
     * @return A new `Builder` instance.
     */
    public static Builder toBuilder(HeaderPageable headerPageable) {
        return new Builder().elementName(headerPageable.elementName).page(headerPageable.page).size(headerPageable.size).total(headerPageable.total);
    }

    /**
     * Returns a new `HeaderPageable` for the next page.
     *
     * @return A new `HeaderPageable` for the next page.
     * @throws IndexOutOfBoundsException if this is the last page.
     */
    public HeaderPageable nextPage() {
        long lastPage = (total - 1) / size;
        if (page >= lastPage) {
            throw new IndexOutOfBoundsException("Cannot move to next page from the last page");
        }
        return toBuilder(this).page(page + 1).build();
    }

    /**
     * Returns a new `HeaderPageable` for the previous page.
     *
     * @return A new `HeaderPageable` for the previous page.
     * @throws IndexOutOfBoundsException if this is the first page.
     */
    public HeaderPageable previousPage() {
        if (page == 1) {
            throw new IndexOutOfBoundsException("Cannot move to previous page from the first page");
        }
        return toBuilder(this).page(page - 1).build();
    }

    /**
     * Returns a new `HeaderPageable` for the first page.
     *
     * @return A new `HeaderPageable` for the first page.
     */
    public HeaderPageable firstPage() {
        return new HeaderPageable(elementName, 1, size, total);
    }

    /**
     * Returns a new `HeaderPageable` for the last page.
     *
     * @return A new `HeaderPageable` for the last page.
     */
    public HeaderPageable lastPage() {
        return new HeaderPageable(elementName, Long.valueOf((total - 1) / size).intValue(), size, total);
    }

    /**
     * A builder for creating `HeaderPageable` instances.
     */
    public static class Builder {
        String elementName;
        int page;
        int size;
        long total;

        public Builder elementName(String elementName) {
            this.elementName = elementName;
            return this;
        }
        public Builder page(int page) {
            this.page = page;
            return this;
        }
        public Builder size(int size) {
            this.size = size;
            return this;
        }
        public Builder total(long total) {
            this.total = total;
            return this;
        }
        public HeaderPageable build() {
            return new HeaderPageable(elementName, page, size, total);
        }
    }
}
