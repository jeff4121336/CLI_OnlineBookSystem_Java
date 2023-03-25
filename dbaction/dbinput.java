package dbaction;

import java.util.InputMismatchException;
import java.util.Scanner;

public class dbinput {
    
    public int PrintScan(int min, int max) {
        int input = 0;
        Scanner s = new Scanner(System.in);
        try {
            input = s.nextInt();
            s.close();
            System.out.println(input);
            if (input > max || input < min)
                throw new Exception();
        } catch (InputMismatchException e) {
            System.out.println("Invalid Input Type! Please Enter Again!");
        } catch (Exception e) {
            System.out.println("Invalid Input Range! Please Enter Again!");
        }
    
        return input;
    }
}
