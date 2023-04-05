package dbaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class dbtime {
    public static String _dbtime() {
        DateTimeFormatter d = DateTimeFormatter.ofPattern("yyyy/MM/dd||HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return d.format(now);
    }

    public static String currentDate(){
        DateTimeFormatter d = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime now = LocalDateTime.now();
        return d.format(now);
    }
}
