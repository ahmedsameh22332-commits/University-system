package menu;

import java.util.Scanner;


public class InputHelper {

    public static final Scanner INPUT = new Scanner(System.in);

    private InputHelper() {
    }

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return INPUT.nextLine();
    }

    public static int readIntChoice(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = INPUT.nextLine();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static int readIntInRange(String prompt, String errorMessage, int min, int max) {
        while (true) {
            int value = readIntChoice(prompt);
            if (value >= min && value <= max) return value;
            System.out.println(errorMessage);
        }
    }

    public static float readFloat(String prompt) {
        while (true) {
            System.out.print(prompt);
            String line = INPUT.nextLine();
            try {
                return Float.parseFloat(line);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    public static float readFloatInRange(String prompt, String errorMessage, float min, float max) {
        while (true) {
            float value = readFloat(prompt);
            if (value >= min && value <= max) return value;
            System.out.println(errorMessage);
        }
    }
}
