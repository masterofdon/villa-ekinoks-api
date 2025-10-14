// Test scenario for pricing range consolidation

import java.time.LocalDate;

public class TestConsolidationLogic {
    
    public static void main(String[] args) {
        // Test scenario from user:
        // Initial: 20251001 - 20251010
        // ADD 1: 20251004 - 20251009
        // Result after ADD 1: 3 ranges
        // ADD 2: 20251007 - 20251009 (same dates as part of range from ADD 1)
        // Expected final result after consolidation
        
        System.out.println("=== Your Scenario Test ===");
        System.out.println("Initial: 20251001 - 20251010 (some price)");
        System.out.println("ADD 1: 20251004 - 20251009 (different price)");
        System.out.println("Result after ADD 1:");
        System.out.println("  1st: 20251001 - 20251003 (original price)");
        System.out.println("  2nd: 20251004 - 20251009 (new price)");
        System.out.println("  3rd: 20251010 - 20251010 (original price)");
        System.out.println();
        
        System.out.println("ADD 2: 20251007 - 20251009 (another price, but let's say it matches 3rd range price)");
        System.out.println("Result after ADD 2 (before consolidation):");
        System.out.println("  1st: 20251001 - 20251003 (400.00 EUR)");
        System.out.println("  2nd: 20251004 - 20251006 (350.00 EUR)");
        System.out.println("  3rd: 20251007 - 20251009 (400.00 EUR)");
        System.out.println("  4th: 20251010 - 20251010 (400.00 EUR)");
        System.out.println();
        
        System.out.println("After consolidation (3rd and 4th have same price and are adjacent):");
        System.out.println("  1st: 20251001 - 20251003 (400.00 EUR)");
        System.out.println("  2nd: 20251004 - 20251006 (350.00 EUR)");
        System.out.println("  3rd: 20251007 - 20251010 (400.00 EUR) <- MERGED!");
        System.out.println();
        
        // Test adjacency logic
        String date1 = "20251009";
        String date2 = "20251010";
        String nextDay = addDaysToDate(date1, 1);
        System.out.println("Adjacency test:");
        System.out.println("Date 1 end: " + date1);
        System.out.println("Next day: " + nextDay);
        System.out.println("Date 2 start: " + date2);
        System.out.println("Are adjacent? " + nextDay.equals(date2));
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