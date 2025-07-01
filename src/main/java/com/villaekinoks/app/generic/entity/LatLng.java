package com.villaekinoks.app.generic.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Setter
@Getter
public class LatLng {

  private Double latitude;

  private Double longitude;
}
