package utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[_A-Z0-9-\\+]+(\\.[_A-Z0-9-]+)*@[A-Z0-9-]+(\\.[A-Z0-9]+)*(\\.[A-Z]{2,6})$", Pattern.CASE_INSENSITIVE);

    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }

        return str.equals("");
    }

    public static boolean validEmail(String email) {
        if (!isEmpty(email)) {
            Matcher matcher = EMAIL_REGEX.matcher(email);
            return !matcher.matches();
        }
        return false;
    }

    public static boolean validDate(String date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            dateFormat.parse(date);
        } catch (ParseException e) {
            return true;
        }

        return false;
    }

    public static boolean validHouseNumber(String houseNumber) {
        for (int i = 0; i < houseNumber.length() - 1; i++) {
            if (!Character.isDigit(houseNumber.charAt(i))) {
                return false;
            }
        }

        if (houseNumber.length() == 1 && !Character.isDigit(houseNumber.charAt(0))) {
            return false;
        }

        return Character.isLetter(houseNumber.charAt(houseNumber.length() - 1)) ||
                Character.isDigit(houseNumber.charAt(houseNumber.length() - 1));
    }
}