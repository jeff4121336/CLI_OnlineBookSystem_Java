package booksystem;
import dbaction.*;

public class BookSystem{

    public BookSystem(DataBase db) {

    }

    public void StartingMenu() {
        dbtime._dbtime();
        System.out.println("\n===== Welcome to Book Ordering Management System =====");
        System.out.println("===== Version 1.0 Last Updated: 27/02/2023 =====");
        System.out.println("===== Data Base Record: xxxx yyyy zzzzz !!!!!=====\n");
        System.out.println("> 1. Database Initialization\n> 2. Customer Operation\n> 3. Bookstore Operation\n> 4. Quit\n");
        System.out.println("Please Enter Your Action:");   
    }

    public void OperationCall(int i) {
        switch (i) {
            case 1:
                Operation_1_Menu();
                break;
            case 2:
                Operation_2_Menu();
                break;
            case 3:
                Operation_3_Menu();
                break;
            case 4:
                Operation_4_Menu();
                break;
        }
    }

    private void Operation_1_Menu() {
        System.out.println("\n==== Database Initization - Please choose from the following operation =====");
        System.out.println("\n> 1. sf\n> 2. f\n> 3. x");
    }

    private void Operation_2_Menu() {
        System.out.println("\n===== Customer Operation - Please choose from the following operation =====");
        System.out.println("\n> 1. Book Searching\n> 2. Placing Order\n> 3. Check History Order");
        dbinput s = new dbinput();
        final int a = s.PrintScan(1, 3);
        
        switch (a) {
            case 1:
            //DataBase.BookSearching();
                break;
            case 2:
                Operation_2_Menu();
                break;
            case 3:
                Operation_3_Menu();
                break;
        }
    }

    private void Operation_3_Menu() {
        System.out.println("\n==== dfs - Please choose from the following operation =====");
        System.out.println("\n> 1. sf\n> 2. f\n> 3. x");
    }

    private void Operation_4_Menu() {
        System.out.println("\n==== dfs - Please choose from the following operation =====");
        System.out.println("\n> 1. sf\n> 2. f\n> 3. x");
    }

}

