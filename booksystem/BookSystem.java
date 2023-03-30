package booksystem;


import java.sql.SQLException;

// import java.util.Scanner;

import java.util.Scanner;

import dbaction.*;

public class BookSystem{

    /* Comment out the two lines if needed (error may occur if not commenting and not using it)
     * This will be the database we working on 
    */
    private DataBase db;

    public BookSystem(DataBase db) {
        this.db = db;
    }


    public void StartingMenu() throws SQLException {
        int bookSize=-1;
        int customerSize=-1;
        int orderSize=-1;
        bookSize = db.bookSize();
        customerSize = db.customerSize();
        orderSize = db.orderSize();
        
        dbtime._dbtime();
        System.out.println("\n===== Welcome to Book Ordering Management System =====");
        System.out.println("===== Version 1.0 Last Updated: 27/02/2023 =====");
        System.out.println("===== Data Base Record: book("+ bookSize +") customer("+ customerSize+") order("+ orderSize +")=====\n");
        System.out.println("> 1. Database Initialization\n> 2. Customer Operation\n> 3. Bookstore Operation\n> 4. Quit\n");
        System.out.println("Please Enter Your Action:");   
    }

    public void OperationCall(int i, Scanner s) {
        switch (i) {
            case 1:
                try{
                    Operation_1_Menu(s);
                }catch(SQLException e){
                    System.out.println(e);
                }
                break;
            case 2:
                Operation_2_Menu(s);
                break;
            case 3:
                Operation_3_Menu(s);
                break;
            case 4:
                Operation_4_Menu(s);
                break;
        }
        if (i>0 && i<4){
            System.out.println("Press enter to return to main menu");
            s.nextLine();
        }
    }

    private void Operation_1_Menu(Scanner s) throws SQLException {
        System.out.println("\n==== Database Initization - Loading init record from local files =====");
        /* call the function that you fetch the record
         * write the function in dbaction/DataBase.java
         * 
         * no static method for this part 
         */
        db.DataBaseInit();
    }

    private void Operation_2_Menu(Scanner s) {
        System.out.println("\n===== Customer Operation - Please choose from the following operation =====");
        System.out.println("\n> 1. Book Searching\n> 2. Placing Order\n> 3. Check History Order");

        final int a = dbinput.PrintScan(1, 3, s);
        
        switch (a) {
            case 1:
                System.out.println("hi 1");
                //DataBase.BookSearching();
                break;
            case 2:
                System.out.println("hi 2");
                break;
            case 3:
                System.out.println("hi 3");
                break;
        }
    }

    private void Operation_3_Menu(Scanner s) {
        System.out.println("\n==== BookStore Operation - Please choose from the following operation =====");
        System.out.println("\n> 1. Order Update\n> 2. Order Query\n> 3. N Most Popular Books"); 
        
        /* Modify the parameters in printscan if u need 
         * PLEASE DONT CHANGE THE SCANNER, Use the one pass into function, i.e. s
         * some scanner bugs will occur if multiple scanner used in an application :(
        */

        final int a = dbinput.PrintScan(1, 3, s);
        
        // Add more if u need 
        switch (a) {
            case 1:
                db.Order_update(s);
                break;
            case 2:
                db.Order_query();
                break;
            case 3:
                // ur func
                break;
        }
    }

    /* um... Not sure this part need what */
    private void Operation_4_Menu(Scanner s) {
        
    }

}

