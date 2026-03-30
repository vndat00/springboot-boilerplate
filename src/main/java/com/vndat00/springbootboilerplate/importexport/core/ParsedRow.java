package com.vndat00.springbootboilerplate.importexport.core;

import java.util.Map;

public record ParsedRow(int rowNumber, Map<String, String> cells) {}
