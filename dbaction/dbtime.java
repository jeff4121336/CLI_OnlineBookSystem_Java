package dbaction;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dbaction.model.Order;

public class dbtime {
    public static String _dbtime() {
        DateTimeFormatter d = DateTimeFormatter.ofPattern("yyyy/MM/dd||HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return d.format(now);
    }

    public int timecount(String OrderTime) { /* Accurate to one day */
        int diff = -1;
        String TimeInDigit = OrderTime.replaceAll("[^0-9]", " ");
        String[] TimeInSlot = TimeInDigit.split(" ");
        String CurrentTime = _dbtime();
        String CurrentInDigit = CurrentTime.replaceAll("[^0-9]", " ");
        String[] CurrentInSlot = CurrentInDigit.split(" ");
        for (int i = 0; i < TimeInSlot.length - 3; i++) {
            System.out.println(TimeInSlot[i] +  ", "  + CurrentInSlot[i]);
            if (TimeInSlot[i] != CurrentInSlot[i]) 
                return 86401; /* Exceed more than one day */
        }
        diff = Integer.parseInt(CurrentInSlot[3]) * 3600  + Integer.parseInt(CurrentInSlot[4]) * 60 + Integer.parseInt(CurrentInSlot[5]) -
        (Integer.parseInt(TimeInSlot[3]) * 3600  + Integer.parseInt(TimeInSlot[4]) * 60 + Integer.parseInt(TimeInSlot[5]));

        return diff;
    } 
}
