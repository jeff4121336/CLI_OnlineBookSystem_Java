package dbaction;
import java.sql.*;
import java.io.*;
import java.util.Scanner;

import dbaction.model.*;

public class DataBase {
  
  final String url = "jdbc:oracle:thin:@//db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk";
  final String user = "h022";
  final String password = "GackTels";
  private Connection conn;
  
  final String[] tableName = {"purchaser", "product", "write_", "book", "customer", "order_", "author"};
  
  public void connect() throws SQLException, ClassNotFoundException{
    System.out.println("connecting...");
    DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
    this.conn = DriverManager.getConnection(url, user, password); //connect to JDBC
    // //test for query
    // System.out.println( "test for query:");
    // Statement stmt = conn.createStatement();
    // String query = "SELECT * FROM book";
    // ResultSet rs = stmt.executeQuery( query );
    // // loop through result tuples (rs is a cursor)
    // while ( rs.next() ) {
      // String s = rs.getString("ISBN");
      // //Int n = rs.getInt('rating');
      // System.out.println( s + ' ');
      // }
    }
    
    public void close() throws SQLException{
      this.conn.close();
    }
    
    private void DropAllTables() throws SQLException {
      Statement stmt = conn.createStatement();
      // drop all tables
      for (String table : tableName) {
        String query = "DROP TABLE "+ table;
        try {
          stmt.execute(query);
        } catch (SQLException e) {  // don't know how to implement "drop if table exist" so I just throw away the error if it happened
          System.out.println(e+query); // for dubugging: should be commented out at the end
        }
      }
      stmt.close();
    }
    private void CreateAllTables() throws SQLException{
      Statement stmt = conn.createStatement();
      String[] createTable={ "CREATE TABLE book ( ISBN VARCHAR(13) PRIMARY KEY, Title VARCHAR2(100), Price INTEGER, Inventory_Quantity INTEGER)",
      "CREATE TABLE customer (UID_ VARCHAR2(10) PRIMARY KEY, Name VARCHAR2(50), Address VARCHAR2(200))",
      "CREATE TABLE order_ (OID VARCHAR2(8) PRIMARY KEY, Order_Date DATE, Shipping_Status VARCHAR2(20))",
      "CREATE TABLE author (Name VARCHAR2(50) PRIMARY KEY)",
      "CREATE TABLE write_ (Name VARCHAR2(50),ISBN VARCHAR2(13),PRIMARY KEY (Name, ISBN),FOREIGN KEY (Name) REFERENCES author(Name),FOREIGN KEY (ISBN) REFERENCES book(ISBN))",
      "CREATE TABLE product (OID VARCHAR2(8),ISBN VARCHAR2(13),Order_Quantity INTEGER,PRIMARY KEY (OID, ISBN),FOREIGN KEY (OID) REFERENCES order_(OID),FOREIGN KEY (ISBN) REFERENCES book(ISBN))",
      "CREATE TABLE purchaser(OID VARCHAR2(8),UID_ VARCHAR2(10),PRIMARY KEY (OID),FOREIGN KEY (OID) REFERENCES order_(OID),FOREIGN KEY (UID_) REFERENCES customer(UID_))"};
      for (String query : createTable) {
        try {
          stmt.execute(query);
        } catch (SQLException e) {
          System.out.println(e+query); // for debugging: can be deleted later
        }
      }
      stmt.close();
    }

    private void DataInit() throws SQLException {
      // initialize book data
      try {
        String dataFile = "././booksystem/data/book.txt";
        File myObj = new File(dataFile);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
          String[] data = myReader.nextLine().split("\t");
          String[] authors = data[2].split(",");
          Book book = new Book(data[0], data[1], authors, Integer.parseInt(data[3]), Integer.parseInt(data[4]));
          book.insert(conn);
        }
        myReader.close();
      } catch (FileNotFoundException e) {
        System.out.println("An error occurred: "+e);
      }
      // initialize customer data
      try {
        String dataFile = "././booksystem/data/customer.txt";
        File myObj = new File(dataFile);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
          String[] data = myReader.nextLine().split("\t");
          Customer customer = new Customer(data[0], data[1],data[2]);
          customer.insert(conn);
        }
        myReader.close();
      } catch (FileNotFoundException e) {
        System.out.println("An error occurred: "+e);
      }
      // initialize order data
      try {
        String dataFile = "././booksystem/data/order.txt";
        File myObj = new File(dataFile);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
          String[] data = myReader.nextLine().split("\t");
          Date date = Date.valueOf(data[2]);
          Order order = new Order(data[0], data[1], date, data[3],Integer.parseInt(data[4]) ,data[5]);
          order.insert(conn);
        }
        myReader.close();
      } catch (FileNotFoundException e) {
        System.out.println("An error occurred: "+e);
      }
    }
    /* Function 1 - Database Init */
    public void DataBaseInit() throws SQLException {
      /* 1. File Read io
      * 2. DataBase Connect and Init
      * 3. INSERT
      * 4. Error reporting during INSERT or File Data Type
      * 5. Delete or Reconstruct after each call of the system
      */
      System.out.println("initializing...");
      DropAllTables();
      CreateAllTables();
      DataInit();
      System.out.println("initialization finished");
    }
    /* Function 2 - Customer Oper */
    public void BookSearching() {
      System.out.println("Book Seraching!");
      //PreparedStatement cstmt = conn.prepareStatement("");
      //cstmt.setString();
      //cstmt.setString();
      //...
      //ResultSet a = cstmt.executeQuery();
      
    }
    public void PlacingOrder() {
      System.out.println("Placing Order!");
      //PreparedStatement cstmt = conn.prepareStatement("");
      //cstmt.setString();
      //cstmt.setString();
      //...
      //ResultSet a = cstmt.executeQuery();
    }
    public void CheckHistoryOrder() {
      System.out.println("CheckHistoryOrder");
      //PreparedStatement cstmt = conn.prepareStatement("");
      //cstmt.setString();
      //cstmt.setString();
      //...
      //ResultSet a = cstmt.executeQuery();
    }
    /* Fuction 3 - Bookstore Oper */
    public void BookStoreOper() {
      /* 1. DataBase Connect and read (also the history of order)     
      * 2. Order update
      * 3. Order Query
      * 4. N Most Popilar Books
      */
    }   
    public void Order_update(Scanner s) {
      String order_id;
      do {
          System.out.println("Please enter the order ID that you would like to update the shipping status: (in the form XXXXXXXX)");
          order_id = s.nextLine().toLowerCase();
      } while (!order_id.matches("[0-9]{8}"));
      String order_state;
      do {
          System.out.println("Enter the new status for the order (ordered, shipped, received): ");
          order_state = s.nextLine().toLowerCase();
      } while (!order_state.equals("ordered") && !order_state.equals("shipped") && !order_state.equals("received"));
      
      try {
        Order order = new Order();
        order.update_shipping_status(conn, order_id, order_state);
      } catch (SQLException e) {
        System.out.println("An error occurred: "+e);
      }
    }   

    /* Fuction 4 */
    public int bookSize() throws SQLException{
      Book book = new Book();
      return book.size(conn);
    }

    public int customerSize() throws SQLException{
      Customer customer = new Customer();
      return customer.size(conn);
    }

    public int orderSize() throws SQLException{
      Order order = new Order();
      return order.size(conn);
    }
    public void Order_query() {
      try {    
        String[] statusValues = {"ordered", "shipped", "received"};

        for (String status : statusValues) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ORDER_ WHERE Shipping_Status=? ORDER BY OID");
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("\nThe following orders are in the " + status + " status:");
            while (rs.next()) {
                System.out.println("Order ID: " + rs.getString("OID") + " Order Date: " + rs.getDate("Order_Date") + " Shipping Status: " + rs.getString("Shipping_Status"));
            }
            rs.close();
            pstmt.close();
        }
    } catch (SQLException e) {
        System.out.println("An error occurred: " + e);
    }    
    }

  public void N_most_popular_book(Scanner s) {
    try {    
      int number = 0;
      do {
        System.out.print("Enter a positive integer: ");
        while (!s.hasNextInt()) {
          System.out.println("That's not a valid integer. Try again.");
          s.next();
        }
        number = s.nextInt();
        s.nextLine(); // consume the newline character
      } while (number <= 0);
    
      String sql = 
      "SELECT b.isbn, b.title, b.price, total.total_order_quantity, authors.author_names " +
      "FROM book b " +
      "JOIN ( " +
      "    SELECT w.isbn, LISTAGG(w.name, ', ') WITHIN GROUP (ORDER BY w.name) as author_names " +
      "    FROM write_ w " +
      "    GROUP BY w.isbn " +
      ") authors ON b.isbn = authors.isbn " +
      "JOIN ( " +
      "    SELECT p.isbn, SUM(p.order_quantity) as total_order_quantity " +
      "    FROM product p " +
      "    GROUP BY p.isbn " +
      "    ORDER BY total_order_quantity DESC " +
      "    FETCH FIRST ? ROWS ONLY " +
      ") total ON b.isbn = total.isbn " +
      "ORDER BY total_order_quantity DESC";


      PreparedStatement pstmt = conn.prepareStatement(sql);
      pstmt.setInt(1, number);
      ResultSet rs = pstmt.executeQuery();
      System.out.println("\nThe " + number + " most popular books are:");
      while (rs.next()) {
        String isbn = rs.getString("isbn");
        String title = rs.getString("title");
        double price = rs.getDouble("price");
        int totalOrderQuantity = rs.getInt("total_order_quantity");
        String authorNames = rs.getString("author_names");
    
        String output = totalOrderQuantity+ " | "+ isbn + " | " + title + " | " + price + " | " + authorNames;
        System.out.println(output);
      }
      System.out.println();
      rs.close();
      pstmt.close();
      
  } catch (SQLException e) {
      System.out.println("An error occurred: " + e);
  }    
  }      
  }
  