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

    public static int timecount(String OrderTime) { /* Accurate to one day */
        int diff = -1;
        String TimeInDigit = OrderTime.replaceAll("[^0-9]", " ");
        String[] TimeInSlot = TimeInDigit.split(" ");
        String CurrentTime = _dbtime();
        String CurrentInDigit = CurrentTime.replaceAll("[^0-9]", " ");
        String[] CurrentInSlot = CurrentInDigit.split(" ");
        // System.out.println(TimeInDigit +  ", "  + CurrentInDigit);
        // System.out.println(TimeInSlot.length);

        for (int i = 0; i < 3; i++) {
            if (Integer.parseInt(TimeInSlot[i]) >  Integer.parseInt(CurrentInSlot[i])) 
                return -1; /* in the future, testing purpose only */
            if (!TimeInSlot[i].equals(CurrentInSlot[i]))  {
                // System.out.println("more than one day");
                return 86401; /* Exceed more than one day */
            }
        }

        diff = Integer.parseInt(CurrentInSlot[4]) * 3600  + Integer.parseInt(CurrentInSlot[5]) * 60 + Integer.parseInt(CurrentInSlot[6]) -
        (Integer.parseInt(TimeInSlot[4]) * 3600  + Integer.parseInt(TimeInSlot[5]) * 60 + Integer.parseInt(TimeInSlot[6]));
        // System.out.println("time diff: " + diff);
        return diff;
    } 
}
