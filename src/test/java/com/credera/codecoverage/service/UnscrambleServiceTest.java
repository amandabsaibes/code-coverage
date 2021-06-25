package com.credera.codecoverage.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UnscrambleServiceTest {
    private UnscrambleService unscrambleService;

    @BeforeEach
    public void setUp() {
        unscrambleService = new UnscrambleService();
    }

    private static Stream<Arguments> valuesToSubtract() {
        return Stream.of(
                Arguments.arguments(60, -6),
                Arguments.arguments(15, 15),
                Arguments.arguments(10, 10),
                Arguments.arguments(6, 6)
        );
    }

    @ParameterizedTest
    @MethodSource("valuesToSubtract")
    void subtractsValues(int val, int expectedVal) {
        int subtractedVal = unscrambleService.subtract(val);
        assertEquals(subtractedVal, expectedVal);
    }

    @Test
    void translatesNumbers() {
        final char[] expectedSymbols = {'?', '+', '-', '=', ' '};

        IntStream.range(0, 42).forEachOrdered(val -> {
            char translatedVal = unscrambleService.translate(val);
            char expectedVal = Character.MIN_VALUE;

            if (val < 26) {
                expectedVal = (char) (val + 65);
            } else if (val < 36) {
                expectedVal = (char) (val + 22);
            } else if (val < 41) {
                expectedVal = expectedSymbols[40 - val];
            }

            assertEquals(translatedVal, expectedVal);
        });
    }

    private static Stream<Arguments> valuesToFindAbsoluteValue() {
        return Stream.of(
                Arguments.arguments(-4, true, 1),
                Arguments.arguments(-4, false, 4),
                Arguments.arguments(3, true, 6),
                Arguments.arguments(3, false, 3)
        );
    }

    @ParameterizedTest
    @MethodSource("valuesToFindAbsoluteValue")
    void absoluteValues(int val, boolean add, int expectedVal) {
        int absVal = unscrambleService.abs(val, add);
        assertEquals(absVal, expectedVal);
    }

    @Test
    void unscramblesMatrix() {
        final int[][] matrix = {{3, 3}, {6, 6}};
       final String expectedLines = "==\n==";

       String unscrabledMatrix = unscrambleService.unscramble(matrix);

       assertEquals(unscrabledMatrix, expectedLines);
    }
}
