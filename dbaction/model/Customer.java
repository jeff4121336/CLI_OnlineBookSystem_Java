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

    private boolean isValid_UID(String UID){
        if (UID.isEmpty() || UID.length()>10){
            System.out.println("UID is not in the correct format.");
            return false;
        }
        return true;
    }

    private boolean isValid_Name(String Name){
        if (Name.isEmpty() || Name.length()>50){
            System.out.println("Name is not in the correct format.");
            return false;
        }
        return true;
    }

    private boolean isValid_Address(String Address){
        if (Address.isEmpty() || Address.length()>200){
            System.out.println("Address is not in the correct format.");
            return false;
        }
        return true;
    }

    public boolean insert(Connection conn) throws SQLException{
        boolean isInputValid = true;
        UID = UID.trim();
        Name = Name.trim();
        Address = Address.trim();
        if (!isValid_UID(UID) || !isValid_Name(Name) || !isValid_Address(Address)){
            return false;
        }
        
        // insert to customer
        PreparedStatement pstmt = conn.prepareStatement("INSERT INTO customer values(?,?,?)");
        pstmt.setString(1, UID);
        pstmt.setString(2, Name);
        pstmt.setString(3, Address);
        pstmt.executeUpdate();
        pstmt.close();
        return isInputValid;
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
