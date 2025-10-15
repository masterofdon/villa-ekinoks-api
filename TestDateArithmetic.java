// Simple test to verify date arithmetic for the consecutive dates issue
import java.time.LocalDate;

public class TestDateArithmetic {
    public static void main(String[] args) {
        // Test your specific case
        String july31 = "20260731";
        String aug1 = "20260801";
        
        String nextDay = addDaysToDate(july31, 1);
        System.out.println("July 31, 2026: " + july31);
        System.out.println("Next day: " + nextDay);
        System.out.println("August 1, 2026: " + aug1);
        System.out.println("Are consecutive? " + nextDay.equals(aug1));
        
        if (nextDay.equals(aug1)) {
            System.out.println("✅ Date arithmetic is correct - ranges should consolidate");
        } else {
            System.out.println("❌ Date arithmetic issue detected");
        }
    }
    
    private static String addDaysToDate(String dateStr, int days) {
        try {
            // Parse the date string
            int year = Integer.parseInt(dateStr.substring(0, 4));
            int month = Integer.parseInt(dateStr.substring(4, 6));
            int day = Integer.parseInt(dateStr.substring(6, 8));
            
            // Create LocalDate and add/subtract days
            LocalDate date = LocalDate.of(year, month, day);
            LocalDate newDate = date.plusDays(days);
            
            // Format back to YYYYMMDD
            return String.format("%04d%02d%02d", 
                newDate.getYear(), 
                newDate.getMonthValue(), 
                newDate.getDayOfMonth());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format: " + dateStr, e);
        }
    }
}