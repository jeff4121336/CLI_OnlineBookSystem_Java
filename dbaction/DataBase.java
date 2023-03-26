package dbaction;
import java.sql.*;

public class DataBase {
    public DataBase() {

      final String url = "jdbc:oracle:thin:@//db18.cse.cuhk.edu.hk:1521/oradb.cse.cuhk.edu.hk";
      final String user = "h022";
      final String password = "GackTels";
      final Connection conn;
      
    try{
        System.out.println("connecting...");
        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        conn = DriverManager.getConnection(url, user, password); //connect to JDBC
        //test for query
        System.out.println( "test for query:");
        Statement stmt = conn.createStatement();
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
    }  
    /* Function 1 - Database Init */
      public void DataBaseInit() {
          /* 1. File Read io
           * 2. DataBase Connect and Init
           * 3. INSERT
           * 4. Error reporting during INSERT or File Data Type
           * 5. Delete or Reconstruct after each call of the system
           */
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
}
