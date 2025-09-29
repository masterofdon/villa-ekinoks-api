package com.villaekinoks.app.booking.view;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.booking.VillaBooking;

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

  public static VillaBookingSummaryView fromEntity(VillaBooking entity) {
    VillaBookingSummaryView view = new VillaBookingSummaryView();
    view.setId(entity.getId());
    return view;
  }
}
