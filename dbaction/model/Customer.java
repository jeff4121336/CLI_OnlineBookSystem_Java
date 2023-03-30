package dbaction.model;

import java.sql.*;

public class Customer {
    private String UID;
    private String Name;
    private String Address;

    public Customer(){
        
    }

    public Customer(String UID, String Name, String Address){
        this.UID = UID;
        this.Name = Name;
        this.Address = Address;
    }

    public void insert(Connection conn) throws SQLException{
        // insert to customer
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO customer values(?,?,?)");
        pstmt.setString(1, UID);
        pstmt.setString(2, Name);
        pstmt.setString(3, Address);
        pstmt.executeUpdate();
        pstmt.close();
    }

    public int size(Connection conn) throws SQLException{
        int size=-1;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs =  stmt.executeQuery("SELECT COUNT(*) FROM customer");
            rs.next();
            size = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e+"\nin customer size");
        }
        return size;
    }
}
