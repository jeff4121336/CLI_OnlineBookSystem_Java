package dbaction.model;

import java.sql.*;

public class Customer {
    private String CID;
    private String Cname;
    private String Address;

    public Customer(String CID, String Cname, String Address){
        this.CID = CID;
        this.Cname = Cname;
        this.Address = Address;
    }

    public void insert(Connection conn) throws SQLException{
        // insert to customer
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO customer values(?,?,?)");
        pstmt.setString(1, CID);
        pstmt.setString(2, Cname);
        pstmt.setString(3, Address);
        pstmt.executeUpdate();
        pstmt.close();
    }
}
