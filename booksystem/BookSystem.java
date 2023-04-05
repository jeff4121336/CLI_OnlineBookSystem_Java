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
        bookSize = db.getBookSize();
        customerSize = db.getCustomerSize();
        orderSize = db.getOrderSize();
        
        String time = dbtime._dbtime();
        System.out.println("Current Time: " + time);
        System.out.println("\n===== Welcome to Book Ordering Management System =====");
        System.out.println("===== Data Base Record: book("+ bookSize +") customer("+ customerSize+") order("+ orderSize +")=====\n");
        System.out.println("> 1. Database Initialization\n> 2. Customer Operation\n> 3. Bookstore Operation\n> 4. Show Information\n> 5. Quit");
        System.out.println("Please Enter Your Action:");   
    }

    public void OperationCall(int i, Scanner s) {
        switch (i) {
            case 1:
                try{
                    Operation_1_Menu(s);
                } catch(SQLException e){
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
            case 5:
            Operation_5_Menu(s);
            break;
        }
        if (i>0 && i<5){
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
        System.out.println("\n> 1. Book Searching\n> 2. Placing Order\n> 3. Check History Order\n> 4. Back To Main Menu");

        final int a = dbinput.PrintScan(1, 4, s);
        
        switch (a) {
            case 1:
                db.Book_Search(s);
                break;
            case 2:
                db.Order_place(s);
                break;
            case 3:
                db.Order_history_check(s);
                break;
        }
    }

    private void Operation_3_Menu(Scanner s) {
        System.out.println("\n==== BookStore Operation - Please choose from the following operation =====");
        System.out.println("\n> 1. Order Update\n> 2. Order Query\n> 3. N Most Popular Books\n> 4. Back To Main Menu"); 
        
        /* Modify the parameters in printscan if u need 
         * PLEASE DONT CHANGE THE SCANNER, Use the one pass into function, i.e. s
         * some scanner bugs will occur if multiple scanner used in an application :(3
        */

        final int a = dbinput.PrintScan(1, 4, s);
        
        // Add more if u need 
        switch (a) {
            case 1:
                db.Order_update(s);
                break;
            case 2:
                db.Order_query();
                break;
            case 3:
                db.N_most_popular_book(s);
                break;
        }
    }

    /* um... Not sure this part need what */
    private void Operation_4_Menu(Scanner s) {
        System.out.println("\n==== Show Database - Please choose the database you want to look =====");
        System.out.println("\n> 1. Show Table\n> 2. Back To Main Menu"); 
        
        /* Modify the parameters in printscan if u need 
         * PLEASE DONT CHANGE THE SCANNER, Use the one pass into function, i.e. s
         * some scanner bugs will occur if multiple scanner used in an application :(3
        */

        final int a = dbinput.PrintScan(1, 2, s);
        
        // Add more if u need 
        switch (a) {
            case 1:
                db.Show_table(s);
                break;
        }
    }

    private void Operation_5_Menu(Scanner s) {
        db.Quit_program();
        System.exit(0);
    }

}