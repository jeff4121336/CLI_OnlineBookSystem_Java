
import booksystem.*;
import dbaction.*;

/* 1. Connect to the intranet (e.g. connect to CSE VPN)
 * 2. javac -cp .\lib\ojdbc10-19.3.0.0.jar Main.java;java -cp .\lib\ojdbc10-19.3.0.0.jar  Main.java
 */

public class Main {
    public static void main(String[] args){
        DataBase db = new DataBase();
        BookSystem bs = new BookSystem(db);
        dbinput scan = new dbinput();
        int action;
        bs.StartingMenu();
        action = scan.PrintScan(1, 4);
        bs.OperationCall(action);
    }
}

