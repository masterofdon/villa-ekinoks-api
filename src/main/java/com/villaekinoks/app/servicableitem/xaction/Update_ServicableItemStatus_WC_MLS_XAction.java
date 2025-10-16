package com.villaekinoks.app.servicableitem.xaction;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.servicableitem.ServicableItemStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Update_ServicableItemStatus_WC_MLS_XAction {

  private ServicableItemStatus status;
}
