package com.fullcontact.apilib.stats;

/** Interface which can be implemented for getting stats for a particular request */
public interface FCStats {
  long personEnrichResponseTime(long time);

  long companyEnrichResponseTime(long time);

  long companySearchResponseTime(long time);

  int personEnrichResponseCode(int code);

  int companyEnrichResponseCode(int code);

  int companySearchResponseCode(int code);
}
