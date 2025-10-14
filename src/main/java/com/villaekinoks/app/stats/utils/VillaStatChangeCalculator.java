package com.villaekinoks.app.stats.utils;

public class VillaStatChangeCalculator {

  public static String calculateBookingChange(String prevValue, String newValue) {
    int prev = Integer.parseInt(prevValue);
    int curr = Integer.parseInt(newValue);
    int change = curr - prev;
    return String.valueOf(change);
  }
}
