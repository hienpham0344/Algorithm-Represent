package com.example.main.ui;

import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SortInputParserTest {

    @Test
    void parsesFlexibleRepeatedSeparators() {
        int[] values = SortInputParser.parse("1,  2\n\n3,\t4");

        assertArrayEquals(new int[]{1, 2, 3, 4}, values);
    }

    @Test
    void removesUtf8BomAndNormalizesValues() {
        int[] values = SortInputParser.parse("\uFEFF1\n280");

        assertArrayEquals(new int[]{1, 280}, values);
        assertEquals("1, 280", SortInputParser.normalize(values));
    }

    @Test
    void acceptsMinimumAndMaximumItemCounts() {
        assertArrayEquals(new int[]{1, 280}, SortInputParser.parse("1, 280"));

        String twentyFiveValues = IntStream.rangeClosed(1, 25)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(", "));
        assertEquals(25, SortInputParser.parse(twentyFiveValues).length);
    }

    @Test
    void rejectsEmptyInput() {
        SortInputValidationException error = assertThrows(
                SortInputValidationException.class,
                () -> SortInputParser.parse(" \n, ,\t"));

        assertTrue(error.getMessage().contains("at least 2"));
    }

    @Test
    void rejectsOneValue() {
        SortInputValidationException error = assertThrows(
                SortInputValidationException.class,
                () -> SortInputParser.parse("42"));

        assertTrue(error.getMessage().contains("at least 2"));
    }

    @Test
    void rejectsMoreThanTwentyFiveValues() {
        String twentySixValues = IntStream.rangeClosed(1, 26)
                .mapToObj(Integer::toString)
                .collect(Collectors.joining(" "));

        SortInputValidationException error = assertThrows(
                SortInputValidationException.class,
                () -> SortInputParser.parse(twentySixValues));

        assertTrue(error.getMessage().contains("at most 25"));
    }

    @Test
    void reportsInvalidTokenAndOneBasedPosition() {
        SortInputValidationException error = assertThrows(
                SortInputValidationException.class,
                () -> SortInputParser.parse("10, nope, 20"));

        assertEquals("nope", error.token());
        assertEquals(2, error.tokenPosition());
        assertTrue(error.getMessage().contains("position 2"));
    }

    @Test
    void reportsInvalidTokenBeforeItemCountError() {
        SortInputValidationException error = assertThrows(
                SortInputValidationException.class,
                () -> SortInputParser.parse("nope"));

        assertEquals("nope", error.token());
        assertEquals(1, error.tokenPosition());
    }

    @Test
    void reportsIntegerOverflowAsInvalidToken() {
        SortInputValidationException error = assertThrows(
                SortInputValidationException.class,
                () -> SortInputParser.parse("1, 2147483648"));

        assertEquals("2147483648", error.token());
        assertEquals(2, error.tokenPosition());
    }

    @Test
    void rejectsValuesOutsideAllowedRange() {
        SortInputValidationException belowRange = assertThrows(
                SortInputValidationException.class,
                () -> SortInputParser.parse("0, 1"));
        SortInputValidationException aboveRange = assertThrows(
                SortInputValidationException.class,
                () -> SortInputParser.parse("1, 281"));

        assertEquals("0", belowRange.token());
        assertEquals(1, belowRange.tokenPosition());
        assertTrue(belowRange.getMessage().contains("1 and 280"));
        assertEquals("281", aboveRange.token());
        assertEquals(2, aboveRange.tokenPosition());
    }
}
