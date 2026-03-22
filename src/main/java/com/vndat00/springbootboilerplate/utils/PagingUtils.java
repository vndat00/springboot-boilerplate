package com.vndat00.springbootboilerplate.utils;

import org.apache.commons.text.CaseUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.JpaSort;

public final class PagingUtils {

  public static final String ASC = "asc";

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
}
