package dbaction;
import java.sql.*;
import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import dbaction.model.*;



public class DataBase {  
  
  private final String url = "jdbc:oracle:thin:@//db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk";
  private final String user = "h022";
  private final String password = "GackTels";
  private Connection conn;
  
  final String[] tableName = {"purchaser", "product", "write_", "book", "customer", "order_", "author"};
  
  public void connect() throws SQLException, ClassNotFoundException{
    System.out.println("connecting...");
    DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
    this.conn = DriverManager.getConnection(url, user, password); //connect to JDBC
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
        } catch (SQLException e) {
          //System.out.println(e+query); // for dubugging: should be commented out at the end
        }
      }
      stmt.close();
    }
    private void CreateAllTables() throws SQLException{
      Statement stmt = conn.createStatement();
      String[] createTable={ "CREATE TABLE book ( ISBN VARCHAR(13) PRIMARY KEY, Title VARCHAR2(100), Price INTEGER, Inventory_Quantity INTEGER)",
      "CREATE TABLE customer (UID_ VARCHAR2(10) PRIMARY KEY, Name VARCHAR2(50), Address VARCHAR2(200))",
      "CREATE TABLE order_ (OID VARCHAR2(8) PRIMARY KEY, Order_DateTime VARCHAR2(200), Shipping_Status VARCHAR2(20))",
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
          Book.insert(conn,data[0], data[1], authors, Integer.parseInt(data[3]), Integer.parseInt(data[4]));
        }
        myReader.close();
      } catch (FileNotFoundException e) {
        System.out.println("Missing book.txt");
      } catch (Exception e) {
        System.out.println("Error when init with book.txt: " + e);
      }
      // initialize customer data
      try {
        String dataFile = "././booksystem/data/customer.txt";
        File myObj = new File(dataFile);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
          String[] data = myReader.nextLine().split("\t");
          Customer.insert(conn, data[0], data[1], data[2]);
        }
        myReader.close();
      } catch (FileNotFoundException e) {
        System.out.println("Missing customer.txt");
      } catch (Exception e) {
        System.out.println("Error when init with customer.txt: " + e);
      }
      // initialize order data
      try {
        String dataFile = "././booksystem/data/order.txt";
        File myObj = new File(dataFile);
        Scanner myReader = new Scanner(myObj);
        while (myReader.hasNextLine()) {
          String[] data = myReader.nextLine().split("\t");
          //System.out.println(data[0] + ", " + data[1] + ", " + data[2] + ", " + data[3]  + ", " + Integer.parseInt(data[4]) + ", " + data[5]);
          Order.insert(conn, data[0], data[1], data[2], data[3], Integer.parseInt(data[4]), data[5]);
        }
        myReader.close();
      } catch (FileNotFoundException e) {
        System.out.println("Missing order.txt");
      } catch (Exception e) {
        System.out.println("Error when init with order.txt: " + e);
      }
    } 

    public void DataBaseInit() throws SQLException {
      System.out.println("initializing...");
      DropAllTables();
      CreateAllTables();
      DataInit();
      System.out.println("initialization finished");

      Timer timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          try{
            Order.Order_Shipping(conn);
            System.out.println("Shipped all ordered orders");
          } catch (SQLException e) {
            System.out.println("Error for update to database: " + e);
            return;
          }
        }
      }, 30000);
    }

    
    
    public void Order_place(Scanner s) {
      System.out.println("Place an Order...");
      String[] _input, _info;
      String _name, _address;
      int bookcounter = 0;

      String[] _booklist;
      int[] _quantitylist;
      
      String uid;
      int nextOid;
      String time;
      try {
        PreparedStatement checkISBNandQuan = conn.prepareStatement("SELECT ISBN, Inventory_Quantity FROM book");
        ResultSet rs = checkISBNandQuan.executeQuery();
        _booklist = new String[Book.size(conn)];
        _quantitylist = new int[Book.size(conn)];

        if (!rs.next()) {
          System.out.println("Empty booklist, no order aviliable"); 
          return;
        } else {
          do {
            _booklist[bookcounter] = rs.getString(1);
            _quantitylist[bookcounter] = rs.getInt(2);
            bookcounter++;
          } while (rs.next());
        }
        checkISBNandQuan.close();
      } catch (Exception e) {
        System.out.println("Fail to fetch checking resources: " + e);
        return;
      }
      System.out.println("Complete fetching Book resources...");

      // Debug use
      // for (int j = 0; j < _booklist.length; j++)  
      //   System.out.println("ISBN exist: " + _booklist[j]);
      // for (int j = 0; j < _quantitylist.length; j++) 
      //   System.out.println("Quantity exist: " + _quantitylist[j]);
      
      System.out.println("Enter your order(s): (in the form [Book ISBN], [Quantity])");
      System.out.println("e.g. 2-2222-2222-2, 4, 5-4444-3333-4, 5, ....");
      String input = s.nextLine();
      _input = input.split("[,]");

      System.out.println("Are you an existing user having your own UID? (Y/N)");
      String input_isExistingUser = s.nextLine().toUpperCase();
      switch(input_isExistingUser){
        case "Y":
          System.out.println("Enter your UID: ");
          uid = s.nextLine();
          // check if uid exists
          try {
            PreparedStatement pstmt_checkUID = conn.prepareStatement("SELECT UID_ FROM customer WHERE UID_= ?");
            pstmt_checkUID.setString(1, uid);
            ResultSet rs = pstmt_checkUID.executeQuery();
            if (!rs.next()){
              System.out.println("Not exist customer");
              return;
            }
            pstmt_checkUID.close();
          } catch (SQLException e) {
            System.out.println("fail to verify the UID");
            return;
          }
          break;

        case "N":
          // System.out.println("Enter your personal info: (in the form [name], [Address without ','])");
          // System.out.println("e.g. Chan, 1/F BABC House Kwun Tong HK");
          // String info = s.nextLine();
          // _info = info.split("[,]");
          // if (_info.length != 2) {
          //   System.out.println("Invaild input for personal information, excess comma or missing information");
          //   return;
          // }
          // _name = _info[0];
          // _address = _info[1];
          System.out.println("Enter your personal info: ");
          System.out.println("- Enter your name: ");
          _name = s.nextLine();
          System.out.println("- Enter your address: (The components of the address are delimited by (,))");
          _address = s.nextLine();
          try {
            uid = Integer.toString(Customer.size(conn) + 1);
            if(!Customer.insert(conn, uid, _name, _address)) {
              System.out.println("Customer info insert failed.");
              return;
            }
            System.out.println("New customer created. Your UID is " + uid);
          } catch (SQLException e) {
            System.out.println("Customer info insert failed.");
            return;
          }
          break;

        default:
          System.out.println("Invaild input, input can only be 'Y' or 'N'");
          return;
      }

      for (int i = 0; i < _input.length; i++) {
          _input[i] = _input[i].strip();
          if (_input.length % 2 == 1) {
            System.out.println("Invaild input for order, excess comma or missing information");
            return;
          }
          
          
          if (i % 2 == 0) {
            if (!Arrays.asList(_booklist).contains(_input[i])) {
              System.out.println("Not exist ISBN detected"); 
              return;
            }
            if (!Book.isValid_ISBN(_input[i]) ) {
              System.out.println("Invaild/missing ISBN"); 
              return;
            }
          } else {
            if (!Order.isValid_Order_Quantity(Integer.parseInt(_input[i]))){
              System.out.println("Invaild/missing Quantity"); 
              return;
            }  
            /* Excess Quantity detect later (in book.update) */
          }
        }
        
        try {
          nextOid = Order.size(conn) + 1;
          time = dbtime._dbtime();
          
          int ordercount = 0;
          
          
          
          for (int i = 0; i < _input.length; i += 2, ordercount++) {
            if (!Book.update(conn, _input[i], Integer.parseInt(_input[i + 1]))) 
            return;
            if (!(Order.insert(conn, Integer.toString(nextOid + ordercount), uid, time,_input[i], Integer.parseInt(_input[i + 1]), "ordered"))) {
              System.out.println("Order insert failed.");
              return;
            }
          }      
        } catch (SQLException e) {
          System.out.println("Error for insert to database: " + e);
          return;
        }
        
        System.out.println("Order insert process finished");
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
          @Override
          public void run() {
            try{
              Order.Order_Shipping(conn);
              System.out.println("Shipped all ordered orders");
            } catch (SQLException e) {
              System.out.println("Error for update to database: " + e);
              return;
            }
          }
        }, 30000);
        return;
      }
    
    
    public void Order_history_check(Scanner s) {
      System.out.println("Check History Order by UID...");
      String _uid; 
      System.out.println("Please enter your UID to the history order"); //edit
      _uid = s.nextLine();
      
      try {
        Order.check(conn, _uid);
      } catch (SQLException e) {
        System.out.println("ERROR: "  + e);
      }
        
    }
  
  public void Book_Search(Scanner s) {
    String _isbn, _title, _authors;
    
    System.out.println("Book Searching..."); 

    System.out.println("Search by:\n" +"> 1. ISBN\n" + "> 2. Book Title\n" + "> 3. Authors\n");
    int method = dbinput.PrintScan(1, 3, s);

    switch (method) {
      case 1:
        System.out.println("Please enter the ISBN that you like to search: (in the form X-XXXX-XXXX-X)");
        _isbn = s.nextLine();   
        try {
          Book.search_by_ISBN(conn, _isbn);
        } catch (Exception e) {
          System.out.println("An error occurred: " + e);
        }
        break;
      case 2:
        System.out.println("Please enter the BookTitle that you like to search:"); 
        _title = s.nextLine();   
        try {
          Book.search_by_Title(conn, _title);
        } catch (Exception e) {
          System.out.println("An error occurred: " + e);
        }
        break;
      case 3:
        System.out.println("Please enter the Author Name of the book: (1 only)"); 
        _authors = s.nextLine();
        try {
          Book.search_by_Authors(conn, _authors);
        } catch (Exception e) {
          System.out.println("An error occurred: " + e);
        }
        break;
    }
  }

    public void Order_update(Scanner s) {
      String order_id;
      do {
          System.out.println("Please enter the order ID that you would like to update the shipping status: ");
          order_id = s.nextLine().toLowerCase();
      } while (!order_id.matches("\\d{1,8}"));
      String order_state;
      do {
          System.out.println("Enter the new status for the order (ordered, shipped, received): ");
          order_state = s.nextLine().toLowerCase();
      } while (!order_state.equals("ordered") && !order_state.equals("shipped") && !order_state.equals("received"));
      
      try {
        Order.update_shipping_status(conn, order_id, order_state);
      } catch (SQLException e) {
        System.out.println("An error occurred: "+e);
      }
    }   

    /* Fuction 4 */
    public int getBookSize() throws SQLException{
      return Book.size(conn);
    }

    public int getCustomerSize() throws SQLException{
      return Customer.size(conn);
    }

    public int getOrderSize() throws SQLException{
      return Order.size(conn);
    }

    public void Order_query() {
      try {    
        String[] statusValues = {"ordered", "shipped", "received"};
        /* Debug */
        Order.Order_Shipping(conn);
        /* Debug */
        for (String status : statusValues) {
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM ORDER_ WHERE Shipping_Status=? ORDER BY OID");
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            if (!rs.next()) {
                System.out.println("No orders in the " + status + " status.");
            }else{
              System.out.println("\nThe following orders are in the " + status + " status:");
              do {
                System.out.println("Order ID: " + rs.getString("OID") + " Order Date: " + rs.getString("Order_DateTime") + " Shipping Status: " + rs.getString("Shipping_Status"));
              }while (rs.next());
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
      int count=0;
      while (rs.next()) {
        count++;
        String isbn = rs.getString("isbn");
        String title = rs.getString("title");
        int price = rs.getInt("price");
        int totalOrderQuantity = rs.getInt("total_order_quantity");
        String authorNames = rs.getString("author_names");
    
        String output = "Quantity being ordered: " + totalOrderQuantity+ " | ISBN: "+ isbn + " | Title: " + title + " | Price: $" + price + " | Author(s): " + authorNames;
        System.out.println(output);
      }
      if (count<number) {
        System.out.println("There is only " + count + " books being ordered. Therefore only " + count + " books are shown.");
      }
      System.out.println();
      rs.close();
      pstmt.close();
      
  } catch (SQLException e) {
      System.out.println("An error occurred: " + e);
  }    
  }      

  public void Show_table(Scanner s){
    
    System.out.println("Type the table name you want to show: ");
    System.out.println("Choose: \n> 1. book \n> 2. customer \n> 3. author \n> 4. order_ \n> 5. write_ \n> 6. product \n> 7. purchaser ");

    try {    
      int _table = dbinput.PrintScan(1, 7, s);
      PreparedStatement printstmt = null;
      switch (_table) {
        case 1:
          printstmt = conn.prepareStatement("SELECT * FROM book");
          break;
        case 2:
          printstmt = conn.prepareStatement("SELECT * FROM customer");
          break;
        case 3:
          printstmt = conn.prepareStatement("SELECT * FROM author");
          break;
        case 4:
          printstmt = conn.prepareStatement("SELECT * FROM order_");
          break;
        case 5:
          printstmt = conn.prepareStatement("SELECT * FROM write_");
          break;
        case 6:
          printstmt = conn.prepareStatement("SELECT * FROM product");
          break;
        case 7:
          printstmt = conn.prepareStatement("SELECT * FROM purchaser");
          break;
      }
      ResultSet prs = printstmt.executeQuery();
      ResultSetMetaData rsmd = prs.getMetaData();
      int columnsNumber = rsmd.getColumnCount();
      
      if (prs.next() == false) { 
        System.out.println("No such table");
        return;
      } else { 
        System.out.println(" ");
        do {
          for(int i = 1 ; i <= columnsNumber; i++){
            String columnsName = rsmd.getColumnName(i);
            System.out.print(columnsName + ": " + prs.getString(i) + "  ");
          }
          System.out.println(); 
        } while(prs.next());
      }   
      printstmt.close();   
    } catch (Exception e) {
      System.out.println("Error: " + e);
    }

  }
  }
  