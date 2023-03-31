package dbaction.model;

import java.sql.*;
import java.util.Arrays;

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
    
    private boolean isValid_ISBN(String ISBN){
        String regex_ISBN = "\\d-\\d{4}-\\d{4}-\\d";
        if (!ISBN.matches(regex_ISBN)) {
            System.out.println("ISBN is not in the correct format.");
            return false;
        }
        return true;
    }

    private boolean isValid_Title(String Title){
        if (Title.isEmpty() || Title.length()>100 || Title.contains("%") || Title.contains("_")){
            System.out.println("Title is not in the correct format.");
            return false;
        }
        return true;
    }

    private boolean isValid_Authors(String[] Authors){
        if (Authors.length == 0) {
            System.out.println("Authors is not in the correct format.");
            return false;
        }
        for (String author : Authors) {
            if (author.isEmpty() || author.length()>50 || author.contains(",")){
                System.out.println("Authors is not in the correct format.");
                return false;
            }
        }
        return true;
    }

    private boolean isValid_Price(int Price){
        if (Price < 0) {
            System.out.println("Price is not in the correct format.");
            return false;
        }
        return true;
    }

    private boolean isValid_Inventory_Quantity(int Inventory_Quantity){
        if (Inventory_Quantity < 0){
            System.out.println("Inventory Quantity is not in the correct format.");
            return false;
        }
        return true;
    }

    public boolean insert(Connection conn) throws SQLException {
        boolean isInputValid=true;
        ISBN = ISBN.trim();
        Title = Title.trim();
        for (int i=0; i<Authors.length; i++) {
            Authors[i]=Authors[i].trim();
        }
        Authors = Arrays.stream(Authors).filter(str -> !str.isEmpty() && !str.contains(",")).toArray(String[]::new);
        if (!isValid_ISBN(ISBN) || !isValid_Title(Title) || !isValid_Authors(Authors) || !isValid_Price(Price) || !isValid_Inventory_Quantity(Inventory_Quantity)){
            return false;
        }
        
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
            } catch (SQLException e) {  // don't know how to implement "insert if not exist" so I just throw away the error if it happened
                //System.out.println(e); // for debugging: should be commented out at the end (it is normal to have this error)
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
        return isInputValid;
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

    public void search(Connection conn, Book b) throws SQLException{
        boolean inputerror = true;
        try {
            inputerror = isValid_ISBN(b.ISBN) && isValid_Title(b.Title) && isValid_Authors(b.Authors);
            if (!inputerror) 
                System.out.println("No result: invaild input");
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM book where ISBN = ? AND Title = ?");
            stmt.setString(1, b.ISBN);
            stmt.setString(2, b.Title);
            ResultSet rs =  stmt.executeQuery();

            /* For checking authors */
            PreparedStatement tempstmt = conn.prepareStatement("SELECT Name FROM write_ where ISBN = ?");
            tempstmt.setString(1, b.ISBN);
            ResultSet temprs = tempstmt.executeQuery();

            while (temprs.next()) 
                if (!Arrays.asList(Authors).contains(temprs.getString(1))) {
                    System.out.println("No result: Book does not exists");
                    return;
                }
             
            
            if (rs == null) 
                System.out.println("No result: Book does not exists");
            else { 
                while (rs.next()) {
                    System.out.print("Result: \n");
                    System.out.println("ISBN: " + rs.getString(1) + "\nTitle: " + rs.getString(2) 
                    + "\nAuthor: " + Arrays.toString(b.Authors) + "\nPrice: " + rs.getString(3) 
                    + "\nQuantity: " + rs.getString(4));
                }
            }
        } catch (SQLException e) {
            System.out.println("ERROR: " + e);
        }
        return;
    }
}
