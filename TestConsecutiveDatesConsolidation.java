/**
 * Test case to verify the specific consolidation issue reported by the user:
 * Adding single date from 20260731 to 20260731
 * Then adding single date from 20260801 to 20260801
 * These should consolidate into a single range [20260731 - 20260801]
 */
import java.time.LocalDate;

public class TestConsecutiveDatesConsolidation {
    
    public static void main(String[] args) {
        System.out.println("=== Testing Consecutive Single Dates Consolidation ===");
        System.out.println();
        
        // User's specific scenario
        String date1 = "20260731"; // July 31, 2026
        String date2 = "20260801"; // August 1, 2026
        
        System.out.println("Step 1: Add single date range [" + date1 + " - " + date1 + "]");
        System.out.println("Step 2: Add single date range [" + date2 + " - " + date2 + "]");
        System.out.println();
        
        // Test adjacency logic
        System.out.println("Testing adjacency logic:");
        System.out.println("Date 1 end: " + date1);
        String nextDay = addDaysToDate(date1, 1);
        System.out.println("Next day after date 1: " + nextDay);
        System.out.println("Date 2 start: " + date2);
        System.out.println("Are consecutive (adjacent)? " + nextDay.equals(date2));
        System.out.println();
        
        if (nextDay.equals(date2)) {
            System.out.println("✅ CONSOLIDATION SHOULD WORK: Dates are adjacent");
            System.out.println("Expected result: [" + date1 + " - " + date2 + "] (single consolidated range)");
        } else {
            System.out.println("❌ CONSOLIDATION ISSUE: Dates are NOT adjacent");
            System.out.println("Gap between ranges: " + calculateDaysBetween(date1, date2) + " days");
        }
        
        System.out.println();
        System.out.println("=== Testing Edge Cases ===");
        
        // Test month boundary
        testMonthBoundary("20260228", "20260301"); // February to March (non-leap year)
        testMonthBoundary("20240229", "20240301"); // February to March (leap year)
        testMonthBoundary("20260430", "20260501"); // April to May
        testMonthBoundary("20260531", "20260601"); // May to June
        testMonthBoundary("20261231", "20270101"); // Year boundary
    }
    
    private static void testMonthBoundary(String date1, String date2) {
        System.out.println("Testing: " + date1 + " -> " + date2);
        String nextDay = addDaysToDate(date1, 1);
        boolean adjacent = nextDay.equals(date2);
        System.out.println("  Next day: " + nextDay + ", Adjacent: " + adjacent);
        if (!adjacent) {
            System.out.println("  ❌ POTENTIAL ISSUE: Not adjacent across month boundary");
        }
    }
    
    private static String addDaysToDate(String dateStr, int days) {
        try {
            // Parse the date string (YYYYMMDD format)
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
    
    private static long calculateDaysBetween(String date1, String date2) {
        try {
            LocalDate d1 = LocalDate.of(
                Integer.parseInt(date1.substring(0, 4)),
                Integer.parseInt(date1.substring(4, 6)),
                Integer.parseInt(date1.substring(6, 8))
            );
            LocalDate d2 = LocalDate.of(
                Integer.parseInt(date2.substring(0, 4)),
                Integer.parseInt(date2.substring(4, 6)),
                Integer.parseInt(date2.substring(6, 8))
            );
            return java.time.temporal.ChronoUnit.DAYS.between(d1, d2);
        } catch (Exception e) {
            return -1;
        }
    }
}