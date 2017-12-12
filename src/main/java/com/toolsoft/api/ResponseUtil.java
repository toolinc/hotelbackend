package com.toolsoft.api;

import javax.servlet.http.HttpServletResponse;

final class ResponseUtil {

  private static final String JSON = "application/json";
  private static final String CORS = "Access-Control-Allow-Origin";
  private static final String CORS_VALUE = "*";

  private ResponseUtil() {}

  static final void setJsonContentType(HttpServletResponse response) {
    response.setContentType(JSON);
  }

  /*
  * Cross-Origin Resource Sharing (CORS)
   */
  static final void setCors(HttpServletResponse response) {
    response.setHeader(CORS, CORS_VALUE);
  }
}
