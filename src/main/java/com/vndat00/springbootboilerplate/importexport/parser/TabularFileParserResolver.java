package com.vndat00.springbootboilerplate.importexport.parser;

import com.vndat00.springbootboilerplate.constant.MessageConstant;
import com.vndat00.springbootboilerplate.exception.BadRequestException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TabularFileParserResolver {
  private final List<TabularFileParser> parsers;

  public TabularFileParser resolve(String fileName) {
    return parsers.stream()
        .filter(parser -> parser.supports(fileName))
        .findFirst()
        .orElseThrow(() -> new BadRequestException(MessageConstant.BAD_REQUEST));
  }
}
