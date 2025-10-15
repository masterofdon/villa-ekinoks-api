package com.villaekinoks.app.villapricing.service;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.villaekinoks.app.generic.entity.Price;
import com.villaekinoks.app.villapricing.PricingRange;
import com.villaekinoks.app.villapricing.VillaPricingSchema;
import com.villaekinoks.app.villapricing.xaction.UpdatePricingRangeAction;
import com.villaekinoks.app.villapricing.xaction.Update_PricingRange_WC_MLS_XAction;

@ExtendWith(MockitoExtension.class)
class PricingRangeUtilServiceTest {

    @Mock
    private PricingRangeService pricingRangeService;

    @InjectMocks
    private PricingRangeUtilService pricingRangeUtilService;

    private VillaPricingSchema villaPricingSchema;

    @BeforeEach
    void setUp() {
        villaPricingSchema = new VillaPricingSchema();
        villaPricingSchema.setId("test-villa-pricing-schema-id");
    }

    @Test
    void testDeleteAction_InclusiveDeletion_SimpleSplit() {
        // Arrange: Existing range [20251001 - 20251031]
        PricingRange existingRange = createPricingRange("20251001", "20251031", "350.00");
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(existingRange));
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList()); // Empty for consolidation

        Update_PricingRange_WC_MLS_XAction deleteAction = createDeleteAction("20251005", "20251012");

        // Act: Delete [20251005 - 20251012] (inclusive)
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, deleteAction);

        // Assert: Should create two ranges [20251001 - 20251004] and [20251013 - 20251031]
        verify(pricingRangeService).delete(existingRange);
        verify(pricingRangeService, times(2)).save(any(PricingRange.class));
        
        // Verify the saved ranges have correct boundaries
        verify(pricingRangeService).save(argThat(range -> 
            "20251001".equals(range.getStartperiod()) && "20251004".equals(range.getEndperiod())));
        verify(pricingRangeService).save(argThat(range -> 
            "20251013".equals(range.getStartperiod()) && "20251031".equals(range.getEndperiod())));
    }

    @Test
    void testDeleteAction_InclusiveDeletion_BeginningOfRange() {
        // Arrange: Existing range [20251001 - 20251031]
        PricingRange existingRange = createPricingRange("20251001", "20251031", "350.00");
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(existingRange));
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList()); // Empty for consolidation

        Update_PricingRange_WC_MLS_XAction deleteAction = createDeleteAction("20250925", "20251010");

        // Act: Delete from before start to middle (inclusive)
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, deleteAction);

        // Assert: Should create one range [20251011 - 20251031]
        verify(pricingRangeService).delete(existingRange);
        verify(pricingRangeService, times(1)).save(any(PricingRange.class));
        
        // Verify the saved range has correct start boundary
        verify(pricingRangeService).save(argThat(range -> 
            "20251011".equals(range.getStartperiod()) && "20251031".equals(range.getEndperiod())));
    }

    @Test
    void testDeleteAction_InclusiveDeletion_EndOfRange() {
        // Arrange: Existing range [20251001 - 20251031]
        PricingRange existingRange = createPricingRange("20251001", "20251031", "350.00");
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(existingRange));
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList()); // Empty for consolidation

        Update_PricingRange_WC_MLS_XAction deleteAction = createDeleteAction("20251015", "20251105");

        // Act: Delete from middle to after end (inclusive)
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, deleteAction);

        // Assert: Should create one range [20251001 - 20251014]
        verify(pricingRangeService).delete(existingRange);
        verify(pricingRangeService, times(1)).save(any(PricingRange.class));
        
        // Verify the saved range has correct end boundary
        verify(pricingRangeService).save(argThat(range -> 
            "20251001".equals(range.getStartperiod()) && "20251014".equals(range.getEndperiod())));
    }

    @Test
    void testDeleteAction_InclusiveDeletion_CompleteContainment() {
        // Arrange: Existing range [20251001 - 20251031]
        PricingRange existingRange = createPricingRange("20251001", "20251031", "350.00");
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(existingRange));
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList()); // Empty for consolidation

        Update_PricingRange_WC_MLS_XAction deleteAction = createDeleteAction("20250925", "20251105");

        // Act: Delete period completely contains the existing range
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, deleteAction);

        // Assert: Should delete the range completely, no new ranges created
        verify(pricingRangeService).delete(existingRange);
        verify(pricingRangeService, never()).save(any(PricingRange.class));
    }

    @Test
    void testDeleteAction_WithConsolidation_AdjacentRangesWithSamePrice() {
        // Arrange: Three ranges where middle deletion should result in consolidation
        // [20251001 - 20251010, 350.00] + [20251011 - 20251020, 400.00] + [20251021 - 20251031, 350.00]
        // Delete: [20251011 - 20251020] (the middle range with different price)
        // Expected: [20251001 - 20251031, 350.00] (consolidated)
        
        PricingRange range1 = createPricingRange("20251001", "20251010", "350.00");
        PricingRange range2 = createPricingRange("20251011", "20251020", "400.00");
        PricingRange range3 = createPricingRange("20251021", "20251031", "350.00");
        
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(range2)); // Only the middle range overlaps with delete period
        
        // Mock the consolidation call - return the first and third ranges for consolidation
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList(range1, range3));

        Update_PricingRange_WC_MLS_XAction deleteAction = createDeleteAction("20251011", "20251020");

        // Act: Delete the middle range
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, deleteAction);

        // Assert: Should delete the middle range and consolidate the remaining adjacent ranges
        verify(pricingRangeService).delete(range2); // Delete the overlapping range
        
        // Should call consolidation which will merge range1 and range3
        verify(pricingRangeService).findAllByVillaPricingSchemaOrderByStartperiod(anyString());
        
        // Should delete both individual ranges and save one consolidated range
        verify(pricingRangeService, times(3)).delete(any(PricingRange.class)); // 1 for delete + 2 for consolidation
        verify(pricingRangeService, times(1)).save(any(PricingRange.class)); // Save consolidated range
        
        // Verify the consolidated range spans from first start to last end
        verify(pricingRangeService).save(argThat(range -> 
            "20251001".equals(range.getStartperiod()) && "20251031".equals(range.getEndperiod())));
    }

    @Test
    void testDeleteAction_WithConsolidation_PartialDeletionCreatesAdjacency() {
        // Arrange: Two ranges with a gap, delete part of first range to create adjacency
        // [20251001 - 20251020, 350.00] + [20251025 - 20251031, 350.00]
        // Delete: [20251015 - 20251024] (spans gap and creates adjacency)
        // Expected: [20251001 - 20251014, 350.00] + [20251025 - 20251031, 350.00] initially
        // After consolidation: Should remain separate as they're not adjacent (gap exists)
        
        PricingRange range1 = createPricingRange("20251001", "20251020", "350.00");
        PricingRange range2 = createPricingRange("20251025", "20251031", "350.00");
        
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(range1)); // Only first range overlaps
        
        // Mock consolidation - return the modified ranges
        PricingRange modifiedRange1 = createPricingRange("20251001", "20251014", "350.00");
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList(modifiedRange1, range2));

        Update_PricingRange_WC_MLS_XAction deleteAction = createDeleteAction("20251015", "20251024");

        // Act: Delete overlapping portion
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, deleteAction);

        // Assert: Should modify first range and leave second range unchanged
        verify(pricingRangeService).delete(range1); // Delete original first range
        verify(pricingRangeService, times(1)).save(any(PricingRange.class)); // Save modified first range
        
        // Consolidation should be called but no changes should be made (ranges not adjacent)
        verify(pricingRangeService).findAllByVillaPricingSchemaOrderByStartperiod(anyString());
    }

    @Test
    void testDeleteAction_NoConsolidation_WhenRangesRemainSeparate() {
        // Arrange: Two separate ranges with same price but gap between them
        // [20251001 - 20251010, 350.00] + [20251020 - 20251031, 350.00]
        // Delete: [20251005 - 20251007] (delete part of first range)
        // Expected: [20251001 - 20251004, 350.00] + [20251008 - 20251010, 350.00] + [20251020 - 20251031, 350.00]
        // No consolidation should happen as ranges are not adjacent
        
        PricingRange range1 = createPricingRange("20251001", "20251010", "350.00");
        PricingRange range2 = createPricingRange("20251020", "20251031", "350.00");
        
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(range1)); // Only first range overlaps
        
        // Mock consolidation - return all ranges after split (none are adjacent)
        PricingRange splitRange1 = createPricingRange("20251001", "20251004", "350.00");
        PricingRange splitRange2 = createPricingRange("20251008", "20251010", "350.00");
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList(splitRange1, splitRange2, range2));

        Update_PricingRange_WC_MLS_XAction deleteAction = createDeleteAction("20251005", "20251007");

        // Act: Delete middle portion of first range
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, deleteAction);

        // Assert: Should split first range into two parts, no consolidation
        verify(pricingRangeService).delete(range1); // Delete original first range
        verify(pricingRangeService, times(2)).save(any(PricingRange.class)); // Save two split ranges
        
        // Consolidation should be called but no changes made (no adjacent ranges with same price)
        verify(pricingRangeService).findAllByVillaPricingSchemaOrderByStartperiod(anyString());
    }

    @Test
    void testDeleteAction_SingleDate_InclusiveDeletion() {
        // Arrange: Single range [20251001 - 20251031]
        // Delete: Single date [20251015 - 20251015] (same start and end date)
        // Expected: [20251001 - 20251014] + [20251016 - 20251031]
        
        PricingRange existingRange = createPricingRange("20251001", "20251031", "350.00");
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(existingRange));
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList()); // Empty for consolidation

        Update_PricingRange_WC_MLS_XAction deleteAction = createDeleteAction("20251015", "20251015");

        // Act: Delete single date (inclusive)
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, deleteAction);

        // Assert: Should split into two ranges around the deleted date
        verify(pricingRangeService).delete(existingRange);
        verify(pricingRangeService, times(2)).save(any(PricingRange.class));
        
        // Verify the split ranges exclude the deleted date
        verify(pricingRangeService).save(argThat(range -> 
            "20251001".equals(range.getStartperiod()) && "20251014".equals(range.getEndperiod())));
        verify(pricingRangeService).save(argThat(range -> 
            "20251016".equals(range.getStartperiod()) && "20251031".equals(range.getEndperiod())));
    }

    @Test
    void testDeleteAction_SingleDate_AtRangeStart() {
        // Arrange: Single range [20251001 - 20251031]
        // Delete: First date [20251001 - 20251001]
        // Expected: [20251002 - 20251031]
        
        PricingRange existingRange = createPricingRange("20251001", "20251031", "350.00");
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(existingRange));
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList()); // Empty for consolidation

        Update_PricingRange_WC_MLS_XAction deleteAction = createDeleteAction("20251001", "20251001");

        // Act: Delete first date
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, deleteAction);

        // Assert: Should create one range starting from the next day
        verify(pricingRangeService).delete(existingRange);
        verify(pricingRangeService, times(1)).save(any(PricingRange.class));
        
        // Verify the remaining range starts from the day after deleted date
        verify(pricingRangeService).save(argThat(range -> 
            "20251002".equals(range.getStartperiod()) && "20251031".equals(range.getEndperiod())));
    }

    @Test
    void testDeleteAction_SingleDate_AtRangeEnd() {
        // Arrange: Single range [20251001 - 20251031]
        // Delete: Last date [20251031 - 20251031]
        // Expected: [20251001 - 20251030]
        
        PricingRange existingRange = createPricingRange("20251001", "20251031", "350.00");
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(existingRange));
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList()); // Empty for consolidation

        Update_PricingRange_WC_MLS_XAction deleteAction = createDeleteAction("20251031", "20251031");

        // Act: Delete last date
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, deleteAction);

        // Assert: Should create one range ending at the day before deleted date
        verify(pricingRangeService).delete(existingRange);
        verify(pricingRangeService, times(1)).save(any(PricingRange.class));
        
        // Verify the remaining range ends at the day before deleted date
        verify(pricingRangeService).save(argThat(range -> 
            "20251001".equals(range.getStartperiod()) && "20251030".equals(range.getEndperiod())));
    }

    @Test
    void testDeleteAction_SingleDate_EntireRange() {
        // Arrange: Single day range [20251015 - 20251015]
        // Delete: Same single date [20251015 - 20251015]
        // Expected: Range should be completely deleted
        
        PricingRange existingRange = createPricingRange("20251015", "20251015", "350.00");
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList(existingRange));
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList()); // Empty for consolidation

        Update_PricingRange_WC_MLS_XAction deleteAction = createDeleteAction("20251015", "20251015");

        // Act: Delete the entire single-day range
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, deleteAction);

        // Assert: Should delete the range completely, no new ranges created
        verify(pricingRangeService).delete(existingRange);
        verify(pricingRangeService, never()).save(any(PricingRange.class));
    }

    @Test
    void testAddAction_ConsecutiveSingleDates_ShouldConsolidate() {
        // Arrange: Test the specific issue - adding consecutive single dates
        // First add single date [20260731 - 20260731]
        // Then add single date [20260801 - 20260801]
        // Expected: Should consolidate into [20260731 - 20260801]
        
        // First call: Add 20260731 (no overlaps)
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList()); // No overlaps for first add
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList()); // Empty initially

        Update_PricingRange_WC_MLS_XAction addAction1 = createAddAction("20260731", "20260731", "350.00");

        // Act: Add first date
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, addAction1);

        // Verify first range was saved and consolidation was called
        verify(pricingRangeService, times(1)).save(any(PricingRange.class));
        verify(pricingRangeService, times(1)).findAllByVillaPricingSchemaOrderByStartperiod(anyString());

        // Reset mocks for second add operation
        reset(pricingRangeService);
        
        // Second call: Add 20260801 (no overlaps, but adjacent to existing)
        PricingRange existingRange = createPricingRange("20260731", "20260731", "350.00");
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList()); // No overlaps for second add
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList(existingRange)); // Return first range for consolidation

        Update_PricingRange_WC_MLS_XAction addAction2 = createAddAction("20260801", "20260801", "350.00");

        // Act: Add second date (should trigger consolidation)
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, addAction2);

        // Assert: Should save the new range and call consolidation
        verify(pricingRangeService, times(1)).save(any(PricingRange.class)); // Save the new individual range
        verify(pricingRangeService, times(1)).findAllByVillaPricingSchemaOrderByStartperiod(anyString()); // Consolidation call
    }

    @Test
    void testAddAction_NonAdjacentSingleDates_ShouldNotConsolidate() {
        // Arrange: Test non-adjacent dates that should remain separate
        // Add single date [20260731 - 20260731]
        // Then add single date [20260802 - 20260802] (gap in between)
        // Expected: Should remain as two separate ranges
        
        // First call: Add 20260731 (no overlaps)
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList()); // No overlaps for first add
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList()); // Empty initially

        Update_PricingRange_WC_MLS_XAction addAction1 = createAddAction("20260731", "20260731", "350.00");

        // Act: Add first date
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, addAction1);

        // Reset mocks for second add operation
        reset(pricingRangeService);
        
        // Second call: Add 20260802 (no overlaps, NOT adjacent to existing)
        PricingRange existingRange = createPricingRange("20260731", "20260731", "350.00");
        when(pricingRangeService.findOverlappingRanges(anyString(), anyString(), anyString()))
            .thenReturn(Arrays.asList()); // No overlaps for second add
        when(pricingRangeService.findAllByVillaPricingSchemaOrderByStartperiod(anyString()))
            .thenReturn(Arrays.asList(existingRange)); // Return first range for consolidation

        Update_PricingRange_WC_MLS_XAction addAction2 = createAddAction("20260802", "20260802", "350.00");

        // Act: Add second date (consolidation should be called but no merging should happen)
        pricingRangeUtilService.processPricingRangeUpdate(villaPricingSchema, addAction2);

        // Assert: Should save the new range and call consolidation (but no actual consolidation due to gap)
        verify(pricingRangeService, times(1)).save(any(PricingRange.class)); // Save the new individual range
        verify(pricingRangeService, times(1)).findAllByVillaPricingSchemaOrderByStartperiod(anyString()); // Consolidation call
    }

    private PricingRange createPricingRange(String startPeriod, String endPeriod, String amount) {
        PricingRange range = new PricingRange();
        range.setStartperiod(startPeriod);
        range.setEndperiod(endPeriod);
        
        Price priceObj = new Price();
        priceObj.setAmount(amount);
        range.setPricepernight(priceObj);
        range.setVillapricingschema(villaPricingSchema);
        
        return range;
    }

    private Update_PricingRange_WC_MLS_XAction createDeleteAction(String startPeriod, String endPeriod) {
        Update_PricingRange_WC_MLS_XAction action = new Update_PricingRange_WC_MLS_XAction();
        action.setStartperiod(startPeriod);
        action.setEndperiod(endPeriod);
        action.setAction(UpdatePricingRangeAction.DELETE);
        return action;
    }

    private Update_PricingRange_WC_MLS_XAction createAddAction(String startPeriod, String endPeriod, String amount) {
        Update_PricingRange_WC_MLS_XAction action = new Update_PricingRange_WC_MLS_XAction();
        action.setStartperiod(startPeriod);
        action.setEndperiod(endPeriod);
        action.setAction(UpdatePricingRangeAction.ADD);
        
        Price priceObj = new Price();
        priceObj.setAmount(amount);
        action.setPricepernight(priceObj);
        
        return action;
    }
}