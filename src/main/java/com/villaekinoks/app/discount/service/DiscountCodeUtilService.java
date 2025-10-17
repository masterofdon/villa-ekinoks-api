package com.villaekinoks.app.discount.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.villaekinoks.app.discount.DiscountCode;
import com.villaekinoks.app.discount.DiscountCodeStatus;
import com.villaekinoks.app.discount.DiscountType;
import com.villaekinoks.app.exception.BadApiRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DiscountCodeUtilService {

  private final DiscountCodeService discountCodeService;

  /**
   * Validates and applies a discount code to the total amount
   * 
   * @param discountCode The discount code to validate
   * @param villaId      The villa ID to validate against
   * @param totalAmount  The original total amount
   * @return The discounted total amount
   * @throws BadApiRequestException if the discount code is invalid
   */
  public BigDecimal applyDiscountCode(String discountCode, String villaId, BigDecimal totalAmount) {
    // Find discount code for the specific villa
    DiscountCode code = discountCodeService.getByCodeAndVillaId(discountCode, villaId);

    if (code == null) {
      throw new BadApiRequestException(
          "Invalid discount code: " + discountCode + " for this villa",
          "400#INVALID_DISCOUNT_CODE");
    }

    // Check if discount code is active
    if (code.getStatus() != DiscountCodeStatus.ACTIVE) {
      throw new BadApiRequestException(
          "Discount code " + discountCode + " is not active",
          "400#INACTIVE_DISCOUNT_CODE");
    }

    // Apply discount based on type
    BigDecimal discountedAmount = totalAmount;

    if (code.getDiscounttype() == DiscountType.PERCENTAGE) {
      // Apply percentage discount
      BigDecimal discountPercentage = new BigDecimal(code.getValue());
      BigDecimal discountAmount = totalAmount.multiply(discountPercentage).divide(new BigDecimal("100"));
      discountedAmount = totalAmount.subtract(discountAmount);
    } else if (code.getDiscounttype() == DiscountType.FIXED_AMOUNT) {
      // Apply fixed amount discount
      BigDecimal discountAmount = new BigDecimal(code.getValue());
      discountedAmount = totalAmount.subtract(discountAmount);

      // Ensure the discounted amount is not negative
      if (discountedAmount.compareTo(BigDecimal.ZERO) < 0) {
        discountedAmount = BigDecimal.ZERO;
      }
    }

    return discountedAmount.setScale(2);
  }

}
