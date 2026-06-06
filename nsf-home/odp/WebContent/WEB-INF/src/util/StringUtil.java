package util;

import java.util.Objects;

public enum StringUtil {
  ;
  
  public static boolean isEmpty(String value) {
    return value == null || value.isEmpty();
  }
  
  public static boolean isNotEmpty(String value) {
    return !isEmpty(value);
  }
  
  public static String toString(Object value) {
    return Objects.toString(value, "");
  }
}
