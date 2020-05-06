package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Klasa zawierająca funkcje wykożystujące wyrażenia regularne
 */
public class RegexUtils {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    /**
     * Funkcja sprawdzająca poprawność adresu email.
     * @param emailStr Adres email
     * @return True, jeśli adres email jest poprawny lub falese w przeciwnym wypadku
     */
    public static boolean validateEmail(String emailStr) {
        return validate(emailStr, VALID_EMAIL_ADDRESS_REGEX);
    }

    /**
     * Funkcja sprawdzająca poprawność podanego łańcucha znakowego, korzystająca z wyrażenia regularnego.
     * Korzystają z niej inne metody.
     * @param value Łańcuch znakowy do sprawdzenia.
     * @param pattern Wyrażenie regularne.
     * @return True, jeśli łańcuch w pełni pasuje do wyrażenia, false w przeciwnym wypadku.
     */
    private static boolean validate(String value, Pattern pattern) {
        Matcher matcher = pattern.matcher(value);
        return matcher.find();
    }
}
