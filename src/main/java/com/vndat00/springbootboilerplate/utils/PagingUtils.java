package com.vndat00.springbootboilerplate.utils;

import java.util.regex.Pattern;
import org.apache.commons.text.CaseUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.util.StringUtils;

public final class PagingUtils {

  public static final String ASC = "asc";
  private static final String SORT_SEPARATOR = ";";
  private static final String SORT_DIRECTION_SEPARATOR = ":";

  private PagingUtils() {}

  /**
   * Handle make page request for query
   *
   * @param sortBy Sort By Field
   * @param order Order By Desc Or Asc
   * @param page Page No
   * @param paging Page Size
   * @return Pageable
   */
  public static Pageable makePageRequest(String sortBy, String order, int page, int paging) {
    String sortField = CaseUtils.toCamelCase(sortBy, false, '_');
    return makePageRequestWithNativeQuery(sortField, order, page, paging);
  }

  public static Pageable makePageRequestWithNativeQuery(
      String sortBy, String order, int page, int paging) {
    Sort sort =
        ASC.equalsIgnoreCase(order) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    return PageRequest.of(page - 1, paging, sort);
  }

  public static Pageable makePageRequestWithMultipleOrder(
      String sortBy, String order, int page, int paging) {
    Sort sort =
        ASC.equalsIgnoreCase(order)
            ? JpaSort.unsafe(sortBy).ascending()
            : JpaSort.unsafe(sortBy).descending();
    return PageRequest.of(page - 1, paging, sort);
  }

  /**
   * Handle make page request for multi-field sorting Format: field1:asc;field2:desc;field3:asc or
   * single field: created_at:desc Fallback to single field format if multi-field format is not
   * provided
   *
   * @param sorts Multi-field sort specification or single field sort
   * @param order Default order if not specified in sorts (deprecated, kept for backward
   *     compatibility)
   * @param page Page No
   * @param paging Page Size
   * @return Pageable
   */
  public static Pageable makePageRequestMultiSort(
      String sorts, String order, int page, int paging) {
    if (!StringUtils.hasText(sorts)) {
      return makePageRequestWithNativeQuery("createdAt", order, page, paging);
    }

    Sort sort = parseMultiFieldSort(sorts, order);
    return PageRequest.of(page - 1, paging, sort);
  }

  private static Sort parseMultiFieldSort(String sorts, String defaultOrder) {
    // Check if new multi-sort format (field1:asc;field2:desc)
    if (sorts.contains(SORT_SEPARATOR) || sorts.contains(SORT_DIRECTION_SEPARATOR)) {
      Sort result = Sort.unsorted();
      String[] sortFields = sorts.split(Pattern.quote(SORT_SEPARATOR));

      for (String sortField : sortFields) {
        sortField = sortField.trim();
        if (!StringUtils.hasText(sortField)) {
          continue;
        }

        String[] parts = sortField.split(Pattern.quote(SORT_DIRECTION_SEPARATOR));
        String fieldName = CaseUtils.toCamelCase(parts[0].trim(), false, '_');
        String direction = parts.length > 1 ? parts[1].trim() : defaultOrder;

        Sort.Direction dir =
            ASC.equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        result = result.and(Sort.by(dir, fieldName));
      }

      return result;
    } else {
      // Fallback to single field format (backward compatibility)
      String fieldName = CaseUtils.toCamelCase(sorts, false, '_');
      Sort.Direction dir =
          ASC.equalsIgnoreCase(defaultOrder) ? Sort.Direction.ASC : Sort.Direction.DESC;
      return Sort.by(dir, fieldName);
    }
  }
}
