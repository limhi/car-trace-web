package org.luke.ct.api;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.luke.ct.dao.CarPhonePushNotificationService;
import org.luke.ct.dao.CarPhonePushNotificationServiceImpl;
import org.luke.ct.dao.CarPhoneRelationService;
import org.luke.ct.dao.CarPhoneRelationServiceImpl;
import org.luke.ct.dao.CarRegService;
import org.luke.ct.dao.CarRegServiceImpl;
import org.luke.ct.dao.PhoneCarPushNotificationService;
import org.luke.ct.dao.PhoneCarPushNotificationServiceImpl;
import org.luke.ct.dao.PhoneRegService;
import org.luke.ct.dao.PhoneRegServiceImpl;
import org.luke.ct.dao.PushNotificationMessageService;
import org.luke.ct.dao.PushNotificationMessageServiceImpl;
import org.luke.ct.model.CarPhonePushNotification;
import org.luke.ct.model.CarPhoneRelation;
import org.luke.ct.model.CarReg;
import org.luke.ct.model.PhoneCarPushNotification;
import org.luke.ct.model.PhoneReg;
import org.luke.ct.model.PushNotificationMessage;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(name = "ctpush", version = "v1")
public class CarTracePushNotificationAPI {
  private static CarRegService cr_service = new CarRegServiceImpl();
  private static PhoneRegService pr_service = new PhoneRegServiceImpl();
  private static CarPhoneRelationService cpr_service = new CarPhoneRelationServiceImpl();
  private static CarPhonePushNotificationService cppn_service = new CarPhonePushNotificationServiceImpl();
  private static PhoneCarPushNotificationService pcpn_service = new PhoneCarPushNotificationServiceImpl();
  private static PushNotificationMessageService pnm_service = new PushNotificationMessageServiceImpl();
  private static final Logger log = Logger.getLogger(CarTracePushNotificationAPI.class.getName());

  @ApiMethod(name = "cppn.list", httpMethod = HttpMethod.GET)
  public List<CarPhonePushNotification> getCarPhonePushNotificationList() {
    return cppn_service.getPaginationData(null, "addTime desc").getResultList();
  }

  @ApiMethod(name = "pcpn.list", httpMethod = HttpMethod.GET)
  public List<PhoneCarPushNotification> getPhoneCarPushNotificationList() {
    return pcpn_service.getPaginationData(null, "addTime desc").getResultList();
  }

  @ApiMethod(name = "pnm.list", httpMethod = HttpMethod.GET)
  public List<PushNotificationMessage> getPushNotificationMessageList() {
    return pnm_service.getPaginationData(null, "addTime desc").getResultList();
  }

  @ApiMethod(name = "cppn.merge", httpMethod = HttpMethod.POST)
  public JSONObject postCPPushNotification(@Named("carID") String carID, JSONObject json) {
    JSONObject retJson = null;
    // 檢查carID是否合法
    CarReg cr = cr_service.getDataByID(carID);
    if (null == cr)
      throw new Error("該carID尚未註冊");

    // 找出和該carID連結的phoneID list
    String filter = String.format("carID=='%s'", carID);
    List<CarPhoneRelation> cpr_list = cpr_service.getPaginationData(filter).getResultList();
    if (null == cpr_list || cpr_list.size() == 0)
      throw new Error("該carID尚未有配對的phone");

    // 檢查push notification 是否有 type 和 message
    if (null == json || null == json.getString("type") || null == json.getString("message"))
      throw new Error("提供的訊息內容格式不正確");

    String type = json.getString("type");
    String message = json.getString("message");
    if (StringUtils.isBlank(type) || StringUtils.isBlank(message))
      throw new Error("提供的訊息內容中，type或message錯誤");
    // 正確的儲存push notification
    PushNotificationMessage pnm = new PushNotificationMessage();
    pnm.setAddTime(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
    pnm.setType(type);
    pnm.setMessage(message);
    pnm_service.add(pnm);

    retJson = new JSONObject();
    retJson.put("messageID", pnm.getEncodedKey());
    for (CarPhoneRelation o : cpr_list) {
      CarPhonePushNotification cppn = new CarPhonePushNotification();
      cppn.setAddTime(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
      cppn.setCarID(carID);
      cppn.setPhoneID(o.getPhoneID());
      cppn.setIsSend(false);
      cppn.setMessageID(pnm.getEncodedKey());
      cppn_service.add(cppn);

      JSONArray phoneArray = retJson.getJSONArray("phoneArray");
      if (null == phoneArray)
        phoneArray = new JSONArray();

      phoneArray.add(o.getPhoneID());
      retJson.put("phoneArray", phoneArray);
    }

    return retJson;
  }
  
  @ApiMethod(name = "pcpn.merge", httpMethod = HttpMethod.POST)
  public JSONObject postPCPushNotification(@Named("phoneID") String phoneID, JSONObject json) {
    JSONObject retJson = null;
    // 檢查phoneID是否合法
    PhoneReg pr = pr_service.getDataByID(phoneID);
    if (null == pr)
      throw new Error("該phoneID尚未註冊");

    // 找出和該phoneID連結的carID list
    String filter = String.format("phoneID=='%s'", phoneID);
    List<CarPhoneRelation> cpr_list = cpr_service.getPaginationData(filter).getResultList();
    if (null == cpr_list || cpr_list.size() == 0)
      throw new Error("該phoneID尚未有配對的phone");

    // 檢查push notification 是否有 type 和 message
    if (null == json || null == json.getString("type") || null == json.getString("message"))
      throw new Error("提供的訊息內容格式不正確");

    String type = json.getString("type");
    String message = json.getString("message");
    if (StringUtils.isBlank(type) || StringUtils.isBlank(message))
      throw new Error("提供的訊息內容中，type或message錯誤");
    // 正確的儲存push notification
    PushNotificationMessage pnm = new PushNotificationMessage();
    pnm.setAddTime(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
    pnm.setType(type);
    pnm.setMessage(message);
    pnm_service.add(pnm);

    retJson = new JSONObject();
    retJson.put("messageID", pnm.getEncodedKey());
    for (CarPhoneRelation o : cpr_list) {
      PhoneCarPushNotification pcpn = new PhoneCarPushNotification();
      pcpn.setAddTime(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(Calendar.getInstance().getTime()));
      pcpn.setCarID(o.getCarID());
      pcpn.setPhoneID(phoneID);
      pcpn.setIsSend(false);
      pcpn.setMessageID(pnm.getEncodedKey());
      pcpn_service.add(pcpn);

      JSONArray carArray = retJson.getJSONArray("carArray");
      if (null == carArray)
        carArray = new JSONArray();

      carArray.add(o.getCarID());
      retJson.put("carArray", carArray);
    }

    return retJson;
  }

}
