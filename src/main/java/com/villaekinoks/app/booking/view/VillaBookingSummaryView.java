package com.villaekinoks.app.booking.view;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.villaekinoks.app.booking.VillaBooking;
import com.villaekinoks.app.booking.VillaBookingAdditionalService;
import com.villaekinoks.app.booking.VillaBookingStatus;
import com.villaekinoks.app.booking.VillaBookingTimestamps;
import com.villaekinoks.app.payment.Payment;
import com.villaekinoks.app.user.VillaGuestUser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VillaBookingSummaryView {

  private String id;

  private VillaBookingTimestamps timestamps;

  private String startdate;

  private String enddate;

  private VillaBookingStatus status;

  @JsonIncludeProperties({ "id", "personalinfo" })
  private VillaGuestUser inquiror;

  private Set<VillaBookingAdditionalService> services;

  private Payment bookingpayment;

  private Integer numberofguests;

  public static VillaBookingSummaryView fromEntity(VillaBooking entity) {
    VillaBookingSummaryView view = new VillaBookingSummaryView();
    view.setId(entity.getId());
    view.setTimestamps(entity.getTimestamps());
    view.setStartdate(entity.getStartdate());
    view.setEnddate(entity.getEnddate());
    view.setStatus(entity.getStatus());
    view.setInquiror(entity.getInquiror());
    view.setServices(entity.getServices());
    view.setBookingpayment(entity.getBookingpayment());
    view.setNumberofguests(entity.getNumberofguests());
    return view;
  }
}
