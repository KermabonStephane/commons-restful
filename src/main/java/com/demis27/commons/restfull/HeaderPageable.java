package com.demis27.commons.restfull;

import java.util.regex.Pattern;

public record HeaderPageable(String elementName, long page, long size, long total) {

    public static final Pattern RANGE_HEADER_PATTERN = Pattern.compile("Range: [a-zA-Z]+=(\\d+)-(\\d+)");
    public static final Pattern CONTENT_RANGE_HEADER_PATTERN = Pattern.compile("Content-Range: [a-zA-Z]+ (\\d+)-(\\d+)/(\\d+)");
    public static final Pattern ACCEPT_RANGES_HEADER_PATTERN = Pattern.compile("Accept-Ranges: [a-zA-Z]+");
    public static final String RANGE_HEADER_NAME = "Range";
    public static final String CONTENT_RANGE_HEADER_NAME = "Content-Range";
    public static final String ACCEPT_RANGES_HEADER_NAME = "Accept-Ranges";


    public HeaderPageable {
        if (page < -1) {
            throw new IllegalArgumentException("Page must be greater than or equal to 0, or -1 for unknown.");
        }
        if (size == 0 || size < -1) {
            throw new IllegalArgumentException("Size must be greater than 0, or -1 for unknown.");
        }
        if (total < -1) {
            throw new IllegalArgumentException("Total must be greater than or equal to 0, or -1 for unknown.");
        }
    }

    public static HeaderPageable parseRangeHeader(String header) {
        if (header == null || header.isEmpty()) {
            throw new IllegalArgumentException("Header cannot be null or empty");
        }
        if (!RANGE_HEADER_PATTERN.matcher(header).matches()) {
            throw new IllegalArgumentException("Header '%s' is not in the correct format. The format must be like 'Range: elements=0-9'".formatted(header));
        }

        String[] parts = header.substring(RANGE_HEADER_NAME.length() + 2).split("=");
        String elementName = parts[0];
        String[] range = parts[1].split("-");
        long start = Long.parseLong(range[0]);
        long end = Long.parseLong(range[1]);
        if (end <= start) {
            throw new IllegalArgumentException("Header '%s' is not in the correct format. The end must be greater than the start".formatted(header));
        }
        long size = end - start + 1;
        long page = start / size;

        return new HeaderPageable(elementName, page, size, -1L);
    }

    public static HeaderPageable parseContentRangeHeader(String header) {
        if (header == null || header.isEmpty()) {
            throw new IllegalArgumentException("Header cannot be null or empty");
        }
        if (!CONTENT_RANGE_HEADER_PATTERN.matcher(header).matches()) {
            throw new IllegalArgumentException("Header '%s' is not in the correct format. The format must be like 'Content-Range: elements 0-9/100'".formatted(header));
        }

        String[] parts = header.substring(CONTENT_RANGE_HEADER_NAME.length() + 2).split(" ");
        String elementName = parts[0];
        String[] range = parts[1].split("[-/]");
        long start = Long.parseLong(range[0]);
        long end = Long.parseLong(range[1]);
        long total = Long.parseLong(range[2]);
        if (end <= start) {
            throw new IllegalArgumentException("Header '%s' is not in the correct format. The end must be greater than the start".formatted(header));
        }
        long size = end - start + 1;
        long page = start / size;

        return new HeaderPageable(elementName, page, size, total);
    }

    public static HeaderPageable parseAcceptRangesHeader(String header) {
        if (header == null || header.isEmpty()) {
            throw new IllegalArgumentException("Header cannot be null or empty");
        }
        if (!ACCEPT_RANGES_HEADER_PATTERN.matcher(header).matches()) {
            throw new IllegalArgumentException("Header '%s' is not in the correct format. The format must be like 'Accept-Ranges: elements'".formatted(header));
        }

        String elementName = header.substring(ACCEPT_RANGES_HEADER_NAME.length() + 2);
        return new HeaderPageable(elementName, -1L, -1L, -1L);
    }

    public String toRangeHeader() {
        long start = page * size;
        long end = Math.min((page + 1) * size - 1, total - 1);
        return "%s: %s=%d-%d".formatted(RANGE_HEADER_NAME, elementName, start, end);
    }

    public String toContentRangeHeader() {
        long start = page * size;
        long end = Math.min((page + 1) * size - 1, total - 1);
        return "%s: %s %d-%d/%d".formatted(CONTENT_RANGE_HEADER_NAME, elementName, start, end, total);
    }

    public String toAcceptRangesHeader() {
        return "%s: %s".formatted(ACCEPT_RANGES_HEADER_NAME, elementName);
    }

    public static Builder toBuilder(HeaderPageable headerPageable) {
        return new Builder().elementName(headerPageable.elementName).page(headerPageable.page).size(headerPageable.size).total(headerPageable.total);
    }

    public HeaderPageable nextPage() {
        long lastPage = (total - 1) / size;
        if (page >= lastPage) {
            throw new IndexOutOfBoundsException("Cannot move to next page from the last page");
        }
        return toBuilder(this).page(page + 1).build();
    }

    public HeaderPageable previousPage() {
        if (page <= 0) {
            throw new IndexOutOfBoundsException("Cannot move to previous page from the first page");
        }
        return toBuilder(this).page(page - 1).build();
    }

    public HeaderPageable firstPage() {
        return new HeaderPageable(elementName, 0L, size, total);
    }

    public HeaderPageable lastPage() {
        return new HeaderPageable(elementName, (total - 1) / size, size, total);
    }

    public static class Builder {
        String elementName;
        long page;
        long size;
        long total;

        public Builder elementName(String elementName) {
            this.elementName = elementName;
            return this;
        }
        public Builder page(long page) {
            this.page = page;
            return this;
        }
        public Builder size(long size) {
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
