package com.vndat00.springbootboilerplate.repository.dsl;

import static com.querydsl.core.types.OrderSpecifier.NullHandling.NullsLast;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.vndat00.springbootboilerplate.domain.model.Note;
import com.vndat00.springbootboilerplate.domain.model.QNote;
import com.vndat00.springbootboilerplate.payload.request.NoteSearchRequest;
import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.CaseUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class NoteDslRepository {

  private static final String CREATED_DATE = "createdDate";
  private static final String UPDATED_DATE = "updatedDate";
  private static final String DUE_DATE = "dueDate";
  private static final String PRIORITY = "priority";
  private static final String VIEW_COUNT = "viewCount";
  private static final String TITLE = "title";

  public static final List<String> NOTE_FIELDS =
      List.of(CREATED_DATE, UPDATED_DATE, DUE_DATE, PRIORITY, VIEW_COUNT, TITLE);

  private final JPAQueryFactory queryFactory;
  private final QNote qNote = QNote.note;

  public Page<Note> findAll(NoteSearchRequest request, Pageable pageable) {
    JPAQuery<Note> baseQuery = queryFactory.selectFrom(qNote);
    applyFilters(baseQuery, request);
    return paginate(pageable, baseQuery);
  }

  private void applyFilters(JPAQuery<Note> baseQuery, NoteSearchRequest request) {
    if (request == null) {
      return;
    }

    if (StringUtils.hasText(request.getKeyword())) {
      String keyword = request.getKeyword().trim();
      baseQuery.where(
          qNote
              .content
              .containsIgnoreCase(keyword)
              .or(qNote.title.containsIgnoreCase(keyword))
              .or(qNote.description.containsIgnoreCase(keyword))
              .or(qNote.tags.containsIgnoreCase(keyword))
              .or(qNote.authorEmail.containsIgnoreCase(keyword))
              .or(qNote.category.containsIgnoreCase(keyword)));
    }

    BooleanBuilder builder = new BooleanBuilder();

    if (StringUtils.hasText(request.getContent())) {
      builder.and(qNote.content.containsIgnoreCase(request.getContent().trim()));
    }
    if (StringUtils.hasText(request.getTitle())) {
      builder.and(qNote.title.containsIgnoreCase(request.getTitle().trim()));
    }
    if (StringUtils.hasText(request.getAuthorEmail())) {
      builder.and(qNote.authorEmail.containsIgnoreCase(request.getAuthorEmail().trim()));
    }
    if (StringUtils.hasText(request.getCategory())) {
      builder.and(qNote.category.containsIgnoreCase(request.getCategory().trim()));
    }
    if (StringUtils.hasText(request.getTags())) {
      builder.and(qNote.tags.containsIgnoreCase(request.getTags().trim()));
    }
    if (request.getStatus() != null) {
      builder.and(qNote.status.eq(request.getStatus()));
    }
    if (request.getIsCompleted() != null) {
      builder.and(qNote.isCompleted.eq(request.getIsCompleted()));
    }
    if (request.getIsArchived() != null) {
      builder.and(qNote.isArchived.eq(request.getIsArchived()));
    }
    if (request.getPriorityFrom() != null) {
      builder.and(qNote.priority.goe(request.getPriorityFrom()));
    }
    if (request.getPriorityTo() != null) {
      builder.and(qNote.priority.loe(request.getPriorityTo()));
    }
    if (request.getViewCountFrom() != null) {
      builder.and(qNote.viewCount.goe(request.getViewCountFrom()));
    }
    if (request.getViewCountTo() != null) {
      builder.and(qNote.viewCount.loe(request.getViewCountTo()));
    }
    if (request.getEstimatedTimeFrom() != null) {
      builder.and(qNote.estimatedTime.goe(request.getEstimatedTimeFrom()));
    }
    if (request.getEstimatedTimeTo() != null) {
      builder.and(qNote.estimatedTime.loe(request.getEstimatedTimeTo()));
    }
    if (request.getDueDateFrom() != null) {
      builder.and(qNote.dueDate.goe(Timestamp.valueOf(request.getDueDateFrom())));
    }
    if (request.getDueDateTo() != null) {
      builder.and(qNote.dueDate.loe(Timestamp.valueOf(request.getDueDateTo())));
    }

    if (builder.hasValue()) {
      baseQuery.where(builder);
    }
  }

  private Page<Note> paginate(Pageable pageable, JPAQuery<Note> baseQuery) {
    JPAQuery<Note> selectQuery = baseQuery.clone();

    // Build multiple order specifiers from pageable
    OrderSpecifier<?>[] orderSpecifiers = buildOrderSpecifiers(pageable.getSort());
    if (orderSpecifiers.length > 0) {
      selectQuery.orderBy(orderSpecifiers);
    } else {
      // Fallback to created date desc if no sort specified
      selectQuery.orderBy(new OrderSpecifier<>(Order.DESC, qNote.createdAt, NullsLast));
    }

    selectQuery.limit(pageable.getPageSize()).offset(pageable.getOffset());

    List<Note> records = selectQuery.fetch();
    return new PageImpl<>(records, pageable, getTotalCount(baseQuery));
  }

  private OrderSpecifier<?>[] buildOrderSpecifiers(Sort sort) {
    if (sort == null || sort.isUnsorted()) {
      return new OrderSpecifier[0];
    }

    return sort.stream()
        .map(order -> getOrderSpecifier(order.getProperty(), order.getDirection().name()))
        .toArray(OrderSpecifier[]::new);
  }

  private long getTotalCount(JPAQuery<Note> baseQuery) {
    Long total = baseQuery.clone().select(qNote.count()).fetchFirst();
    return total == null ? 0L : total;
  }

  private OrderSpecifier<?> getOrderSpecifier(String givenSort, String givenSortDirection) {
    Order sortDirection = Order.valueOf(givenSortDirection.toUpperCase());
    String sort = normalizeSort(givenSort);

    if (!NOTE_FIELDS.contains(sort)) {
      return new OrderSpecifier<>(sortDirection, qNote.createdAt, NullsLast);
    }

    return switch (sort) {
      case CREATED_DATE -> new OrderSpecifier<>(sortDirection, qNote.createdAt, NullsLast);
      case UPDATED_DATE -> new OrderSpecifier<>(sortDirection, qNote.updatedAt, NullsLast);
      case DUE_DATE -> new OrderSpecifier<>(sortDirection, qNote.dueDate, NullsLast);
      case PRIORITY -> new OrderSpecifier<>(sortDirection, qNote.priority, NullsLast);
      case VIEW_COUNT -> new OrderSpecifier<>(sortDirection, qNote.viewCount, NullsLast);
      case TITLE -> new OrderSpecifier<>(sortDirection, qNote.title, NullsLast);
      default -> new OrderSpecifier<>(Order.DESC, qNote.createdAt, NullsLast);
    };
  }

  private String normalizeSort(String sort) {
    if (!StringUtils.hasText(sort)) {
      return CREATED_DATE;
    }
    if (sort.contains("_")) {
      return CaseUtils.toCamelCase(sort, false, '_');
    }
    return sort;
  }
}
