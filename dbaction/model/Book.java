/* Project Finished Tag */
package dbaction.model;

import java.sql.*;
import java.util.Arrays;

public class Book {
    
    public static boolean isValid_ISBN(String ISBN){
        String regex_ISBN = "\\d-\\d{4}-\\d{4}-\\d";
        if (!ISBN.matches(regex_ISBN)) {
            System.out.println("ISBN is not in the correct format.");
            return false;
        }
        return true;
    }

    public static boolean isValid_Title(String Title){
        if (Title.isEmpty() || Title.length()>100 || Title.contains("%") || Title.contains("_")){
            System.out.println("Title is not in the correct format.");
            return false;
        }
        return true;
    }

    public static boolean isValid_Authors(String[] Authors){
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

    public static boolean isValid_Price(int Price){
        if (Price < 0) {
            System.out.println("Price is not in the correct format.");
            return false;
        }
        return true;
    }

    public static boolean isValid_Inventory_Quantity(int Inventory_Quantity){
        if (Inventory_Quantity < 0){
            System.out.println("Inventory Quantity is not in the correct format.");
            return false;
        }
        return true;
    }

    public static boolean insert(Connection conn, String ISBN, String Title, String[] Authors, int Price, int Inventory_Quantity) throws SQLException {
        boolean isInsertSuccess=true;
        ISBN = ISBN.trim();
        Title = Title.trim();
        for (int i=0; i<Authors.length; i++) {
            Authors[i]=Authors[i].trim();
        }
        Authors = Arrays.stream(Authors).filter(str -> !str.isEmpty() && !str.contains(",")).toArray(String[]::new);
        if (!isValid_ISBN(ISBN) || !isValid_Title(Title) || !isValid_Authors(Authors) || !isValid_Price(Price) || !isValid_Inventory_Quantity(Inventory_Quantity)){
            return false;
        }
        PreparedStatement pstmt_insert_book = conn.prepareStatement("INSERT INTO book values(?,?,?,?)");
        try {
            pstmt_insert_book.setString(1, ISBN);
            pstmt_insert_book.setString(2, Title);
            pstmt_insert_book.setInt(3, Price);
            pstmt_insert_book.setInt(4, Inventory_Quantity);
            pstmt_insert_book.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e+"in book insertion");
            return false;
        }
        pstmt_insert_book.close();

        PreparedStatement pstmt_insert_author = conn.prepareStatement("INSERT INTO author values(?)");
        PreparedStatement pstmt_insert_write = conn.prepareStatement("INSERT INTO write_ values(?,?)");
        PreparedStatement pstmt_find_author = conn.prepareStatement("SELECT name FROM author WHERE name = ?");
        for (String author : Authors) {
            try {
                // check author not exist
                pstmt_find_author.setString(1, author);
                ResultSet rs = pstmt_find_author.executeQuery();
                if (!rs.next()){
                    // insert to author
                    pstmt_insert_author.setString(1, author);
                    pstmt_insert_author.executeUpdate();
                }
            } catch (SQLException e) {
                System.out.println(e+"in author insertion");
                pstmt_find_author.close();
                pstmt_insert_author.close();
                pstmt_insert_write.close();
                return false;
            }
            // insert to write
            try {
                pstmt_insert_write.setString(1, author);
                pstmt_insert_write.setString(2, ISBN);
                pstmt_insert_write.executeUpdate();
            } catch (SQLException e) {
                System.out.println(e+"in write insertion");
                pstmt_find_author.close();
                pstmt_insert_author.close();
                pstmt_insert_write.close();
                return false;
            }
            
        }
        pstmt_find_author.close();
        pstmt_insert_author.close();
        pstmt_insert_write.close();
        return isInsertSuccess;
    }
    
    public static int size(Connection conn) throws SQLException{
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

    private static void fetch_authors(Connection conn ,String ISBN) throws SQLException {
        PreparedStatement tempstmt = conn.prepareStatement("SELECT Name FROM write_ where ISBN = ?");
        tempstmt.setString(1, ISBN);
        ResultSet temprs = tempstmt.executeQuery();
        String[] authorlist = new String[20];
        int count = 0;

        while (temprs.next()) {
            authorlist[count++] = temprs.getString(1);
        }

        System.out.print("All Author(s):");
        for (String i : authorlist) {
            if (i == null) {
                System.out.println("\n");
                return; //end of author list
            }
            System.out.print("  " + i);
        }
    
        return;
    }
    public static void search_by_Title(Connection conn, String Title) throws SQLException{
        try {
            if (!isValid_Title(Title)) {
                System.out.println("No result: invaild input");
                return;
            }
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM book WHERE LOWER(Title) LIKE LOWER(?)");
            stmt.setString(1, "%" + Title + "%");
            ResultSet rs = stmt.executeQuery();               
            if (!rs.next()) {
                System.out.println("No result: Book does not exists");
                return;
            }else{
                do{
                    System.out.print("Result: \n");
                    System.out.println("ISBN: " + rs.getString(1) + "\nTitle: " + rs.getString(2) 
                    + "\nPrice: " + rs.getString(3) + "\nQuantity: " + rs.getString(4));
                    fetch_authors(conn ,rs.getString(1));
                } while (rs.next()); 
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.out.println("ERROR: " + e);
        }
        return;
    }

    public static void search_by_Authors(Connection conn, String AuthorName) throws SQLException{
        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM book, write_ WHERE book.ISBN = write_.ISBN AND LOWER(write_.Name) LIKE ?");
            stmt.setString(1, "%" + AuthorName.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();            
            
            if (!rs.next()) {
                System.out.println("No result: Book does not exists");
                return;
            } else {
                do {
                    System.out.print("Result: \n");
                    System.out.println("ISBN: " + rs.getString(1) + "\nTitle: " + rs.getString(2) 
                    + "\nPrice: " + rs.getString(3) 
                    + "\nQuantity: " + rs.getString(4));
                    fetch_authors(conn, rs.getString(1));
                } while (rs.next());
            }
        } catch (SQLException e) {
            System.out.println("ERROR: " + e);
        }
        return;
    }

    public static void search_by_ISBN(Connection conn, String ISBN) throws SQLException{
        try {
            if (!isValid_ISBN(ISBN)) {
                System.out.println("No result: invaild input");
                return;
            }
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM book where ISBN = ? ");
            stmt.setString(1, ISBN);
            ResultSet rs =  stmt.executeQuery();
            
            while (rs.next()) {
                System.out.print("Result: \n");
                System.out.println("ISBN: " + rs.getString(1) + "\nTitle: " + rs.getString(2) 
                + "\nPrice: " + rs.getString(3) + "\nQuantity: " + rs.getString(4));
                fetch_authors(conn, rs.getString(1));
                return;
            }
        } catch (SQLException e) {
            System.out.println("ERROR: " + e);
        }
        System.out.println("No result: Book does not exists");
        return;
    }

    public static boolean update(Connection conn, String ISBN, int orderquantity) {
        try {
            int originalvalue = 0;
            PreparedStatement temp = conn.prepareStatement("SELECT Inventory_Quantity From Book where ISBN = ?");
            temp.setString(1, ISBN);
            ResultSet temprs = temp.executeQuery();
            if (temprs.next())
                originalvalue = temprs.getInt(1);
            else {
                System.out.println("Error in update quantity of book after order");
                return false;    
            }
            if (originalvalue - orderquantity < 0) {
                System.out.println("Excess quantity has been entered. There are only " + originalvalue 
                + " book (for ISBN: " + ISBN + ")in our library, but you ordered " + orderquantity + ".");
                System.out.println("Previous order(s) has been accepted, but current/further order(s) has been aborted");
                return false;
            }

            PreparedStatement stmt = conn.prepareStatement("UPDATE book SET Inventory_Quantity = ? where ISBN = ?");
            stmt.setInt(1, originalvalue - orderquantity);
            stmt.setString(2, ISBN);
            stmt.executeQuery();
        } catch (Exception e) {
            System.out.println("Error in update quantity of book after order: " + e);
            return false;    
        }
        return true;    
    }
}