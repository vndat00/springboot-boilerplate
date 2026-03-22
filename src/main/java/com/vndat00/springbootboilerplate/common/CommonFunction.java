package com.vndat00.springbootboilerplate.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vndat00.springbootboilerplate.config.properties.ServerProperties;
import com.vndat00.springbootboilerplate.constant.CommonConstant;
import com.vndat00.springbootboilerplate.payload.response.ErrorResponse;
import com.vndat00.springbootboilerplate.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;

@Slf4j
public final class CommonFunction {

  private static final String ERROR_FILE = "errors.yml";
  private static final String VALIDATION_FILE = "validations.yml";

  private CommonFunction() {}

  /**
   * Parse object to json string
   *
   * @param ob {@link Object} to parse
   * @return String
   */
  public static String convertToJSONString(Object ob) {
    try {
      ObjectMapper mapper = new ObjectMapper();
      return mapper.writeValueAsString(ob);
    } catch (Exception e) {
      LogUtils.error(e.getMessage());
      return null;
    }
  }

  public static List<String> convertObjectToListString(Object object) {
    List<String> attachments = null;
    if (object instanceof List<?>) {
      attachments =
          ((List<?>) object)
              .stream().filter(String.class::isInstance).map(String.class::cast).toList();
    }
    return attachments;
  }

  /**
   * Generate a queue name
   *
   * @param userId User's id
   * @return String
   */
  public static String generateQueueName(UUID userId) {
    return generateCode(64) + "|" + userId;
  }

  /**
   * Generate a random code
   *
   * @param length Code length
   * @return String code
   */
  public static String generateCode(int length) {
    List<CharacterRule> rules =
        Arrays.asList(
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.LowerCase, 1),
            new CharacterRule(EnglishCharacterData.Digit, 1));

    PasswordGenerator generator = new PasswordGenerator();
    return generator.generatePassword(length, rules);
  }

  /**
   * Generate a random code
   *
   * @param length Code length
   * @return String code
   */
  public static String generateCodeDigit(int length) {
    List<CharacterRule> rules = List.of(new CharacterRule(EnglishCharacterData.Digit, 1));

    PasswordGenerator generator = new PasswordGenerator();
    return generator.generatePassword(length, rules);
  }

  /**
   * Get current date time
   *
   * @return Timestamp
   */
  public static Timestamp getCurrentDateTime() {
    Date date = new Date();
    return new Timestamp(date.getTime());
  }

  /**
   * Get the start of the specified month.
   *
   * @return Timestamp
   */
  public static Timestamp getStartOfMonth(YearMonth yearMonth) {
    return Timestamp.valueOf(yearMonth.atDay(1).atStartOfDay());
  }

  /**
   * Get the end of the specified month.
   *
   * @return Timestamp
   */
  public static Timestamp getEndOfMonth(YearMonth yearMonth) {
    return Timestamp.valueOf(yearMonth.atEndOfMonth().atTime(23, 59, 59, 999999999));
  }

  /**
   * Extract exception error
   *
   * @param error String error
   * @return ErrorResponse
   */
  @SuppressWarnings("unchecked")
  public static ErrorResponse getExceptionError(String error) {
    ReadYAML readYAML = new ReadYAML();
    Map<String, Object> errors = readYAML.getValueFromYAML(ERROR_FILE);
    Map<String, Object> objError = (Map<String, Object>) errors.get(error);
    String code = (String) objError.get("code");
    String message = (String) objError.get("message");
    return new ErrorResponse(code, message);
  }

  /**
   * Extract validation error
   *
   * @param resource file error
   * @param fieldName field error
   * @param error String error
   * @return ErrorResponse
   */
  @SuppressWarnings("unchecked")
  public static ErrorResponse getValidationError(String resource, String fieldName, String error) {
    if (fieldName.contains("[")) {
      fieldName = handleFieldName(fieldName);
    }

    ReadYAML readYAML = new ReadYAML();
    Map<String, Object> errors = readYAML.getValueFromYAML(VALIDATION_FILE);
    Map<String, Object> fields = (Map<String, Object>) errors.get(resource);
    Map<String, Object> objErrors = (Map<String, Object>) fields.get(fieldName);
    Map<String, Object> objError = (Map<String, Object>) objErrors.get(error);
    String code = (String) objError.get("code");
    String message = (String) objError.get("message");
    return new ErrorResponse(code, message);
  }

  /**
   * Convert date into pattern "yyyy-MM-dd"
   *
   * @param inputDate Input date
   * @return Timestamp
   * @throws ParseException {@link ParseException} Error during parser
   */
  public static Timestamp yyyyMMddFormat(String inputDate) throws ParseException {
    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(inputDate);
    return new Timestamp(date.getTime());
  }

  /**
   * Convert date into pattern "yyyy-MM-dd"
   *
   * @param inputDate Input date
   * @return Timestamp
   */
  public static Timestamp yyyyMMddHHmmSSFormat(String inputDate) {
    try {
      Date date =
          new SimpleDateFormat(CommonConstant.DATE_FORMAT).parse(inputDate.replace("T", " "));
      return new Timestamp(date.getTime());
    } catch (Exception e) {
      LogUtils.error(e.getMessage());
      return null;
    }
  }

  public static Timestamp yyyyMMddHHmmSSFormatUTC(String inputDate) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CommonConstant.DATE_FORMAT);
    LocalDateTime localDateTime = LocalDateTime.parse(inputDate, formatter);
    return Timestamp.from(localDateTime.atZone(ZoneOffset.UTC).toInstant());
  }

  public static String convertTimeStampToStringUTC(Timestamp timestamp) {
    return LocalDateTime.ofInstant(timestamp.toInstant(), ZoneOffset.UTC)
        .format(DateTimeFormatter.ofPattern(CommonConstant.DATE_FORMAT));
  }

  public static String convertTimeStampToStringJP(Timestamp timestamp) {
    return LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.of(CommonConstant.TIME_ZONE_JP))
        .format(DateTimeFormatter.ofPattern(CommonConstant.DATE_TIME_FORMAT_JP));
  }

  public static String convertTimeStampToStringJP2(Timestamp timestamp) {
    return LocalDateTime.ofInstant(timestamp.toInstant(), ZoneId.of(CommonConstant.TIME_ZONE_JP))
        .format(DateTimeFormatter.ofPattern(CommonConstant.DATE_FORMAT_JP2));
  }

  /**
   * Convert camel case to snake case
   *
   * @param input string type camel case
   * @return String type snake case
   */
  public static String convertToSnakeCase(String input) {
    return input.replaceAll("([^_A-Z])([A-Z])", "$1_$2").toLowerCase();
  }

  public static Timestamp getMinTime(int year, int month, int day) {
    if (day == 0) {
      String time = year + "-" + convertDayAndMonthToString(month) + "-01 00:00:00";
      return CommonFunction.yyyyMMddHHmmSSFormat(time);
    }
    String time =
        year
            + "-"
            + convertDayAndMonthToString(month)
            + "-"
            + convertDayAndMonthToString(day)
            + " 00:00:00";
    return CommonFunction.yyyyMMddHHmmSSFormat(time);
  }

  public static Timestamp getMaxTime(int year, int month, int day) {
    if (day == 0) {
      String time =
          year
              + "-"
              + convertDayAndMonthToString(month)
              + "-"
              + convertDayAndMonthToString(getDaysInMonth(year, month))
              + " 23:59:59";
      return CommonFunction.yyyyMMddHHmmSSFormat(time);
    }

    String time =
        year
            + "-"
            + convertDayAndMonthToString(month)
            + "-"
            + convertDayAndMonthToString(day)
            + " 23:59:59";
    return CommonFunction.yyyyMMddHHmmSSFormat(time);
  }

  public static String convertDayAndMonthToString(int value) {
    if (value < 10) {
      return "0" + value;
    }
    return "" + value;
  }

  public static int getDaysInMonth(int year, int month) {
    YearMonth yearMonth = YearMonth.of(year, month);
    return yearMonth.lengthOfMonth();
  }

  public static Timestamp getTimeOfCurrentTimeZone(Timestamp timeByUTC) {
    TimeZone timeZone = TimeZone.getDefault();
    int offsetInMillis = timeZone.getRawOffset();
    long adjustedTimeInMillis = timeByUTC.getTime() + offsetInMillis;
    return new Timestamp(adjustedTimeInMillis);
  }

  public static Timestamp getTimeOfUTC(Timestamp time) {
    TimeZone timeZone = TimeZone.getDefault();
    int offsetInMillis = timeZone.getRawOffset();
    long adjustedTimeInMillis = time.getTime() - offsetInMillis;
    return new Timestamp(adjustedTimeInMillis);
  }

  public static String handleFieldName(String fieldName) {
    StringBuilder result = new StringBuilder();
    boolean inBrackets = false;

    for (char c : fieldName.toCharArray()) {
      if (c == '[') {
        inBrackets = true;
        result.append(c);
      } else if (c == ']') {
        inBrackets = false;
        result.append(c);
      } else if (!inBrackets) {
        result.append(c);
      }
    }

    return result.toString();
  }

  public static boolean isNullOrBlank(String str) {
    return str == null || str.trim().isEmpty();
  }

  public static String concatServerUrl(String url) {
    if (!CommonFunction.isNullOrBlank(url)) {
      if (url.startsWith(ServerProperties.getServerVersion())) {
        return ServerProperties.getServerUrl() + url;
      } else {
        return url;
      }
    }
    return null;
  }

  public static String trimServerUrl(String url) {
    if (!CommonFunction.isNullOrBlank(url)) {
      if (url.startsWith(ServerProperties.getServerUrl())) {
        return url.replace(ServerProperties.getServerUrl(), "");
      } else {
        return url;
      }
    }
    return null;
  }

  public static String handleContentSearch(String content) {
    return content.replaceAll(
        "([\\[\\]\\-\\\\\\~\\`\\!\\@\\#\\$\\%\\^\\&\\*\\(\\)\\_\\+\\=\\{\\}\\|\\:\\;\\'\\<\\>\\,\\.\\?\\/])",
        "\\\\$1");
  }

  public static String encode(String value) {
    try {
      return URLEncoder.encode(value, StandardCharsets.UTF_8);
    } catch (Exception e) {
      LogUtils.error(e.getMessage());
      return value;
    }
  }

  public static String decode(String value) {
    try {
      return URLDecoder.decode(value, StandardCharsets.UTF_8);
    } catch (Exception e) {
      LogUtils.error(e.getMessage());
      return value;
    }
  }

  public static int calAgeByBirthDay(LocalDate birthDay) {
    return Period.between(birthDay, LocalDate.now()).getYears();
  }

  public static String convertArrayToString(String[] array) {
    StringBuilder patternBuilder = new StringBuilder();
    for (String item : array) {
      if (!patternBuilder.isEmpty()) {
        patternBuilder.append("|");
      }
      patternBuilder.append(item);
    }
    return patternBuilder.toString();
  }

  public static String getPrePostcode(String postcode) {
    return postcode.substring(0, 3);
  }

  public static String getEndPostcode(String postcode) {
    return postcode.substring(3, 7);
  }

  public static String generateRelatedPersonInfoCode() {
    String code = "WPR";
    LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
    code =
        code
            + dateTime.getYear()
            + handelNumber(dateTime.getMonth().getValue())
            + handelNumber(dateTime.getDayOfMonth())
            + handelNumber(dateTime.getHour())
            + handelNumber(dateTime.getMinute());
    return code;
  }

  public static String handelNumber(int number) {
    if (number > 9) {
      return "" + number;
    } else {
      return "0" + number;
    }
  }

  public static String trim(String str) {
    if (str == null || str.isEmpty()) {
      return str;
    }

    int start = 0;
    int end = str.length() - 1;

    while (start <= end && (str.charAt(start) == ' ' || str.charAt(start) == '　')) {
      start++;
    }

    while (end >= start && (str.charAt(end) == ' ' || str.charAt(end) == '　')) {
      end--;
    }

    return str.substring(start, end + 1);
  }

  public static boolean isJson(String json) {
    if (json == null || json.trim().isEmpty()) {
      return true;
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode node = objectMapper.readTree(json);
      return node.isObject() || node.isArray();
    } catch (JsonProcessingException e) {
      return false;
    }
  }

  public static <T> T deserializeJson(String json, TypeReference<T> typeReference) {
    if (json == null) {
      return null;
    }
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.readValue(json, typeReference);
    } catch (Exception e) {
      LogUtils.error(e.getMessage());
      return null;
    }
  }

  public static boolean isScreamingSnakeCase(String input) {
    Pattern screamingSnakeCasePattern = Pattern.compile("^[A-Z]+(_[A-Z]+)*$");
    if (input == null) {
      return false;
    }
    return screamingSnakeCasePattern.matcher(input).matches();
  }

  public static boolean isKebabCase(String input) {
    Pattern kebabCasePattern = Pattern.compile("^[a-z]+(-[a-z]+)*$");
    if (input == null) {
      return false;
    }
    return kebabCasePattern.matcher(input).matches();
  }

  public static String handleJpContent(String content) {
    if (isNullOrBlank(content)) {
      return content;
    }
    content = content.replaceAll("[〇①②③④⑤⑥⑦⑧⑨⑩⑪⑫⑬⑭⑮⑯⑰⑱⑲⑳㉑-㉟]", "");
    content = content.replaceAll("（.*?）|\\(.*?\\)", " ");

    List<String> urls = new ArrayList<>();
    List<String> mentions = new ArrayList<>();
    List<String> hashtags = new ArrayList<>();

    Pattern urlPattern = Pattern.compile("https?://[\\p{L}\\p{N}\\p{P}\\p{S}]+");
    Pattern mentionPattern = Pattern.compile("@[\\p{L}\\p{N}_]+");
    Pattern hashtagPattern = Pattern.compile("#[\\p{L}\\p{N}_]+");

    Matcher mUrl = urlPattern.matcher(content);
    while (mUrl.find()) urls.add(mUrl.group());
    for (int i = 0; i < urls.size(); i++) {
      content = content.replace(urls.get(i), "URL" + i);
    }

    Matcher mMention = mentionPattern.matcher(content);
    while (mMention.find()) mentions.add(mMention.group());
    for (int i = 0; i < mentions.size(); i++) {
      content = content.replace(mentions.get(i), "MENTION" + i);
    }

    Matcher mHashtag = hashtagPattern.matcher(content);
    while (mHashtag.find()) hashtags.add(mHashtag.group());
    for (int i = 0; i < hashtags.size(); i++) {
      content = content.replace(hashtags.get(i), "HASHTAG" + i);
    }

    String[] lines = content.split("\\n");
    StringBuilder cleaned = new StringBuilder();

    for (String line : lines) {
      String temp =
          line.replaceAll("[^\\p{L}\\p{M}\\p{N}\\p{Z}\\u3000\\u30FC\\uFF65\\uFF9E\\uFF9F]", " ")
              .replaceAll("[^\\u0000-\\uFFFF]", "")
              .replaceAll(" {2,}", " ")
              .trim();

      cleaned.append(temp).append("\n");
    }

    content = cleaned.toString().trim();

    // Restore URL, mention, hashtag
    for (int i = 0; i < urls.size(); i++) {
      content = content.replace("URL" + i, urls.get(i));
    }
    for (int i = 0; i < mentions.size(); i++) {
      content = content.replace("MENTION" + i, mentions.get(i));
    }
    for (int i = 0; i < hashtags.size(); i++) {
      content = content.replace("HASHTAG" + i, hashtags.get(i));
    }

    return content.replaceAll("^[\\n\\r]+", "").replaceAll("[\\n\\r\\s\uFE0F]+$", "");
  }
}
