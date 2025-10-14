package com.villaekinoks.app.stats.response;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.villaekinoks.app.stats.VillaStat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Get_VillaStats_WC_MLS_XAction_Response {

  List<VillaStat> stats;

}
