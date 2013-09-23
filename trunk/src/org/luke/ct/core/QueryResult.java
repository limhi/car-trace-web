package org.luke.ct.core;

import java.util.List;

/**
 *查詢結果封裝類 query result class
 * @since 2011/09/01 1.0
 * @version 1.0
 * @author kyle
 *
 * @param <T> 實體類 entity class
 */

public class QueryResult<T> {
  // 查詢結果記錄query result record
  private List<T> resultList;
  // 總記錄數 total record  
  private long totalRecord;
  
  /**
   * 獲取所有滿足條件的實體對像list
   * @return list 所有滿足條件的實體對像
   * @since 2011/09/01 1.0
   */
  public List<T> getResultList() {
    return resultList;
  }
  
  public void setResultList(List<T> resultList) {
    this.resultList = resultList;
  }
  
  /**
   * 獲取總共有好多條記錄
   * @return 滿足條件的記錄數
   * @since 2011/09/01 1.0
   */
  public long getTotalRecord() {
    return totalRecord;
  }
  
  public void setTotalRecord(long totalRecord) {
    this.totalRecord = totalRecord;
  }
}