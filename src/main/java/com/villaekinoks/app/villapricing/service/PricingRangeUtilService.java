package com.villaekinoks.app.villapricing.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.generic.entity.Price;
import com.villaekinoks.app.villapricing.PricingRange;
import com.villaekinoks.app.villapricing.VillaPricingSchema;
import com.villaekinoks.app.villapricing.xaction.UpdatePricingRangeAction;
import com.villaekinoks.app.villapricing.xaction.Update_PricingRange_WC_MLS_XAction;

import lombok.RequiredArgsConstructor;

/**
 * Service for handling complex villa pricing range updates with overlapping period management.
 * 
 * DELETION SCENARIOS:
 * 
 * Scenario 1: Simple Split (inclusive deletion)
 * Existing: [20251001 - 20251031]
 * Delete:   [20251005 - 20251012] (inclusive: deletes 20251005 through 20251012)
 * Result:   [20251001 - 20251004] + [20251013 - 20251031]
 * 
 * Scenario 2: Complex Multi-Range Split (inclusive deletion)
 * Existing: [20251001 - 20251012] + [20251018 - 20251026]
 * Delete:   [20251008 - 20251022] (inclusive: deletes 20251008 through 20251022)
 * Result:   [20251001 - 20251007] + [20251023 - 20251026]
 * 
 * Scenario 3: Deletion with Consolidation
 * Existing: [20251001 - 20251010, 350.00] + [20251011 - 20251020, 400.00] + [20251021 - 20251031, 350.00]
 * Delete:   [20251011 - 20251020] (delete middle range with different price)
 * Result:   [20251001 - 20251031, 350.00] (ranges become adjacent and consolidated)
 * 
 * Scenario 4: Single Date Deletion (inclusive)
 * Existing: [20251001 - 20251031]
 * Delete:   [20251015 - 20251015] (single date deletion)
 * Result:   [20251001 - 20251014] + [20251016 - 20251031]
 * 
 * ADDITION SCENARIOS:
 * 
 * Scenario 1: ADD completely inside existing range (split into 3 ranges)
 * Existing: [20251001 - 20251031, 350.00]
 * Add:      [20251010 - 20251022, 400.00]
 * Result:   [20251001 - 20251009, 350.00] + [20251010 - 20251022, 400.00] + [20251023 - 20251031, 350.00]
 * 
 * Scenario 2: ADD spans multiple ranges (preserves non-overlapping parts)
 * Existing: [20251001 - 20251012, 350.00] + [20251022 - 20251030, 350.00]
 * Add:      [20251010 - 20251024, 400.00]
 * Result:   [20251001 - 20251009, 350.00] + [20251010 - 20251024, 400.00] + [20251025 - 20251030, 350.00]
 * 
 * Scenario 3: No Overlap Addition
 * Existing: [20251001 - 20251012, 350.00]
 * Add:      [20251020 - 20251025, 400.00]
 * Result:   [20251001 - 20251012, 350.00] + [20251020 - 20251025, 400.00]
 * 
 * The service handles all overlapping cases:
 * - DELETE: Complete containment, partial overlaps, middle splits
 * - ADD: Range merging, gap bridging, isolated additions
 */
@Service
@RequiredArgsConstructor
public class PricingRangeUtilService {
  
  private final PricingRangeService pricingRangeService;

  /**
   * Processes price range updates with complex overlapping logic
   * @param villaPricingSchema The villa pricing schema to update
   * @param xAction The update action containing period and price information
   */
  public void processPricingRangeUpdate(VillaPricingSchema villaPricingSchema, Update_PricingRange_WC_MLS_XAction xAction) {
    String startPeriod = xAction.getStartperiod();
    String endPeriod = xAction.getEndperiod();
    UpdatePricingRangeAction action = xAction.getAction();

    // Find all overlapping price ranges
    List<PricingRange> overlappingRanges = findOverlappingRanges(villaPricingSchema, startPeriod, endPeriod);

    if (action == UpdatePricingRangeAction.DELETE) {
      handleDeleteAction(villaPricingSchema, overlappingRanges, startPeriod, endPeriod);
    } else if (action == UpdatePricingRangeAction.ADD) {
      handleAddAction(villaPricingSchema, overlappingRanges, xAction);
    }
  }

  /**
   * Finds all price ranges that overlap with the given period
   */
  private List<PricingRange> findOverlappingRanges(VillaPricingSchema villaPricingSchema, String startPeriod, String endPeriod) {
    return pricingRangeService.findOverlappingRanges(villaPricingSchema.getId(), startPeriod, endPeriod);
  }

  /**
   * Handles DELETE action - removes the specified period from existing ranges
   * 
   * Special handling for single date deletion (deleteStartPeriod == deleteEndPeriod):
   * - At range start: Case 2 (creates one remaining range)
   * - At range end: Case 3 (creates one remaining range)  
   * - In middle: Case 4 (creates two remaining ranges)
   * - Entire range: Case 1 (deletes entire range)
   */
  private void handleDeleteAction(VillaPricingSchema villaPricingSchema, List<PricingRange> overlappingRanges, String deleteStartPeriod, String deleteEndPeriod) {
    List<PricingRange> rangesToDelete = new ArrayList<>();
    List<PricingRange> rangesToAdd = new ArrayList<>();

    for (PricingRange existingRange : overlappingRanges) {
      String existingStart = existingRange.getStartperiod();
      String existingEnd = existingRange.getEndperiod();
      
      rangesToDelete.add(existingRange);

      // Case 1: Delete period completely contains the existing range
      if (comparePeriods(deleteStartPeriod, existingStart) <= 0 && 
          comparePeriods(deleteEndPeriod, existingEnd) >= 0) {
        // Existing range is completely deleted - no new ranges to create
        continue;
      }

      // Case 2: Delete period is at the beginning of existing range
      // For single date: deleteStartPeriod == deleteEndPeriod == existingStart
      if (comparePeriods(deleteStartPeriod, existingStart) <= 0 && 
          comparePeriods(deleteEndPeriod, existingEnd) < 0 &&
          comparePeriods(deleteEndPeriod, existingStart) >= 0) {
        // Create new range from day after deleteEndPeriod to existingEnd (inclusive deletion)
        String newStart = addDaysToDate(deleteEndPeriod, 1);
        PricingRange newRange = createNewPricingRange(existingRange, newStart, existingEnd);
        rangesToAdd.add(newRange);
      }

      // Case 3: Delete period is at the end of existing range  
      // For single date: deleteStartPeriod == deleteEndPeriod == existingEnd
      else if (comparePeriods(deleteStartPeriod, existingStart) > 0 && 
               comparePeriods(deleteStartPeriod, existingEnd) <= 0 &&
               comparePeriods(deleteEndPeriod, existingEnd) >= 0) {
        // Create new range from existingStart to day before deleteStartPeriod (inclusive deletion)
        String newEnd = addDaysToDate(deleteStartPeriod, -1);
        PricingRange newRange = createNewPricingRange(existingRange, existingStart, newEnd);
        rangesToAdd.add(newRange);
      }

      // Case 4: Delete period is in the middle of existing range
      // For single date: existingStart < deleteStartPeriod == deleteEndPeriod < existingEnd
      else if (comparePeriods(deleteStartPeriod, existingStart) > 0 && 
               comparePeriods(deleteEndPeriod, existingEnd) < 0) {
        // Create two new ranges: before and after the deleted period (inclusive deletion)
        String beforeEnd = addDaysToDate(deleteStartPeriod, -1);
        String afterStart = addDaysToDate(deleteEndPeriod, 1);
        PricingRange rangeBefore = createNewPricingRange(existingRange, existingStart, beforeEnd);
        PricingRange rangeAfter = createNewPricingRange(existingRange, afterStart, existingEnd);
        rangesToAdd.add(rangeBefore);
        rangesToAdd.add(rangeAfter);
      }
    }

    // Execute deletions and additions
    rangesToDelete.forEach(pricingRangeService::delete);
    rangesToAdd.forEach(pricingRangeService::save);
    
    // Force flush to ensure database changes are committed before consolidation
    // This ensures consolidateAdjacentRanges sees the updated ranges
    
    // Consolidate adjacent ranges with same price after deletion
    // Note: This is critical for delete operations as they may create situations
    // where previously separated ranges with same price become adjacent
    consolidateAdjacentRanges(villaPricingSchema);
  }

  /**
   * Handles ADD action - adds new price range and properly handles overlapping ones
   * 
   * ADD scenarios:
   * 
   * Scenario 1: ADD inside existing range (split into 3)
   * Existing: [20251001-20251031, 350.00]
   * ADD:      [20251010-20251022, 400.00]
   * Result:   [20251001-20251009, 350.00] + [20251010-20251022, 400.00] + [20251023-20251031, 350.00]
   * 
   * Scenario 2: ADD bridges multiple ranges (merge)
   * Existing: [20251001-20251012, 350.00] + [20251022-20251030, 350.00]
   * ADD:      [20251010-20251024, 400.00]
   * Result:   [20251001-20251009, 350.00] + [20251010-20251024, 400.00] + [20251025-20251030, 350.00]
   */
  private void handleAddAction(VillaPricingSchema villaPricingSchema, List<PricingRange> overlappingRanges, Update_PricingRange_WC_MLS_XAction xAction) {
    String addStartPeriod = xAction.getStartperiod();
    String addEndPeriod = xAction.getEndperiod();

    if (overlappingRanges.isEmpty()) {
      // No overlaps - just add the new range
      PricingRange newRange = new PricingRange();
      newRange.setStartperiod(addStartPeriod);
      newRange.setEndperiod(addEndPeriod);
      newRange.setPricepernight(xAction.getPricepernight());
      newRange.setVillapricingschema(villaPricingSchema);
      pricingRangeService.save(newRange);
      return;
    }

    List<PricingRange> rangesToDelete = new ArrayList<>();
    List<PricingRange> rangesToAdd = new ArrayList<>();

    // Process each overlapping range
    for (PricingRange existingRange : overlappingRanges) {
      String existingStart = existingRange.getStartperiod();
      String existingEnd = existingRange.getEndperiod();
      
      rangesToDelete.add(existingRange);

      // Case 1: ADD period completely contains the existing range
      if (comparePeriods(addStartPeriod, existingStart) <= 0 && 
          comparePeriods(addEndPeriod, existingEnd) >= 0) {
        // Existing range is completely overwritten - no need to preserve parts
        continue;
      }

      // Case 2: ADD period is at the beginning of existing range
      else if (comparePeriods(addStartPeriod, existingStart) <= 0 && 
               comparePeriods(addEndPeriod, existingEnd) < 0 &&
               comparePeriods(addEndPeriod, existingStart) > 0) {
        // Preserve the part after ADD period (inclusive dates require +1 day)
        String preservedStart = addDaysToDate(addEndPeriod, 1);
        PricingRange preservedRange = createNewPricingRange(existingRange, preservedStart, existingEnd);
        rangesToAdd.add(preservedRange);
      }

      // Case 3: ADD period is at the end of existing range
      else if (comparePeriods(addStartPeriod, existingStart) > 0 && 
               comparePeriods(addStartPeriod, existingEnd) < 0 &&
               comparePeriods(addEndPeriod, existingEnd) >= 0) {
        // Preserve the part before ADD period (inclusive dates require -1 day)
        String preservedEnd = addDaysToDate(addStartPeriod, -1);
        PricingRange preservedRange = createNewPricingRange(existingRange, existingStart, preservedEnd);
        rangesToAdd.add(preservedRange);
      }

      // Case 4: ADD period is completely inside existing range (split into 2 parts)
      else if (comparePeriods(addStartPeriod, existingStart) > 0 && 
               comparePeriods(addEndPeriod, existingEnd) < 0) {
        // Preserve parts before and after ADD period (inclusive dates require adjustment)
        String beforeEnd = addDaysToDate(addStartPeriod, -1);
        String afterStart = addDaysToDate(addEndPeriod, 1);
        
        PricingRange rangeBefore = createNewPricingRange(existingRange, existingStart, beforeEnd);
        PricingRange rangeAfter = createNewPricingRange(existingRange, afterStart, existingEnd);
        rangesToAdd.add(rangeBefore);
        rangesToAdd.add(rangeAfter);
      }
    }

    // Execute deletions and additions of preserved parts
    rangesToDelete.forEach(pricingRangeService::delete);
    rangesToAdd.forEach(pricingRangeService::save);

    // Finally, add the new range with the new price
    PricingRange newRange = new PricingRange();
    newRange.setStartperiod(addStartPeriod);
    newRange.setEndperiod(addEndPeriod);
    newRange.setPricepernight(xAction.getPricepernight());
    newRange.setVillapricingschema(villaPricingSchema);
    
    pricingRangeService.save(newRange);
    
    // Consolidate adjacent ranges with same price after addition
    consolidateAdjacentRanges(villaPricingSchema);
  }

  /**
   * Creates a new pricing range based on an existing one with new start/end periods
   */
  private PricingRange createNewPricingRange(PricingRange originalRange, String newStartPeriod, String newEndPeriod) {
    PricingRange newRange = new PricingRange();
    newRange.setStartperiod(newStartPeriod);
    newRange.setEndperiod(newEndPeriod);
    newRange.setPricepernight(originalRange.getPricepernight());
    newRange.setVillapricingschema(originalRange.getVillapricingschema());
    return newRange;
  }

  /**
   * Compare two period strings (format: YYYYMMDD)
   * Returns: < 0 if period1 < period2, 0 if equal, > 0 if period1 > period2
   */
  private int comparePeriods(String period1, String period2) {
    return period1.compareTo(period2);
  }

  /**
   * Adds or subtracts days from a date string in YYYYMMDD format
   * @param dateStr The date string in YYYYMMDD format
   * @param days Number of days to add (positive) or subtract (negative)
   * @return The resulting date string in YYYYMMDD format
   */
  private String addDaysToDate(String dateStr, int days) {
    try {
      // Parse the date string
      int year = Integer.parseInt(dateStr.substring(0, 4));
      int month = Integer.parseInt(dateStr.substring(4, 6));
      int day = Integer.parseInt(dateStr.substring(6, 8));
      
      // Create LocalDate and add/subtract days
      java.time.LocalDate date = java.time.LocalDate.of(year, month, day);
      java.time.LocalDate newDate = date.plusDays(days);
      
      // Format back to YYYYMMDD
      return String.format("%04d%02d%02d", 
          newDate.getYear(), 
          newDate.getMonthValue(), 
          newDate.getDayOfMonth());
    } catch (Exception e) {
      throw new IllegalArgumentException("Invalid date format: " + dateStr, e);
    }
  }

  /**
   * Consolidates adjacent pricing ranges with the same price into single ranges.
   * This method fetches all pricing ranges for the villa, sorts them by start period,
   * and merges consecutive ranges that have the same price.
   * 
   * This is especially important after DELETE operations as they may create situations
   * where previously separated ranges with same price become adjacent.
   * 
   * @param villaPricingSchema The villa pricing schema to consolidate ranges for
   */
  private void consolidateAdjacentRanges(VillaPricingSchema villaPricingSchema) {
    // Get all pricing ranges for this villa, sorted by start period
    List<PricingRange> allRanges = pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(villaPricingSchema.getId());
    
    if (allRanges.size() <= 1) {
      return; // Nothing to consolidate
    }

    List<PricingRange> rangesToDelete = new ArrayList<>();
    List<PricingRange> rangesToAdd = new ArrayList<>();
    
    int i = 0;
    while (i < allRanges.size()) {
      PricingRange currentRange = allRanges.get(i);
      String consolidatedStart = currentRange.getStartperiod();
      String consolidatedEnd = currentRange.getEndperiod();
      
      // Look for consecutive ranges with the same price
      int j = i + 1;
      while (j < allRanges.size()) {
        PricingRange nextRange = allRanges.get(j);
        
        // Check if ranges are adjacent and have the same price
        String expectedNextStart = addDaysToDate(consolidatedEnd, 1);
        boolean areAdjacent = expectedNextStart.equals(nextRange.getStartperiod());
        boolean haveSamePrice = arePricesEqual(currentRange.getPricepernight(), nextRange.getPricepernight());
        
        if (areAdjacent && haveSamePrice) {
          // Extend the consolidated range
          consolidatedEnd = nextRange.getEndperiod();
          j++;
        } else {
          break; // No more adjacent ranges with same price
        }
      }
      
      // If we consolidated multiple ranges (j > i + 1), replace them with a single range
      if (j > i + 1) {
        // Mark original ranges for deletion
        for (int k = i; k < j; k++) {
          rangesToDelete.add(allRanges.get(k));
        }
        
        // Create new consolidated range
        PricingRange consolidatedRange = createNewPricingRange(currentRange, consolidatedStart, consolidatedEnd);
        rangesToAdd.add(consolidatedRange);
      }
      
      i = j; // Move to the next unprocessed range
    }
    
    // Execute consolidation if needed
    if (!rangesToDelete.isEmpty()) {
      rangesToDelete.forEach(pricingRangeService::delete);
      rangesToAdd.forEach(pricingRangeService::save);
    }
  }

  /**
   * Checks if two Price objects are equal (same amount and currency)
   */
  private boolean arePricesEqual(Price price1, Price price2) {
    if (price1 == null || price2 == null) {
      return price1 == price2;
    }
    return price1.getAmount().equals(price2.getAmount()) && 
           price1.getCurrency() == price2.getCurrency();
  }
}
