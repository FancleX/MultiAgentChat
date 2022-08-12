package com.neu.formattedPrinter;

/**
 * Print message in format.
 */
public class FormattedPrinter {

    /**
     * Print the title of a menu with a 50 length string, if the string is less than the length will be filled with "=".
     *
     * @param title the title to be printed
     */
    public static void printTitle(String title) {
        // wrap the title by two spaces
        String wrappedTitle = " " + title + " ";
        int length = wrappedTitle.length();
        String result;
        if (length < 50) {
            // filled the rest spots with "="
            int restSpots = 50 - length;
            int half = restSpots / 2;
            result = "=".repeat(half) + wrappedTitle + "=".repeat(restSpots - half);
        } else {
            result = wrappedTitle;
        }
        System.out.println(result);
    }

    /**
     * Print the end of the title.
     */
    public static void printEnd() {
        System.out.println("=".repeat(50));
    }

    /**
     * Print the line breaker.
     */
    public static void printLineBreaker() {
        System.out.println("-".repeat(50));
    }

    /**
     * Print client system message to user.
     *
     * @param msg message
     */
    public static void printSystemMessage(String msg) {
        System.out.println("[System message]: " + msg);
    }

}
