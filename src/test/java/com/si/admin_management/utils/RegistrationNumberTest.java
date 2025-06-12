package com.si.admin_management.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RegistrationNumberTest {
    private RegistrationNumber registrationNumber;

    @BeforeEach
    void setUp() {
        registrationNumber = new RegistrationNumber();
    }

    @Test
    void generate_shouldPadWithFiveZeros_whenCountResultsInSingleDigit() {
        long listCount = 8;
        String entityCode = "PAT";
        String expected = "PAT-000009";

        String result = registrationNumber.generate(entityCode, listCount);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void generate_shouldPadWithFourZeros_whenCountResultsInTwoDigits() {

        long listCount = 98;
        String entityCode = "RDV";
        String expected = "RDV-000099";

        String result = registrationNumber.generate(entityCode, listCount);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void generate_shouldPadWithThreeZeros_whenCountResultsInThreeDigits() {

        long listCount = 99;
        String entityCode = "FAC";
        String expected = "FAC-000100";

        String result = registrationNumber.generate(entityCode, listCount);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void generate_shouldPadWithTwoZeros_whenCountResultsInFourDigits() {

        long listCount = 999;
        String entityCode = "USR";
        String expected = "USR-001000";

        String result = registrationNumber.generate(entityCode, listCount);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void generate_shouldPadWithOneZero_whenCountResultsInFiveDigits() {

        long listCount = 9999;
        String entityCode = "CMD";
        String expected = "CMD-010000";

        String result = registrationNumber.generate(entityCode, listCount);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void generate_shouldNotPad_whenCountResultsInSixOrMoreDigits() {

        long listCount = 99999;
        String entityCode = "TIC";
        String expected = "TIC-100000";

        String result = registrationNumber.generate(entityCode, listCount);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void generate_shouldHandleFirstItemCorrectly() {

        long listCount = 0;
        String entityCode = "FIRST";
        String expected = "FIRST-000001";

        String result = registrationNumber.generate(entityCode, listCount);

        assertThat(result).isEqualTo(expected);
    }
}
