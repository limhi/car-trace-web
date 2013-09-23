package org.luke.ct.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.luke.ct.core.QueryResult;
import org.luke.ct.dao.CarRegService;
import org.luke.ct.dao.CarRegServiceImpl;
import org.luke.ct.dao.PhoneRegService;
import org.luke.ct.dao.PhoneRegServiceImpl;
import org.luke.ct.model.CarReg;
import org.luke.ct.model.PhoneReg;

import com.alibaba.fastjson.JSON;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(name = "ctreg", version = "v1")
public class CarTraceRegisterAPI {
  private static CarRegService cr_service = new CarRegServiceImpl();
  private static PhoneRegService pr_service = new PhoneRegServiceImpl();
  private static final Logger log = Logger.getLogger(CarTraceRegisterAPI.class.getName());

  @ApiMethod(name = "creg", httpMethod = HttpMethod.GET)
  public CarReg getCarRegister(@Named("id") String id) {
    List<CarReg> list = cr_service.getPaginationData().getResultList();
    int i = 0;
    log.info("count=" + list.size());
    for (CarReg o : list) {
      log.info("index:" + (++i) + ", CarReg=" + JSON.toJSONString(o));
    }
    CarReg reg = cr_service.getDataByID(id);
    return reg;
  }

  @ApiMethod(name = "creg.list", httpMethod = HttpMethod.GET)
  public List<CarReg> getCarRegisterList() {
    return cr_service.getPaginationData().getResultList();
  }

  @ApiMethod(name = "creg.merge", httpMethod = HttpMethod.POST)
  public CarReg postCarRegisterMerge(CarReg cr) {
    CarReg retCR = null;
    // check deviceID, appVersion
    if (StringUtils.isNotBlank(cr.getDeviceID()) && StringUtils.isNotBlank(cr.getAppVersion())) {
      String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
      String filter = String.format("deviceID=='%s' && appVersion=='%s'", cr.getDeviceID(), cr.getAppVersion());
      QueryResult<CarReg> qr = cr_service.getPaginationData(filter);
      // 第一次註冊，新增資料
      if (qr.getTotalRecord() == 0) {
        retCR = new CarReg();
        retCR.setAddTime(dateStr);
        retCR.setAppVersion(cr.getAppVersion());
        retCR.setDeviceID(cr.getDeviceID());
        retCR.setModTime(dateStr);
        retCR.setRegisterID(cr.getRegisterID());
        retCR.setSenderID(cr.getSenderID());
        cr_service.add(retCR);
        log.info("新增一筆設備註冊記錄：" + JSON.toJSONString(retCR));
      } else if (qr.getTotalRecord() == 1) {
        retCR = qr.getResultList().get(0);
        retCR.setAppVersion(cr.getAppVersion());
        retCR.setDeviceID(cr.getDeviceID());
        retCR.setModTime(dateStr);
        retCR.setRegisterID(cr.getRegisterID());
        retCR.setSenderID(cr.getSenderID());
        cr_service.modify(retCR);
        log.info("修改一筆設備註冊記錄：" + JSON.toJSONString(retCR));
      } else {
        log.severe("設備記錄數目異常，不處理");
      }
    }
    return retCR;
  }

  @ApiMethod(name = "preg", httpMethod = HttpMethod.GET)
  public PhoneReg getPhoneRegister(@Named("id") String id) {
    List<PhoneReg> list = pr_service.getPaginationData().getResultList();
    int i = 0;
    log.info("count=" + list.size());
    for (PhoneReg o : list) {
      log.info("index:" + (++i) + ", PhoneReg=" + JSON.toJSONString(o));
    }
    PhoneReg reg = pr_service.getDataByID(id);
    return reg;
  }

  @ApiMethod(name = "preg.list", httpMethod = HttpMethod.GET)
  public List<PhoneReg> getPhoneRegisterList() {
    return pr_service.getPaginationData().getResultList();
  }

  @ApiMethod(name = "preg.merge", httpMethod = HttpMethod.POST)
  public PhoneReg postPhoneRegisterMerge(PhoneReg cr) {
    PhoneReg retPR = null;
    // check deviceID, appVersion
    if (StringUtils.isNotBlank(cr.getDeviceID()) && StringUtils.isNotBlank(cr.getAppVersion())) {
      String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime());
      String filter = String.format("deviceID=='%s' && appVersion=='%s'", cr.getDeviceID(), cr.getAppVersion());
      QueryResult<PhoneReg> qr = pr_service.getPaginationData(filter);
      // 第一次註冊，新增資料
      if (qr.getTotalRecord() == 0) {
        retPR = new PhoneReg();
        retPR.setAddTime(dateStr);
        retPR.setAppVersion(cr.getAppVersion());
        retPR.setDeviceID(cr.getDeviceID());
        retPR.setModTime(dateStr);
        retPR.setRegisterID(cr.getRegisterID());
        retPR.setSenderID(cr.getSenderID());
        pr_service.add(retPR);
        log.info("新增一筆手機註冊記錄：" + JSON.toJSONString(retPR));
      } else if (qr.getTotalRecord() == 1) {
        retPR = qr.getResultList().get(0);
        retPR.setAppVersion(cr.getAppVersion());
        retPR.setDeviceID(cr.getDeviceID());
        retPR.setModTime(dateStr);
        retPR.setRegisterID(cr.getRegisterID());
        retPR.setSenderID(cr.getSenderID());
        pr_service.modify(retPR);
        log.info("修改一筆手機註冊記錄：" + JSON.toJSONString(retPR));
      } else {
        log.severe("手機記錄數目異常，不處理");
      }
    }
    return retPR;
  }
}
