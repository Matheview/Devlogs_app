package utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class RegexUtilsTest {

    @Test
    public void validateEmailTest_emptyString() {
        boolean expected = false;
        boolean actual = RegexUtils.validateEmail("");

        assertEquals(expected, actual);
    }

    @ParameterizedTest(name = "#{index} validateEmail(\"{0}\") => {1}")
    @CsvSource({
            ", false",
            "simple, false",
            "simple.pl, false",
            "simple@, false",
            "@email.pl, false",
            "simple@email, false",
            "simple@pl, false",
            "simple@.pl, false",
            "simple@email.pl, true",
            "simple@email.edu.pl, true"
    })
    public void validateEmailTest(String input, boolean expected) {
        boolean actual = RegexUtils.validateEmail(input);

        assertEquals(expected, actual);
    }
}