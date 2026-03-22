package com.vndat00.springbootboilerplate.utils;

import java.util.List;
import java.util.function.Function;

import com.vndat00.springbootboilerplate.payload.general.PageInfo;
import com.vndat00.springbootboilerplate.payload.general.ResponseDataAPI;
import org.springframework.data.domain.Page;

public class ResponseDataUtils {
  private ResponseDataUtils() {}

  /**
   * Converts a paginated list of entities into a response data object with pagination info.
   *
   * @param <T> The type of the entity in the page.
   * @param <R> The type of the response object.
   * @param page The paginated list of entities.
   * @param mapper A function to map a list of entities to a list of response objects.
   * @return A ResponseDataAPI object containing the mapped data and pagination info.
   */
  public static <T, R> ResponseDataAPI toResponseData(Page<T> page, Function<T, R> mapper) {
    PageInfo pageInfo =
        new PageInfo(page.getNumber() + 1, page.getTotalPages(), page.getTotalElements());
    List<R> data = page.getContent().stream().map(mapper).toList();
    return ResponseDataAPI.success(data, pageInfo);
  }

  public static <T, R> ResponseDataAPI toResponseDataWithListMapper(
      Page<T> page, Function<List<T>, List<R>> mapper) {
    PageInfo pageInfo =
        new PageInfo(page.getNumber() + 1, page.getTotalPages(), page.getTotalElements());
    List<R> data = mapper.apply(page.getContent());
    return ResponseDataAPI.success(data, pageInfo);
  }

  public static ResponseDataAPI toResponseData(Object data) {
    return ResponseDataAPI.successWithoutMeta(data);
  }

  public static ResponseDataAPI toResponseData() {
    return ResponseDataAPI.successWithoutMetaAndData();
  }
}
