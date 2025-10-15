# Discount Code Implementation for Villa Booking Checkout

## Overview
This implementation adds discount code validation and application functionality to the villa booking checkout process. When a customer provides a discount code during payment, the system will validate the code against the specific villa and apply the appropriate discount to the total booking amount.

## Implementation Details

### Key Components Added/Modified

1. **VillaBookingController.java**
   - Added `DiscountCodeService` dependency
   - Modified `createPaymentForBooking` method to handle discount codes
   - Added `applyDiscountCode` private method for discount validation and calculation

### Discount Validation Process

When a discount code is provided during checkout, the system:

1. **Validates the discount code** against the specific villa:
   - Checks if the code exists for the given villa using `DiscountCodeService.getByCodeAndVillaId()`
   - Throws `BadApiRequestException` if the code is not found

2. **Validates the discount code status**:
   - Ensures the code status is `ACTIVE`
   - Throws `BadApiRequestException` if the code is `INACTIVE` or `EXPIRED`

3. **Applies the discount** based on the discount type:
   - **PERCENTAGE**: Calculates percentage discount from the total amount
   - **FIXED_AMOUNT**: Subtracts the fixed amount from the total
   - Ensures the final amount doesn't go below zero for fixed amount discounts

### Usage in Checkout Process

The discount is applied in the checkout flow after calculating:
- Base villa pricing for the booking period
- Additional services costs
- Before payment processing

```java
// Apply discount if provided
if (xAction.getDiscountcode() != null && !xAction.getDiscountcode().trim().isEmpty()) {
    totalAmount = applyDiscountCode(xAction.getDiscountcode(), booking.getVilla().getId(), totalAmount);
}
```

### API Integration

The discount code is passed through the existing `Create_BookingPayment_WC_MLS_XAction` class, which already contains the `discountcode` field.

### Error Handling

The implementation includes proper error handling for:
- Invalid discount codes (not found for villa)
- Inactive/expired discount codes
- Ensures discounted amount doesn't go negative

### Example Request

```json
{
    "discountcode": "SAVE20",
    "cardholdername": "John Doe",
    "cardnumber": "5528790000000008",
    "cardexpirymonth": "12",
    "cardexpiryyear": "2025",
    "cardcvc": "123",
    "useraddress": "123 Main St",
    "usercity": "Istanbul",
    "usercountry": "Turkey",
    "userpostcode": "34000"
}
```

## Benefits

1. **Villa-specific validation**: Ensures discount codes are only valid for the intended villa
2. **Flexible discount types**: Supports both percentage and fixed amount discounts
3. **Proper error handling**: Clear error messages for invalid or inactive codes
4. **Secure calculation**: Prevents negative amounts and maintains precision
5. **Seamless integration**: Works with existing payment flow without disruption

## Testing

A test class `VillaBookingControllerDiscountTest.java` has been created to verify:
- Valid percentage discount application
- Valid fixed amount discount application
- Invalid discount code handling
- Inactive discount code handling

## Future Enhancements

Potential improvements could include:
- Usage tracking for single-use discount codes
- Expiration date validation
- Maximum discount amount limits
- Discount code usage analytics
- Multiple discount codes per booking