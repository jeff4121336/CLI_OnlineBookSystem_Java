import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.Scanner;

import java.sql.*;

/* 1. Connect to the intranet (e.g. connect to CSE VPN)
 * 2. javac -cp .\lib\ojdbc10-19.3.0.0.jar Main.java;java -cp .\lib\ojdbc10-19.3.0.0.jar  Main.java
 */
/* test */
/* https://stackoverflow.com/questions/180158/how-do-i-time-a-methods-execution-in-java 
 * https://stackoverflow.com/questions/2010284/how-to-get-the-current-date-and-time 
*/
public class Main {
  /* Some Class Here (Based on ER diagram) */
  public class Book {
    String ISBN; /* Unique */
    String Title;
    int Price;
    int Inventory;
    String[] Author;

    public Book(String _ISBN, String _Title, int _Price, int _Inventory,
                String[] _Author) {
      ISBN = _ISBN;
      Title = _Title;
      Price = _Price;
      Inventory = _Inventory;
      Author = _Author;
    }
  }

  public static class Order {
    enum Status { Ordered, Shipped, Received }
    Date OrderDate;
    String OID; /* Unique */
    Status ShippingStatus;

    public Order(Date _OrderDate, String _OID, Status _ShippingStatus) {
      OrderDate = _OrderDate;
      OID = _OID;
      ShippingStatus = _ShippingStatus;
    }
  }

  public class Customer {
    String UID; /* Unique */
    String Name;
    String Address;

    public Customer(String _UID, String _Name, String _Address) {
      UID = _UID;
      Name = _Name;
      Address = _Address;
    }
  }

  static int PrintScan() {

    DateTimeFormatter d = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();
    System.out.println("\tCurrent Time: " + d.format(now));

    System.out.println(
        "\n===== Welcome to Book Ordering Management System =====");
    System.out.println("===== Version 1.0 Last Updated: 27/02/2023 =====");
    System.out.println("===== Data Base Record: xxxx yyyy zzzzz !!!!!=====\n");
    System.out.println(
        "> 1. Database Initialization\n> 2. Customer Operation\n> 3. Bookstore Operation\n> 4. Quit\n");
    System.out.println("Please Enter Your Action:");

    boolean inputread = false;
    int input = -1;
    Scanner s = new Scanner(System.in);
    do {
      try {
        input = s.nextInt();
        if (input > 4 || input < 1)
          throw new Exception();
        inputread = true;
        s.close();
      } catch (InputMismatchException e) {
        System.out.println("Wrong Input! Please Enter Again!");
      } catch (Exception e) {
        System.out.println("Wrong Input! Please Enter Again!");
      }
    } while (inputread == false);

    return input;
  }

  /* Function 1 - Database Init */
  static void DataBaseInit() {
    /* 1. File Read io
     * 2. DataBase Connect and Init
     * 3. INSERT
     * 4. Error reporting during INSERT or File Data Type
     * 5. Delete or Reconstruct after each call of the system
     */
  }
  /* Function 2 - Customer Oper */
  static void CustomerOper() {
    /* 1. DataBase Connect and read
     * 2. Book Search -> queries -> return info of book
     * 3. Order Management -> queries -> return result of order
     * 4. Check History -> queries -> return shipping status and other info
     */
  }
  /* Fuction 3 - Bookstore Oper */
  static void BookStoreOper() {
    /* 1. DataBase Connect and read (also the history of order)
     * 2. Order update
     * 3. Order Query
     * 4. N Most Popilar Books
     */
  }

    public static class Database {
        final String url = "jdbc:oracle:thin:@//db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk";
        final String user = "h022";
        final String password = "GackTels";
        private Connection conn;

        public void connect() throws SQLException {
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            conn = DriverManager.getConnection(url, user, password);
        }
    }

    public static void main(String[] args) {
        Database db = new Database();
        try{
            System.out.println("connecting...");
            db.connect(); //connect to JDBC
            //test for query
            System.out.println( "test for query:");
            Statement stmt = db.conn.createStatement();
            String query = "SELECT * FROM Book";
            ResultSet rs = stmt.executeQuery( query );
            // loop through result tuples (rs is a cursor)
            while ( rs.next() ) {
            String s = rs.getString("ISBN");
            //Int n = rs.getInt('rating');
            System.out.println( s + ' ');
            }
        }catch(Exception e){ 
            System.out.println(e);
        } 

        int action;
        action = PrintScan();

        switch (action) {
            case 1:
              System.out.println("Database Initialization!"); /* import file construct db (txt csv tsv) */
              break;
            case 2:
              System.out.println("Customer Operation"); /* Order and also some queries here */
              break;
            case 3:
              System.out.println("Bookstore Operation"); /* Queries Book and get info */
              break;
            case 4:
              System.out.println("Quit System!"); /* quit :3 */
              break;
        }
    }
}