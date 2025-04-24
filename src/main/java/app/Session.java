package app;

public class Session {
    private static String fullName;
    private static String cardNumber;

    public static void setUser(String name, String card) {
        fullName = name;
        cardNumber = card;
    }

    public static String getFullName() {
        return fullName;
    }

    public static String getCardNumber() {
        return cardNumber;
    }

    public static void clear() {
        fullName = null;
        cardNumber = null;
    }
}
