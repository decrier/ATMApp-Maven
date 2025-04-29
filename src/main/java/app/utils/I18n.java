package app.utils;

import java.util.Locale;
import java.util.ResourceBundle;

public class I18n {

    private static Locale locale = Locale.getDefault();
    private static ResourceBundle bundle = loadBundle(locale);

    private static ResourceBundle loadBundle(Locale locale) {
        return ResourceBundle.getBundle("i18n.messages", locale);
    }

    public static void setLocale(Locale newLocale) {
        locale = newLocale;
    }

    public static Locale getLocale() {
        return locale;
    }

    public static String get(String key) {
        return bundle.getString(key);
    }

}
