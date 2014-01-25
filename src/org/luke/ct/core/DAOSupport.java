package org.luke.ct.core;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.luke.ct.model.BaseEntity;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

/**
 * 以jdo方式實現的基礎數據類
 * 
 * @since 2011/09/01 1.0
 * @version 1.0
 * @author kyle
 * 
 * @param <T>
 *          實體類 entity class
 */
public class DAOSupport<T extends BaseEntity> implements IDAO<T> {

  // 獲得當前T的類型
  protected Class<T> entityClass;// = GenericsUtil.getSuperClassGenricType(this.getClass());
  // 日誌對像
  private static final Logger log = Logger.getLogger(DAOSupport.class.getName());

  @SuppressWarnings("unchecked")
  public DAOSupport() {
    // 獲得當前T的類型
    this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
  }

  /**
   * 添加實體
   */

  public void add(T entity) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      // 連同encodeKey一起存起來
      pm.makePersistent(entity);
      entity.setEncodedKey();
      pm.makePersistent(entity);
      log.info("add entity" + entity.getClass().getName());
    } catch (Exception e) {
      log.severe("添加實體 " + entity.getClass().getName() + " 時候出錯!");
      log.severe(e.getMessage());
    } finally {
      pm.close();
    }
  }

  /**
   * 刪除實體
   */

  public void delete(Key key) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      T delEntity = pm.getObjectById(entityClass, key);
      pm.deletePersistent(delEntity);
      log.info("delete entity" + entityClass.getName());
    } catch (Exception e) {
      log.severe("刪除實體 " + entityClass.getName() + "時候出錯!");
      log.severe(e.getMessage());
    } finally {
      pm.close();
    }
  }

  /**
   * 更新實體
   */

  public void modify(T entity) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    try {
      // 更新數據，直接調用makePersistent()方法的，要求實體類註解了如下
      // @PersistenceCapable(detachable="true")
      pm.makePersistent(entity);
      log.info("modify entity" + entityClass.getName());
    } catch (Exception e) {
      log.severe("更新實體 " + entity.getClass().getName() + "時候出錯!");
      log.severe(e.getMessage());
    } finally {
      pm.close();
    }
  }

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
  @SuppressWarnings("unchecked")
  public QueryResult<T> getPaginationData(String filter, String ordering, long firstResult, long maxResult) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    QueryResult<T> qr = new QueryResult<T>();
    try {
      Query query = GSQLUtil.getSelectSqlStr(pm, entityClass, filter, ordering, firstResult, maxResult);
      Object obj = query.execute();
      if (obj != null) {
        List<T> list = (List<T>) obj;
        qr.setResultList(list);
        // 不調用list.size()方法，那麼調用pm.close()後，再次使用list會出現Object Manager has been closed Exception
        list.size();
        Query queryCount = GSQLUtil.getSelectSqlStr(pm, entityClass, filter);
        List<T> l = (List<T>) queryCount.execute();
        qr.setTotalRecord(l.size());
      } else {
        qr.setResultList(new ArrayList<T>());
        qr.setTotalRecord((long) 0);
      }
      log.info("query entity" + entityClass.getName());
    } catch (Exception e) {
      log.severe("查詢時候出錯了！");
      log.severe(e.getMessage());
    } finally {
      pm.close();
    }
    return qr;
  }

  /**
   * @param filter
   *          查詢過濾條件
   * @param odering
   *          查詢後排序條件
   * @return 查詢處理好的數據
   * @author kyle
   */

  public QueryResult<T> getPaginationData(String filter, String ordering) {
    long min = -1;
    return getPaginationData(filter, ordering, min, min);
  }

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

  public QueryResult<T> getPaginationData(String filter, long firstResult, long maxResult) {
    return getPaginationData(filter, null, firstResult, maxResult);
  }

  /**
   * 
   * @param firstResult
   *          開始記錄
   * @param maxResult
   *          檢索結果的最大數量
   * @return 查詢處理好的數據
   * @author kyle
   */

  public QueryResult<T> getPaginationData(long firstResult, long maxResult) {
    return getPaginationData(null, null, firstResult, maxResult);
  }

  /**
   * @param filter
   *          查詢過濾條件
   * @return 查詢處理好的數據
   * @author kyle
   */

  public QueryResult<T> getPaginationData(String filter) {
    return getPaginationData(filter, null);
  }

  /**
   * @return 查詢處理好的數據
   * @author kyle
   */

  public QueryResult<T> getPaginationData() {
    return getPaginationData(null);
  }

  public T getDataByID(Key key) {
    PersistenceManager pm = PMF.get().getPersistenceManager();
    T entity = null;
    try {
      entity = (T) pm.getObjectById(entityClass, key);
      log.info("query entity" + entityClass.getName());
    } catch (Exception e) {
      log.severe("查詢時候出錯了！");
      log.severe(e.getMessage());
    } finally {
      pm.close();
    }
    return entity;
  }

  public T getDataByID(String encodeKey) {
    try {
      Key key = KeyFactory.stringToKey(encodeKey);
      return getDataByID(key);
    } catch (IllegalArgumentException e) {
      log.severe("encodeKey不合法!error:" + e.getMessage());
      return null;
    }
  }

  public T getDataByID(Long key) {
    try {
      Key k = KeyFactory.createKey(entityClass.getSimpleName(), key);
      return getDataByID(k);
    } catch (IllegalArgumentException e) {
      log.severe("Long Key不合法!error:" + e.getMessage());
      return null;
    }
  }

}
