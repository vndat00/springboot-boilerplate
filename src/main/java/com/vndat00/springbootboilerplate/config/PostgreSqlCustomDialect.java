// package com.vndat00.springbootboilerplate.config;
//
// import com.vladmihalcea.hibernate.type.array.StringArrayType;
// import com.vladmihalcea.hibernate.type.json.JsonNodeStringType;
// import java.sql.Types;
// import org.hibernate.dialect.PostgreSQL10Dialect;
//
// public class PostgreSqlCustomDialect extends PostgreSQL10Dialect {
//
//  public PostgreSqlCustomDialect() {
//    super();
//    registerHibernateType(Types.OTHER, JsonNodeStringType.class.getName());
//    registerHibernateType(Types.ARRAY, StringArrayType.class.getName());
//  }
// }
package com.vndat00.springbootboilerplate.config;

import org.hibernate.dialect.PostgreSQLDialect;

public class PostgreSqlCustomDialect extends PostgreSQLDialect {
  public PostgreSqlCustomDialect() {
    super();
  }
}
