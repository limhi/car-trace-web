package org.luke.ct.api;

import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.inject.Named;

import org.luke.ct.core.CTCommon;
import org.luke.ct.dao.CarPhoneRandomNumberService;
import org.luke.ct.dao.CarPhoneRandomNumberServiceImpl;
import org.luke.ct.dao.CarPhoneRelationService;
import org.luke.ct.dao.CarPhoneRelationServiceImpl;
import org.luke.ct.dao.CarRegService;
import org.luke.ct.dao.CarRegServiceImpl;
import org.luke.ct.dao.PhoneRegService;
import org.luke.ct.dao.PhoneRegServiceImpl;
import org.luke.ct.model.CarPhoneRandomNumber;
import org.luke.ct.model.CarPhoneRelation;
import org.luke.ct.model.CarReg;
import org.luke.ct.model.PhoneReg;

import com.alibaba.fastjson.JSON;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(name = "ctrandom", version = "v1")
public class CarTraceMatchAPI {
  private static CarRegService cr_service = new CarRegServiceImpl();
  private static PhoneRegService pr_service = new PhoneRegServiceImpl();
  private static CarPhoneRandomNumberService cprn_service = new CarPhoneRandomNumberServiceImpl();
  private static CarPhoneRelationService cpr_service = new CarPhoneRelationServiceImpl();
  private static final Logger log = Logger.getLogger(CarTraceMatchAPI.class.getName());

  @ApiMethod(name = "cprandom.list", httpMethod = HttpMethod.GET)
  public List<CarPhoneRandomNumber> getCarPhoneRandomNumberList() {
    return cprn_service.getPaginationData(null, "addTime desc").getResultList();
  }

  @ApiMethod(name = "cpr.list", httpMethod = HttpMethod.GET)
  public List<CarPhoneRelation> getCarPhoneRelationList() {
    return cpr_service.getPaginationData(null, "modTime desc").getResultList();
  }

  @ApiMethod(name = "cprandom.merge", httpMethod = HttpMethod.POST)
  public CarPhoneRandomNumber postCPRNMerge(@Named("carID") String carID) {
    // 設定5/100的機率下，會去清除過期的資料
    Random random = new Random(System.currentTimeMillis());
    if (random.nextDouble() < 1)
      CleanOutOfDate();

    CarReg cr = cr_service.getDataByID(carID);
    // 該carID已註冊
    if (null != cr) {
      log.info("該carID已註冊");
      String filter = String.format("carID=='%s'", carID);
      List<CarPhoneRandomNumber> list = cprn_service.getPaginationData(filter, "deadTime desc").getResultList();
      if (null != list && list.size() > 0) {
        log.info("該CarPhoneRandomNumber尚未到期");
        return list.get(0);
      } else {
        // 先檢查是否有相同的關結
        boolean notOnly = true;
        CarPhoneRandomNumber cprn = null;
        int count = 0;
        do {
          cprn = new CarPhoneRandomNumber();
          cprn.setCarID(carID);
          filter = String.format("randomID=='%s'", cprn.getRandomID());
          list = cprn_service.getPaginationData(filter, "deadTime desc").getResultList();
          if (null == list || list.size() == 0) {
            cprn_service.add(cprn);
            log.info("產生新的CarPhoneRandomNumber:" + JSON.toJSONString(cprn));
            notOnly = false;
          }
        } while (notOnly && (count++) <= 3);// 最多只試三次
        return cprn;
      }
    } else {
      throw new Error("尚未註冊");
    }
  }

  @ApiMethod(name = "cprandom.match", httpMethod = HttpMethod.POST)
  public CarPhoneRelation postCPRNMatch(@Named("phoneID") String phoneID, @Named("randomID") String randomID) {
    // 設定5/100的機率下，會去清除過期的資料
    Random random = new Random(System.currentTimeMillis());
    if (random.nextDouble() < 0.05)
      CleanOutOfDate();

    PhoneReg pr = pr_service.getDataByID(phoneID);
    // 該phoneID已註冊
    if (null != pr) {
      log.info("該phoneID已註冊:" + phoneID);
      log.info("使用randomID進行連結:" + randomID);
      String filter = String.format("randomID=='%s'", randomID);
      List<CarPhoneRandomNumber> list = cprn_service.getPaginationData(filter, "deadTime desc").getResultList();
      if (null != list && list.size() > 0) {
        log.info("找到配對的randomID:" + randomID);
        CarPhoneRandomNumber cprn = list.get(0);
        String carID = cprn.getCarID();

        // 清除使用亂數配對的記錄
        cprn_service.delete(cprn.getKey());

        filter = String.format("phoneID=='%s' && carID=='%s'", phoneID, carID);
        List<CarPhoneRelation> cpr_list = cpr_service.getPaginationData(filter).getResultList();
        CarPhoneRelation cpr = null;
        if (null != cpr_list && cpr_list.size() > 1) {
          throw new Error("連結資料異常:carID:" + carID + ", phoneID=" + phoneID);
        }

        // 曾經配對成功過
        if (null != cpr_list && cpr_list.size() == 1) {
          // 更新記錄
          cpr = cpr_list.get(0);
          cpr.setModTime(CTCommon.getNowTime());
          cpr_service.modify(cpr);
        }
        // 未曾配對成功過
        else {
          // 新增記錄
          cpr = new CarPhoneRelation();
          cpr.setCarID(carID);
          cpr.setPhoneID(phoneID);
          cpr.setAddTime(CTCommon.getNowTime());
          cpr_service.add(cpr);
        }
        return cpr;
      } else {
        throw new Error("找不到配對的randomID:" + randomID);
      }
    } else {
      throw new Error("尚未註冊");
    }
  }

  private void CleanOutOfDate() {
    log.info("試著清除過期的資料");
    int count = 1;
    String dateStr = CTCommon.getNowTime();
    String filter = String.format("deadTime<='%s'", dateStr);
    do {
      List<CarPhoneRandomNumber> list = cprn_service.getPaginationData(filter).getResultList();
      count = list.size();
      for (CarPhoneRandomNumber o : list) {
        cprn_service.delete(o.getKey());
      }
    } while (count > 0);

  }
}
