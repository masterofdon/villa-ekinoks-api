package com.villaekinoks.app.payment.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.iyzipay.Options;
import com.iyzipay.model.Address;
import com.iyzipay.model.BasketItem;
import com.iyzipay.model.BasketItemType;
import com.iyzipay.model.Buyer;
import com.iyzipay.model.Locale;
import com.iyzipay.model.Payment;
import com.iyzipay.model.PaymentCard;
import com.iyzipay.model.PaymentChannel;
import com.iyzipay.model.PaymentGroup;
import com.iyzipay.request.CreatePaymentRequest;
import com.villaekinoks.app.utils.RandomizerUtils;

@Service
public class IyzicoPaymentProcessingService {

  private Options paymentProcessingOptions;

  public IyzicoPaymentProcessingService() {
    this.paymentProcessingOptions = new Options();
    this.paymentProcessingOptions.setApiKey("sandbox-fcEn4dBWh3yFlQP7LUODjRaRO4qaV6Ia");
    this.paymentProcessingOptions.setSecretKey("sandbox-Dryz4Dg9CerYqdNWC3cDACEh3CkGUiHJ");
    this.paymentProcessingOptions.setBaseUrl("https://sandbox-api.iyzipay.com");
  }

  public Payment processPayment(
      String userid,
      String conversationId,
      String cardholdername,
      String firstname,
      String lastname,
      String phonenumber,
      String email,
      String idnumber,
      String userip,
      String useraddress,
      String usercity,
      String usercountry,
      String userzipcode,
      String price,
      String currency,
      String cardnumber,
      String expiremonth,
      String expireyear,
      String cvc,
      Integer installmentcount,
      Integer securepayment) {

    CreatePaymentRequest request = new CreatePaymentRequest();
    request.setLocale(Locale.TR.getValue());
    request.setConversationId(conversationId);
    request.setPrice(new BigDecimal(price));
    request.setPaidPrice(new BigDecimal(price));
    request.setCurrency(currency);
    request.setInstallment(1);
    request.setBasketId(conversationId);
    request.setPaymentChannel(PaymentChannel.WEB.name());
    request.setPaymentGroup(PaymentGroup.PRODUCT.name());

    PaymentCard paymentCard = new PaymentCard();
    paymentCard.setCardHolderName(firstname + " " + lastname);
    paymentCard.setCardNumber(cardnumber);
    paymentCard.setExpireMonth(expiremonth);
    paymentCard.setExpireYear(expireyear);
    paymentCard.setCvc(cvc);
    paymentCard.setRegisterCard(0);
    request.setPaymentCard(paymentCard);

    Buyer buyer = new Buyer();
    buyer.setId("BY789");
    buyer.setName(firstname);
    buyer.setSurname(lastname);
    buyer.setGsmNumber(phonenumber);
    buyer.setEmail(email);
    buyer.setIdentityNumber(idnumber);
    // buyer.setLastLoginDate("2015-10-05 12:43:35");
    // buyer.setRegistrationDate("2013-04-21 15:12:09");
    buyer.setRegistrationAddress(useraddress);
    buyer.setIp(userip);
    buyer.setCity(usercity);
    buyer.setCountry(usercountry);
    buyer.setZipCode(userzipcode);
    request.setBuyer(buyer);

    Address shippingAddress = new Address();
    shippingAddress.setContactName(firstname + " " + lastname);
    shippingAddress.setCity(usercity);
    shippingAddress.setCountry(usercountry);
    shippingAddress.setAddress(useraddress);
    shippingAddress.setZipCode(userzipcode);
    request.setShippingAddress(shippingAddress);

    Address billingAddress = new Address();
    billingAddress.setContactName(firstname + " " + lastname);
    billingAddress.setCity(usercity);
    billingAddress.setCountry(usercountry);
    billingAddress.setAddress(useraddress);
    billingAddress.setZipCode(userzipcode);
    request.setBillingAddress(billingAddress);

    List<BasketItem> basketItems = new ArrayList<BasketItem>();
    BasketItem firstBasketItem = new BasketItem();
    firstBasketItem.setId("BI101");
    firstBasketItem.setName("Rental Villa");
    firstBasketItem.setCategory1("Rental Villa");
    firstBasketItem.setCategory2("Rental Villa");
    firstBasketItem.setItemType(BasketItemType.VIRTUAL.name());
    firstBasketItem.setPrice(new BigDecimal(price));
    basketItems.add(firstBasketItem);

    request.setBasketItems(basketItems);

    return Payment.create(request, paymentProcessingOptions);

  };
}
