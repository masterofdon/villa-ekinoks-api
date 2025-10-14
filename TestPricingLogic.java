import java.time.LocalDate;

public class TestPricingLogic {
    
    public static void main(String[] args) {
        // Test the addDaysToDate logic for your scenario
        String existingStart = "20251001";
        String existingEnd = "20251010";
        String addStart = "20251004";
        String addEnd = "20251009";
        
        System.out.println("Existing range: " + existingStart + " to " + existingEnd);
        System.out.println("ADD range: " + addStart + " to " + addEnd);
        System.out.println();
        
        // For Case 4: ADD period is completely inside existing range
        // Should create:
        // 1. rangeBefore: existingStart to (addStart - 1 day)
        // 2. rangeAfter: (addEnd + 1 day) to existingEnd
        // 3. newRange: addStart to addEnd
        
        String beforeEnd = addDaysToDate(addStart, -1);
        String afterStart = addDaysToDate(addEnd, 1);
        
        System.out.println("Result ranges:");
        System.out.println("1st range: " + existingStart + " to " + beforeEnd);
        System.out.println("2nd range: " + addStart + " to " + addEnd + " (new range)");
        System.out.println("3rd range: " + afterStart + " to " + existingEnd);
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