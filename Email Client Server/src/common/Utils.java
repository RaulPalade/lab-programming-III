package common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Raul Palade
 * @project Email Client
 * @date 15/03/2021
 */
public class Utils {
    private static final String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    public static final String IP = "127.0.0.1";
    public static final int PORT = 7777;
    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String email) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        return matcher.find();
    }
}
