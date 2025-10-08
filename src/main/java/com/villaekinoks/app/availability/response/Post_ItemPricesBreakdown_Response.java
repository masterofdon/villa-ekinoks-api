package com.villaekinoks.app.availability.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.availability.view.PriceItem;

import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class Post_ItemPricesBreakdown_Response {

  private List<PriceItem> items;
}
