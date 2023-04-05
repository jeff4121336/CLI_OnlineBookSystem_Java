/* Project Finished Tag */
package dbaction.model;
 
import java.sql.*;

public class Order {

    public static boolean isValid_OID(String OID){
        String regex_OID = "\\d{1,8}";
        if (OID.isEmpty() || !OID.matches(regex_OID)){
            System.out.println("OID is not in the correct format.");
            return false;
        }
        return true;
    }

    public static boolean isValid_Order_Quantity(int Order_Quantity){
        if (Order_Quantity < 0){
            System.out.println("Order Quantity is not in the correct format.");
            return false;
        }
        return true;
    }

    public static boolean isValid_Shipping_Status(String Shipping_Status){
        if (!Shipping_Status.equals("ordered") && !Shipping_Status.equals("shipped") && !Shipping_Status.equals("received")){
            System.out.println("Shipping Status is not in the correct format.");
            return false;
        }
        return true;
    }

    public static boolean insert(Connection conn, String OID, String UID, Date Order_Date, String ISBN, int Order_Quantity, String Shipping_Status) throws SQLException{
        boolean isInsertSuccess = true;
        OID = OID.trim();
        UID = UID.trim();
        ISBN = ISBN.trim();
        Shipping_Status = Shipping_Status.trim().toLowerCase();
        
        if (!isValid_OID(OID) || ! Customer.isValid_UID(UID) || ! Book.isValid_ISBN(ISBN) || !isValid_Order_Quantity(Order_Quantity) || !isValid_Shipping_Status(Shipping_Status)){
            return false;
        }
        
        // insert to order
        PreparedStatement pstmt_order = conn.prepareStatement("INSERT INTO order_ values(?,?,?)");
        try {
            pstmt_order.setString(1, OID);
            pstmt_order.setDate(2, Order_Date);
            pstmt_order.setString(3, Shipping_Status);
            pstmt_order.executeUpdate();
            pstmt_order.close();
        } catch (SQLException e) {
            System.out.println(e+"in order insertion");
            pstmt_order.close();
            return false;
        }
        // insert to product
        PreparedStatement pstmt_product = conn.prepareStatement("INSERT INTO product values(?,?,?)");
        try {
            pstmt_product.setString(1, OID);
            pstmt_product.setString(2, ISBN);
            pstmt_product.setInt(3, Order_Quantity);
            pstmt_product.executeUpdate();
            pstmt_product.close();
        } catch (SQLException e) {
            System.out.println(e+"in product insertion");
            pstmt_product.close();
            return false;
        }
        // insert to purchaser
        PreparedStatement pstmt_purchaser = conn.prepareStatement("INSERT INTO purchaser values(?,?)");
        try {
            pstmt_purchaser.setString(1, OID);
            pstmt_purchaser.setString(2, UID);
            pstmt_purchaser.executeUpdate();
            pstmt_purchaser.close();
        } catch (SQLException e) {
            System.out.println(e+"in purchaser insertion");
            pstmt_purchaser.close();
            return false;
        }
        return isInsertSuccess;
    }

    public static int size(Connection conn) throws SQLException{
        int size=-1;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs =  stmt.executeQuery("SELECT COUNT(*) FROM order_");
            rs.next();
            size = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e+"\nin order size");
        }
        return size;
    }

    public static void update_shipping_status(Connection conn,String OID,String Shipping_Status) throws SQLException{
        OID = OID.trim();
        Shipping_Status = Shipping_Status.trim();
        // update shipping status
        try {
            PreparedStatement pstmt_select = conn.prepareStatement("SELECT * FROM ORDER_ Where OID = ?");
            pstmt_select.setString(1, OID);
            ResultSet rs = pstmt_select.executeQuery();
    
            if (rs.next()) {
                String Original_status= rs.getString("SHIPPING_STATUS");
                if ((Original_status.equals("ordered") && Shipping_Status.equals("received")
                    || (Original_status.equals("ordered") && Shipping_Status.equals("shipped"))
                    || (Original_status.equals("shipped") && Shipping_Status.equals("received")))) {
                    PreparedStatement pstmt = conn.prepareStatement("UPDATE ORDER_ SET Shipping_Status = ? WHERE OID = ?");
                    pstmt.setString(1, Shipping_Status);
                    pstmt.setString(2, OID);
                    pstmt.executeUpdate();
                    pstmt.close();
                    System.out.println("SUCCESS: Shipping status of: "+OID+" is updated from "+ Original_status+ " to " +Shipping_Status);
                }else{            
                    System.out.println("ERROR: Cannot change "+ Original_status +" to "+ Shipping_Status);
                    return;
                }
            } else {
                System.out.println("ERROR: No update is done. No order with OID : "+OID+ " is found.");
                return;
            }
            
        } catch (SQLException e) {
            System.out.println(e+"in update_shipping_status");
        }

    }

    public static void check(Connection conn,String _uid) throws SQLException {
        try {
            PreparedStatement psmt = conn.prepareStatement("SELECT * FROM customer where uid_=?");
            psmt.setString(1, _uid);
            ResultSet nrs = psmt.executeQuery(); 
            if (!nrs.next()){
                System.out.println("No customer with UID " + _uid + " exixts");
            }else{
                PreparedStatement ostmt = conn.prepareStatement("SELECT order_.OID, UID_, book.ISBN, Order_Date, ORDER_QUANTITY, SHIPPING_STATUS from book, order_, purchaser, product" +
                " Where book.ISBN = product.ISBN AND order_.OID = purchaser.OID And order_.OID = product.OID And purchaser.UID_ = ?");
                ostmt.setString(1, _uid);
                ResultSet rs = ostmt.executeQuery(); /* Print result here */ 
                
                if (rs.next() == false) { 
                    System.out.println("No order for UID: " + _uid);
                    return;
                } else { 
                    do {                 
                        System.out.println("OID: " + rs.getString(1) + " UID: " + rs.getString(2) 
                        + " Date: " + rs.getDate(4).toString() + " ISBN: " + rs.getString(3)
                        + " Quantity: " + rs.getString(5) + " Status: " + rs.getString(6));
                    } while (rs.next()); 
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
        return;
    }

    public static void Order_Shipping(Connection conn) throws SQLException {
        PreparedStatement shipping_stmt = conn.prepareStatement("SELECT OID FROM ORDER_ WHERE Shipping_Status=?");
        shipping_stmt.setString(1, "ordered");
        ResultSet shipping_stmtrs = shipping_stmt.executeQuery();
        if (!shipping_stmtrs.next())  
            return;
        else {
            do {
                PreparedStatement update_stmt = conn.prepareStatement("UPDATE ORDER_ SET Shipping_Status = ? Where OID = ?");
                update_stmt.setString(1, "shipped");
                update_stmt.setString(2, shipping_stmtrs.getString(1));
                update_stmt.executeQuery();
            } while (shipping_stmtrs.next());
        }
        return;
    }

    public static int nextOidInt(Connection conn) throws SQLException{
        int max = -1;
        int oidInt;
        try {
            PreparedStatement pstmt = conn.prepareStatement("SELECT OID FROM order_");
            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                oidInt = Integer.parseInt(rs.getString(1));
                if (oidInt>max){
                    max = oidInt;
                }
            }
            pstmt.close();
        } catch (SQLException e) {
            System.out.println(e);
        }
        
        return max+1;
    }

}
