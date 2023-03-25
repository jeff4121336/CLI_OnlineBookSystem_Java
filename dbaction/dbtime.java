package dbaction;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class dbtime {
    public static void _dbtime() {
        DateTimeFormatter d = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        System.out.println("\tCurrent Time: " + d.format(now));
    }
}
