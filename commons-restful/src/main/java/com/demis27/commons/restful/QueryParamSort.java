package com.demis27.commons.restful;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Represents a sort criterion for a query.
 * A sort criterion is composed of a `property` and a `SortOrder`.
 * For example, to sort by name in ascending order, you would use:
 * new Sort("name", Sort.SortOrder.ASC);
 *
 * @param property The name of the property to sort on.
 * @param order    The sort order (ascending or descending).
 */
public record QueryParamSort(String property, SortOrder order) {

    private static final Pattern SORT_PATTERN = Pattern.compile("(?i)[a-z0-9_]+(?::(?:asc|desc))?(?:,[a-z0-9_]+(?::(?:asc|desc))?)*");

    /**
     * The supported sort orders.
     */
    public enum SortOrder {
        /**
         * Ascending order.
         */
        ASC,
        /**
         * Descending order.
         */
        DESC
    }

    /**
     * Parses a sort string into a list of `Sort` objects.
     * The sort string should be a comma-separated list of individual sort criteria.
     * Each individual sort criterion should be in the format: `property[:order]`.
     * If the order is not specified, it defaults to ascending (ASC).
     * For example:
     * "name,age:desc"
     *
     * @param input The sort string to parse.
     * @return A list of `Sort` objects.
     * @throws IllegalArgumentException if the sort string is null, blank, or invalid.
     */
    public static List<QueryParamSort> parse(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("Bad format of the sorts string '%s'".formatted(input));
        }
        String cleanInput = input.replace(" ", "");
        if (cleanInput.isBlank() || !SORT_PATTERN.matcher(cleanInput).matches()) {
            throw new IllegalArgumentException("Bad format of the sorts string '%s'".formatted(input));
        }

        String[] sortsAsString = cleanInput.split(",");
        return Arrays.stream(sortsAsString).map(sortAsString -> {
            String[] split = sortAsString.split(":");
            return new QueryParamSort(split[0], split.length == 1 ? SortOrder.ASC : SortOrder.valueOf(split[1].toUpperCase()));
        }).toList();
    }
}
