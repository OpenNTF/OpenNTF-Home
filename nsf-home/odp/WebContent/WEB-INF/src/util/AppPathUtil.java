package util;

public enum AppPathUtil {
  ;

  public static String concat(final char delim, final String... parts) {
    if (parts == null || parts.length == 0) {
      return "";
    }
    String path = parts[0];
    for (int i = 1; i < parts.length; i++) {
      path = concat(path, parts[i], delim);
    }
    return path;
  }

  public static String concat(final String... parts) {
    return concat('/', parts);
  }

  public static String concat(String path1, String path2, char sep) {
    if (StringUtil.isEmpty(path1)) {
      return path2;
    }
    if (StringUtil.isEmpty(path2)) {
      return path1;
    }
    StringBuilder b = new StringBuilder();
    if (path1.charAt(path1.length() - 1) == sep) {
      b.append(path1, 0, path1.length() - 1);
    } else {
      b.append(path1);
    }
    b.append(sep);
    if (path2.charAt(0) == sep) {
      b.append(path2, 1, path2.length());
    } else {
      b.append(path2);
    }
    return b.toString();
  }
}
