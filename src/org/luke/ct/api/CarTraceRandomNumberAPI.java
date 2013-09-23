package org.luke.ct.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.inject.Named;

import org.luke.ct.dao.CarPhoneRandomNumberService;
import org.luke.ct.dao.CarPhoneRandomNumberServiceImpl;
import org.luke.ct.dao.CarRegService;
import org.luke.ct.dao.CarRegServiceImpl;
import org.luke.ct.dao.PhoneRegService;
import org.luke.ct.dao.PhoneRegServiceImpl;
import org.luke.ct.model.CarPhoneRandomNumber;
import org.luke.ct.model.CarReg;

import com.alibaba.fastjson.JSON;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(name = "ctrandom", version = "v1")
public class CarTraceRandomNumberAPI {
  private static CarRegService cr_service = new CarRegServiceImpl();
  private static PhoneRegService pr_service = new PhoneRegServiceImpl();
  private static CarPhoneRandomNumberService cprn_service = new CarPhoneRandomNumberServiceImpl();
  private static final Logger log = Logger.getLogger(CarTraceRandomNumberAPI.class.getName());

  @ApiMethod(name = "cprandom.list", httpMethod = HttpMethod.GET)
  public List<CarPhoneRandomNumber> getCarPhoneRandomNumberList() {
    return cprn_service.getPaginationData(null, "deadTime desc").getResultList();
  }

  @ApiMethod(name = "cprandom.merge", httpMethod = HttpMethod.GET)
  public CarPhoneRandomNumber getCarPhoneRandomNumber(@Named("id") String carID) {
    // 設定5/100的機率下，會去清除過期的資料
    Random random = new Random(System.currentTimeMillis());
    if (random.nextDouble() < 0.05)
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
        CarPhoneRandomNumber cprn = new CarPhoneRandomNumber();
        cprn.setCarID(carID);
        cprn_service.add(cprn);
        log.info("產生新的CarPhoneRandomNumber:" + JSON.toJSONString(cprn));
        return cprn;
      }
    } else {
      throw new Error("尚未註冊");
    }
  }

  private void CleanOutOfDate() {
    log.info("試著清除過期的資料");
    int count = 1;
    String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
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
