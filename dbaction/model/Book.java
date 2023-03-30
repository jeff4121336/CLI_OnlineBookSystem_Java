package dbaction.model;

import java.sql.*;

public class Book {
    private String ISBN;
    private String Title;
    private String[] Authors;
    private int Price;
    private int Inventory_Quantity;

    public Book(){
        
    }

    public Book(String ISBN, String Title, String[] Authors, int Price, int Inventory_Quantity){
        this.ISBN = ISBN;
        this.Title = Title;
        this.Authors = Authors;
        this.Price = Price;
        this.Inventory_Quantity = Inventory_Quantity;
    }

    public void insert(Connection conn) throws SQLException {
        try {
            PreparedStatement pstmt_insert_book = conn.prepareStatement("INSERT INTO book values(?,?,?,?)");
            pstmt_insert_book.setString(1, ISBN);
            pstmt_insert_book.setString(2, Title);
            pstmt_insert_book.setInt(3, Price);
            pstmt_insert_book.setInt(4, Inventory_Quantity);
            pstmt_insert_book.executeUpdate();
            pstmt_insert_book.close();
        } catch (SQLException e) {
            System.out.println(e+"in book insertion");
        }
        
        PreparedStatement pstmt_insert_author = conn.prepareStatement("INSERT INTO author values(?)");
        PreparedStatement pstmt_insert_write = conn.prepareStatement("INSERT INTO write_ values(?,?)");
        for (String author : Authors) {
            // insert to author
            try {
                pstmt_insert_author.setString(1, author);
                pstmt_insert_author.executeUpdate();
            } catch (SQLException e) {
                //System.out.println(e); // should be commented out at the end // don't know how to implement "insert if not exist" so I just throw away the error if it happened
            }
            // insert to write
            try {
                pstmt_insert_write.setString(1, author);
                pstmt_insert_write.setString(2, ISBN);
                pstmt_insert_write.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e+"in write insertion");
            }
            
        }
        pstmt_insert_author.close();
        pstmt_insert_write.close();
    }

    public int size(Connection conn) throws SQLException{
        int size=-1;
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs =  stmt.executeQuery("SELECT COUNT(*) FROM book");
            rs.next();
            size = rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e+"\nin book size");
        }
        return size;
    }
}
