import java.sql.SQLException;
import java.util.Scanner;

import booksystem.*;
import dbaction.*;

/* 1. Connect to the intranet (e.g. connect to CSE VPN)
 * 2.
 * Compile: 
 * javac -cp ".;lib\ojdbc10-19.3.0.0.jar" *.java
 * Run:  
 * java -cp ".;lib\ojdbc10-19.3.0.0.jar" Main.java
 * Compile and run: 
 * javac -cp ".;lib\ojdbc10-19.3.0.0.jar" *.java; java -cp ".;lib\ojdbc10-19.3.0.0.jar" Main.java
 */

public class Main {


    public static void main(String[] args){
        DataBase db = new DataBase();
        BookSystem bs = new BookSystem(db);
        final Scanner scan = new Scanner(System.in);

        try{
          db.connect();
        }catch(SQLException e){
          System.out.println(e);
        }catch(ClassNotFoundException e){
          System.out.println(e);
        }

        int action;
        do {
          try {
            bs.StartingMenu();
          } catch (SQLException e) {
            System.out.println(e);
          }
          
          // Scanner _sscan = new Scanner(System.in);
          action = dbinput.PrintScan(1, 4, scan);
          bs.OperationCall(action, scan);
        } while (action != 4 && action > 0 && action < 5);
        
        scan.close();
        try{
          db.close();
        }catch(SQLException e){
          System.out.println(e);
        }
    }
}

