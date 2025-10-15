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
}