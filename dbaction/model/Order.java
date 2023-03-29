package dbaction.model;

import java.sql.*;

public class Order {
     private String OID;
    private String CID;
    private Date Order_Date;
    private String ISBN;
    private int Order_Quantity;
    private String Shipping_Status;
    
    public Order(String OID, String CID, Date Order_Date, String ISBN, int Order_Quantity, String Shipping_Status){
        this.OID = OID;
        this.CID = CID;
        this.Order_Date = Order_Date;
        this.ISBN = ISBN;
        this.Order_Quantity = Order_Quantity;
        this.Shipping_Status = Shipping_Status;
    }

    public void insert(Connection conn) throws SQLException{
        // insert to order
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO order_ values(?,?,?)");
            pstmt.setString(1, OID);
            pstmt.setDate(2, Order_Date);
            pstmt.setString(3, Shipping_Status);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e+"in order insertion");
        }
        // insert to product
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO product values(?,?,?)");
            pstmt.setString(1, OID);
            pstmt.setString(2, ISBN);
            pstmt.setInt(3, Order_Quantity);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e+"in product insertion");
        }
        // insert to purchaser
        try {
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO purchaser values(?,?)");
            pstmt.setString(1, OID);
            pstmt.setString(2, CID);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e+"in purchaser insertion");
        }
    }
}
