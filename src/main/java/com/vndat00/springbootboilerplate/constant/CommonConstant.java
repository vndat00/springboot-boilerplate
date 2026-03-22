package com.vndat00.springbootboilerplate.constant;

import java.util.UUID;

public final class CommonConstant {
  public static final String PUT_METHOD = "PUT";

  public static final String SUCCESS = "success";
  public static final String FAILURE = "failure";
  public static final String LANGUAGE_CODE = "ja";
  public static final String TIME_ZONE_JP = "Asia/Tokyo";
  public static final String AUTHORIZATION = "Authorization";
  public static final String TOKEN_TYPE = "Bearer";
  public static final UUID UNKNOWN = UUID.fromString("00000000-0000-0000-0000-000000000000");
  public static final String ROLE_PREFIX = "ROLE_";
  public static final String RULE_PASSWORD =
      "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{8,32})"; // (?=.*[@#$%])
  public static final UUID SYSTEM_ID = new UUID(0, 0);
  public static final int EXPIRE_TIME = 1800;
  public static final long RESTORATION_TIME = 30L * 24 * 60 * 60 * 1000;
  public static final double DISTANCE_BETWEEN_RANK = Math.pow(2, 16);
  public static final long DISTANCE_BETWEEN_POSITION = 65536;
  public static final double DISTANCE_THRESHOLD = 0.5;
  public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
  public static final String DATE_TIME_FORMAT = "yyyy/MM/dd HH:mm";
  public static final String DATE_FORMAT_JP = "yyyy年M月d日";
  public static final String DATE_TIME_FORMAT_JP = "yyyy年M月d日 H:mm";
  public static final String DATE_FORMAT_JP2 = "M月d日";
  public static final String EMAIL_PATTERN =
      "^[a-zA-Z0-9]+([._%+-][a-zA-Z0-9]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)+$";
  public static final Double MAX_VALUE = 999999999999999.0;
  public static final String BASE36 = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
  public static final int MAX_CODE_LENGTH = 6;
  public static final int POLL_INTERVAL_IN_SECONDS = 5;
  public static final String UPPERCASE_ENUM_PATTERN = "^[A-Z_][A-Z_]*$";

  private CommonConstant() {}
}
