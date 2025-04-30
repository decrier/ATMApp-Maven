package app.utils;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class I18n {

    private static final String PREF_KEY = "app.language";
    private static Locale locale = loadSavedLocale();
    private static ResourceBundle bundle = loadBundle(locale);

    private static ResourceBundle loadBundle(Locale locale) {
        return ResourceBundle.getBundle("i18n.messages", locale);
    }

    public static void setLocale(Locale newLocale) {
        locale = newLocale;
        bundle = loadBundle(locale);
        saveLocale(newLocale);
    }

    public static Locale getLocale() {
        return locale;
    }

    public static String get(String key) {
        return bundle.getString(key);
    }

    public static String getLanguageDisplayName() {
        return switch (locale.getLanguage()) {
            case "ru" -> "Русский";
            case "en" -> "English";
            case "de" -> "Deutsch";
            default -> "Deutsch";
        };
    }

    public static void saveLocale(Locale locale) {
        Preferences.userRoot().node("ATMApp").put(PREF_KEY, locale.getLanguage());
    }

    public static Locale loadSavedLocale () {
        String lang = Preferences.userRoot().node("ATMApp").get(PREF_KEY, "de");
        return switch (lang) {
            case "ru" -> new Locale("ru");
            case "en" -> Locale.ENGLISH;
            case "de" -> Locale.GERMAN;
            default -> Locale.ENGLISH;
        };
    }
}
