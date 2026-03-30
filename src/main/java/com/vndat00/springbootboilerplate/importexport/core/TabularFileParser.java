package com.vndat00.springbootboilerplate.importexport.core;

import java.io.IOException;
import java.io.InputStream;

public interface TabularFileParser {
  boolean supports(String fileName);

  FileFormat fileFormat();

  ParsedFile parse(InputStream inputStream) throws IOException;
}
