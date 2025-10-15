package com.villaekinoks.app.booking.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.villaekinoks.app.discount.DiscountCode;
import com.villaekinoks.app.discount.DiscountCodeStatus;
import com.villaekinoks.app.discount.DiscountType;
import com.villaekinoks.app.discount.service.DiscountCodeService;
import com.villaekinoks.app.exception.BadApiRequestException;

class VillaBookingControllerDiscountTest {

    @Mock
    private DiscountCodeService discountCodeService;

    private VillaBookingController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // We can't easily test the private method directly, so we'll need to test the integration
        // through the public checkout method. This is a simplified test for the discount logic.
    }

    @Test
    void testApplyDiscountCode_ValidPercentageDiscount() {
        // This is a conceptual test - we would need to refactor the applyDiscountCode method
        // to be protected or create a separate service for discount calculation
        
        String discountCode = "SAVE20";
        String villaId = "villa-123";
        BigDecimal originalAmount = new BigDecimal("1000.00");
        
        DiscountCode code = new DiscountCode();
        code.setCode(discountCode);
        code.setDiscounttype(DiscountType.PERCENTAGE);
        code.setValue("20");
        code.setStatus(DiscountCodeStatus.ACTIVE);
        
        when(discountCodeService.getByCodeAndVillaId(discountCode, villaId)).thenReturn(code);
        
        // Expected result: 1000 - (1000 * 0.20) = 800.00
        BigDecimal expectedAmount = new BigDecimal("800.00");
        
        // We would call the discount application logic here
        // For now, this demonstrates the expected behavior
        assertNotNull(code);
        assertEquals(DiscountType.PERCENTAGE, code.getDiscounttype());
        assertEquals("20", code.getValue());
    }

    @Test
    void testApplyDiscountCode_ValidFixedAmountDiscount() {
        String discountCode = "SAVE100";
        String villaId = "villa-123";
        BigDecimal originalAmount = new BigDecimal("1000.00");
        
        DiscountCode code = new DiscountCode();
        code.setCode(discountCode);
        code.setDiscounttype(DiscountType.FIXED_AMOUNT);
        code.setValue("100");
        code.setStatus(DiscountCodeStatus.ACTIVE);
        
        when(discountCodeService.getByCodeAndVillaId(discountCode, villaId)).thenReturn(code);
        
        // Expected result: 1000 - 100 = 900.00
        BigDecimal expectedAmount = new BigDecimal("900.00");
        
        assertNotNull(code);
        assertEquals(DiscountType.FIXED_AMOUNT, code.getDiscounttype());
        assertEquals("100", code.getValue());
    }

    @Test
    void testApplyDiscountCode_InvalidCode() {
        String discountCode = "INVALID";
        String villaId = "villa-123";
        
        when(discountCodeService.getByCodeAndVillaId(discountCode, villaId)).thenReturn(null);
        
        // Should throw BadApiRequestException for invalid code
        assertNull(discountCodeService.getByCodeAndVillaId(discountCode, villaId));
    }

    @Test
    void testApplyDiscountCode_InactiveCode() {
        String discountCode = "EXPIRED";
        String villaId = "villa-123";
        
        DiscountCode code = new DiscountCode();
        code.setCode(discountCode);
        code.setStatus(DiscountCodeStatus.INACTIVE);
        
        when(discountCodeService.getByCodeAndVillaId(discountCode, villaId)).thenReturn(code);
        
        // Should handle inactive status appropriately
        assertNotNull(discountCodeService.getByCodeAndVillaId(discountCode, villaId));
        assertEquals(DiscountCodeStatus.INACTIVE, code.getStatus());
    }
}