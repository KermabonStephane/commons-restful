package com.demis27.commons.restfull;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class SortParser {

    // TODO add digit and _ in propertyName
    private static final Pattern SORT_PATTERN = Pattern.compile("[a-zA-Z]+(?::asc|:ASC|:desc|:DESC|)?(?:,[a-zA-Z]+(?::asc|:ASC|:desc|:DESC|))*");

    public List<Sort> parse(String input) {
        if (input == null || input.replaceAll(" ", "").isBlank() || !SORT_PATTERN.matcher(input.replaceAll(" ", "")).matches()) {
            throw new IllegalArgumentException("Bad format of the sorts string '%s'".formatted(input));
        }

        String[] sortsAsString = input.replaceAll(" ", "").split(",");
        return Arrays.stream(sortsAsString).map(sortAsString -> {
            String[] split = sortAsString.split(":");
            return new Sort(split[0], split.length == 1 ? SortOrder.ASC : SortOrder.valueOf(split[1].toUpperCase()));
        }).toList();
    }
}
