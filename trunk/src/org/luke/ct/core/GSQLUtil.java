package org.luke.ct.core;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

/**
 * 用來處理的GSQL語句的簡單封裝類
 * 
 * @since 2011/09/01 1.0
 * @version 1.0
 * @author kyle
 * 
 */
public class GSQLUtil {

  /**
   * 
   * @param pm
   *          PersistenceManager對象，用來產生Query對像
   * @param c
   *          需要查詢的類
   * @param filter
   *          查詢過濾條件
   * @param ordering
   *          查詢後排序條件
   * @param firstResult
   *          開始記錄
   * @param maxResult
   *          檢索結果的最大數量
   * @return 處理好的Query對像
   */
  public static Query getSelectSqlStr(PersistenceManager pm, Class<?> c, String filter, String ordering, long firstResult, long maxResult) {
    Query query = pm.newQuery(c);
    // 設置過濾條件
    if (filter != null) {
      query.setFilter(filter);
    }
    // 設置排序條件
    if (ordering != null) {
      query.setOrdering(ordering);
    }
    // 設置開始和最大值
    if (firstResult != -1 && maxResult != -1) {
      query.setRange(firstResult, maxResult);
    }
    return query;
  }

  /**
   * 
   * @param pm
   *          PersistenceManager對象，用來產生Query對像
   * @param c
   *          需要查詢的類
   * @param filter
   *          查詢過濾條件
   * @param ordering
   *          查詢後排序條件
   * @return 處理好的Query對像
   */
  public static Query getSelectSqlStr(PersistenceManager pm, Class<?> c, String filter, String ordering) {
    long min = -1;
    return getSelectSqlStr(pm, c, filter, ordering, min, min);
  }

  /**
   * 
   * @param pm
   *          PersistenceManager對象，用來產生Query對像
   * @param c
   *          需要查詢的類
   * @param filter
   *          查詢過濾條件
   * @return 處理好的Query對像
   */
  public static Query getSelectSqlStr(PersistenceManager pm, Class<?> c, String filter) {
    return getSelectSqlStr(pm, c, filter, null);
  }

  /**
   * 
   * @param pm
   *          PersistenceManager對象，用來產生Query對像
   * @param c
   *          需要查詢的類
   * @return 處理好的Query對像
   */
  public static Query getSelectSqlStr(PersistenceManager pm, Class<?> c) {
    return getSelectSqlStr(pm, c, null);
  }

}
