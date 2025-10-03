package com.villaekinoks.app.villapricing.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.villaekinoks.app.villapricing.PricingRange;
import com.villaekinoks.app.villapricing.VillaPricingSchema;
import com.villaekinoks.app.villapricing.xaction.Update_PricingRange_WC_MLS_XAction;
import com.villaekinoks.app.villapricing.xaction.UpdatePricingRangeAction;

import lombok.RequiredArgsConstructor;

/**
 * Service for handling complex villa pricing range updates with overlapping period management.
 * 
 * DELETION SCENARIOS:
 * 
 * Scenario 1: Simple Split
 * Existing: [20251001 - 20251031]
 * Delete:   [20251005 - 20251012]
 * Result:   [20251001 - 20251005] + [20251012 - 20251031]
 * 
 * Scenario 2: Complex Multi-Range Split
 * Existing: [20251001 - 20251012] + [20251018 - 20251026]
 * Delete:   [20251008 - 20251022]
 * Result:   [20251001 - 20251008] + [20251022 - 20251026]
 * 
 * ADDITION SCENARIOS:
 * 
 * Scenario 1: ADD completely inside existing range (split into 3 ranges)
 * Existing: [20251001 - 20251031, 350.00]
 * Add:      [20251010 - 20251022, 400.00]
 * Result:   [20251001 - 20251010, 350.00] + [20251010 - 20251022, 400.00] + [20251022 - 20251031, 350.00]
 * 
 * Scenario 2: ADD spans multiple ranges (preserves non-overlapping parts)
 * Existing: [20251001 - 20251012, 350.00] + [20251022 - 20251030, 350.00]
 * Add:      [20251010 - 20251024, 400.00]
 * Result:   [20251001 - 20251010, 350.00] + [20251010 - 20251024, 400.00] + [20251024 - 20251030, 350.00]
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
      handleDeleteAction(overlappingRanges, startPeriod, endPeriod);
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
   */
  private void handleDeleteAction(List<PricingRange> overlappingRanges, String deleteStartPeriod, String deleteEndPeriod) {
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
      if (comparePeriods(deleteStartPeriod, existingStart) <= 0 && 
          comparePeriods(deleteEndPeriod, existingEnd) < 0 &&
          comparePeriods(deleteEndPeriod, existingStart) > 0) {
        // Create new range from deleteEndPeriod to existingEnd
        PricingRange newRange = createNewPricingRange(existingRange, deleteEndPeriod, existingEnd);
        rangesToAdd.add(newRange);
      }

      // Case 3: Delete period is at the end of existing range
      else if (comparePeriods(deleteStartPeriod, existingStart) > 0 && 
               comparePeriods(deleteStartPeriod, existingEnd) < 0 &&
               comparePeriods(deleteEndPeriod, existingEnd) >= 0) {
        // Create new range from existingStart to deleteStartPeriod
        PricingRange newRange = createNewPricingRange(existingRange, existingStart, deleteStartPeriod);
        rangesToAdd.add(newRange);
      }

      // Case 4: Delete period is in the middle of existing range
      else if (comparePeriods(deleteStartPeriod, existingStart) > 0 && 
               comparePeriods(deleteEndPeriod, existingEnd) < 0) {
        // Create two new ranges: before and after the deleted period
        PricingRange rangeBefore = createNewPricingRange(existingRange, existingStart, deleteStartPeriod);
        PricingRange rangeAfter = createNewPricingRange(existingRange, deleteEndPeriod, existingEnd);
        rangesToAdd.add(rangeBefore);
        rangesToAdd.add(rangeAfter);
      }
    }

    // Execute deletions and additions
    rangesToDelete.forEach(pricingRangeService::delete);
    rangesToAdd.forEach(pricingRangeService::save);
  }

  /**
   * Handles ADD action - adds new price range and properly handles overlapping ones
   * 
   * ADD scenarios:
   * 
   * Scenario 1: ADD inside existing range (split into 3)
   * Existing: [20251001-20251031, 350.00]
   * ADD:      [20251010-20251022, 400.00]
   * Result:   [20251001-20251010, 350.00] + [20251010-20251022, 400.00] + [20251022-20251031, 350.00]
   * 
   * Scenario 2: ADD bridges multiple ranges (merge)
   * Existing: [20251001-20251012, 350.00] + [20251022-20251030, 350.00]
   * ADD:      [20251010-20251024, 400.00]
   * Result:   [20251001-20251010, 350.00] + [20251010-20251024, 400.00] + [20251024-20251030, 350.00]
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
        // Preserve the part after ADD period
        PricingRange preservedRange = createNewPricingRange(existingRange, addEndPeriod, existingEnd);
        rangesToAdd.add(preservedRange);
      }

      // Case 3: ADD period is at the end of existing range
      else if (comparePeriods(addStartPeriod, existingStart) > 0 && 
               comparePeriods(addStartPeriod, existingEnd) < 0 &&
               comparePeriods(addEndPeriod, existingEnd) >= 0) {
        // Preserve the part before ADD period
        PricingRange preservedRange = createNewPricingRange(existingRange, existingStart, addStartPeriod);
        rangesToAdd.add(preservedRange);
      }

      // Case 4: ADD period is completely inside existing range (split into 2 parts)
      else if (comparePeriods(addStartPeriod, existingStart) > 0 && 
               comparePeriods(addEndPeriod, existingEnd) < 0) {
        // Preserve parts before and after ADD period
        PricingRange rangeBefore = createNewPricingRange(existingRange, existingStart, addStartPeriod);
        PricingRange rangeAfter = createNewPricingRange(existingRange, addEndPeriod, existingEnd);
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
}
