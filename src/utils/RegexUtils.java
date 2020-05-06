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
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
