package org.kikkoman.parity;

public class Utils {

  public static void sleep() {
    sleep(1000);
  }

  public static void sleep(int millis) {
    try {
      Thread.sleep(millis);
    }
    catch (Exception e) {}
  }
}
