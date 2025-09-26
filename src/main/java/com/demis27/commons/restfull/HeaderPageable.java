package com.demis27.commons.restfull;

import java.util.regex.Pattern;

public record HeaderPageable(String elementName, Long page, Long size, Long total) {

    public final static Pattern RANGE_HEADER_PATTERN = Pattern.compile("Range: [a-zA-Z]+=(\\d+)-(\\d+)");
    public final static Pattern CONTENT_RANGE_HEADER_PATTERN = Pattern.compile("Content-Range: [a-zA-Z]+ (\\d+)-(\\d+)/(\\d+)");
    public final static Pattern ACCEPT_RANGES_HEADER_PATTERN = Pattern.compile("Accept-Ranges: [a-zA-Z]+");
    public final static String RANGE_HEADER_NAME = "Range";
    public final static String CONTENT_RANGE_HEADER_NAME = "Content-Range";
    public final static String ACCEPT_RANGES_HEADER_NAME = "Accept-Ranges";


    public HeaderPageable(String elementName, Long page, Long size, Long total) {
        this.elementName = elementName;
        this.page = page;
        this.size = size;
        this.total = total;
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
        Long start = Long.parseLong(range[0]);
        Long end = Long.parseLong(range[1]);
        if (end <= start) {
            throw new IllegalArgumentException("Header '%s' is not in the correct format. The end must be greater than the start".formatted(header));
        }
        Long size = end - start + 1;
        Long page = start / size;

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
        Long start = Long.parseLong(range[0]);
        Long end = Long.parseLong(range[1]);
        Long total = Long.parseLong(range[2]);
        if (end <= start) {
            throw new IllegalArgumentException("Header '%s' is not in the correct format. The end must be greater than the start".formatted(header));
        }
        Long size = end - start + 1;
        Long page = start / size;

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
}
