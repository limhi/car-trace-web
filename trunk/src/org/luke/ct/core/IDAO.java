package org.luke.ct.core;

import com.google.appengine.api.datastore.Key;

/**
 * 數據處理的基礎接口
 * 
 * @since 2011/09/01 1.0
 * @version 1.0
 * @author kyle
 * 
 * @param <T>
 *          實體類 entity class
 */
public interface IDAO<T> {

  /**
   * 增加實體
   * 
   * @param entity
   */
  public void add(T entity);

  /**
   * 更新實體
   * 
   * @param entity
   */
  public void modify(T entity);

  /**
   * 刪除實體
   * 
   * @param key
   *          id
   */
  public void delete(Key key);

  /**
   * @param filter
   *          查詢過濾條件
   * @param ordering
   *          查詢後排序條件
   * @param firstResult
   *          開始記錄
   * @param maxResult
   *          檢索結果的最大數量
   * @return 查詢處理好的數據
   * @author kyle
   */
  public QueryResult<T> getPaginationData(String filter, String ordering, long firstResult, long maxResult);

  /**
   * @param filter
   *          查詢過濾條件
   * @param odering
   *          查詢後排序條件
   * @return 查詢處理好的數據
   * @author kyle
   */
  public QueryResult<T> getPaginationData(String filter, String ordering);

  /**
   * 
   * @param filter
   *          查詢過濾條件
   * @param firstResult
   *          開始記錄
   * @param maxResult
   *          檢索結果的最大數量
   * @return 查詢處理好的數據
   * @author kyle
   */
  public QueryResult<T> getPaginationData(String filter, long firstResult, long maxResult);

  /**
   * 
   * @param firstResult
   *          開始記錄
   * @param maxResult
   *          檢索結果的最大數量
   * @return 查詢處理好的數據
   * @author kyle
   */
  public QueryResult<T> getPaginationData(long firstResult, long maxResult);

  /**
   * @param filter
   *          查詢過濾條件
   * @return 查詢處理好的數據
   * @author kyle
   */
  public QueryResult<T> getPaginationData(String filter);

  /**
   * @return 查詢處理好的數據
   * @author kyle
   */
  public QueryResult<T> getPaginationData();

  /**
   * @return 根據key查詢資料
   * @author luke
   */
  public T getDataByID(Key key);

  /**
   * @return 根據key查詢資料
   * @author luke
   */
  public T getDataByID(String encodeKey);

  /**
   * @return 根據key查詢資料
   * @author luke
   */
  public T getDataByID(Long key);

}
