package dbaction;

import java.util.Scanner;

public class dbinput {

    public static int PrintScan(int min, int max, Scanner s) {
        int input = -1;
        try {
            System.out.println("Valid Range: " + min + "-" + max);
            input = s.nextInt();
            s.nextLine(); //Take the /n character
                if (input > max || input < min)
                    throw new Exception();
        } catch (NumberFormatException e) {
            System.out.println("Invalid Input!");
        } catch (Exception e) {
            System.out.println("Invalid Input!");
        } 
        return input;
    }

}
