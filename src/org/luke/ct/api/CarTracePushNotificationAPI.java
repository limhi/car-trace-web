package org.luke.ct.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.luke.ct.core.CTCommon;
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

import com.alibaba.fastjson.JSON;
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
	private static final Logger log = Logger
			.getLogger(CarTracePushNotificationAPI.class.getName());

	@ApiMethod(name = "cppn.list", httpMethod = HttpMethod.GET)
	public List<CarPhonePushNotification> getCarPhonePushNotificationList() {
		return cppn_service.getPaginationData(null, "addTime desc")
				.getResultList();
	}

	@ApiMethod(name = "pcpn.list", httpMethod = HttpMethod.GET)
	public List<PhoneCarPushNotification> getPhoneCarPushNotificationList() {
		return pcpn_service.getPaginationData(null, "addTime desc")
				.getResultList();
	}

	@ApiMethod(name = "pnm.list", httpMethod = HttpMethod.GET)
	public List<PushNotificationMessage> getPushNotificationMessageList() {
		return pnm_service.getPaginationData(null, "addTime desc")
				.getResultList();
	}

	@ApiMethod(name = "cppn.merge", httpMethod = HttpMethod.POST)
	public JSONObject postCPPushNotification(@Named("carID") String carID,
			JSONObject json) {
		JSONObject retJson = null;
		// 檢查carID是否合法
		CarReg cr = cr_service.getDataByID(carID);
		if (null == cr)
			throw new Error("該carID尚未註冊");

		// 找出和該carID連結的phoneID list
		String filter = String.format("carID=='%s'", carID);
		List<CarPhoneRelation> cpr_list = cpr_service.getPaginationData(filter)
				.getResultList();
		if (null == cpr_list || cpr_list.size() == 0)
			throw new Error("該carID尚未有配對的phone");

		// 檢查push notification 是否有 type 和 message
		if (null == json || null == json.getString("type")
				|| null == json.getString("message"))
			throw new Error("提供的訊息內容格式不正確");

		String type = json.getString("type");
		String message = json.getString("message");
		if (StringUtils.isBlank(type) || StringUtils.isBlank(message))
			throw new Error("提供的訊息內容中，type或message錯誤");
		// 正確的儲存push notification
		PushNotificationMessage pnm = new PushNotificationMessage();
		pnm.setAddTime(CTCommon.getNowTime());
		pnm.setType(type);
		pnm.setMessage(message);
		pnm_service.add(pnm);

		retJson = new JSONObject();
		retJson.put("messageID", pnm.getEncodedKey());
		for (CarPhoneRelation o : cpr_list) {
			CarPhonePushNotification cppn = new CarPhonePushNotification();
			cppn.setAddTime(CTCommon.getNowTime());
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
	public JSONObject postPCPushNotification(@Named("phoneID") String phoneID,
			JSONObject json) {
		JSONObject retJson = null;
		// 檢查phoneID是否合法
		PhoneReg pr = pr_service.getDataByID(phoneID);
		if (null == pr)
			throw new Error("該phoneID尚未註冊");

		// 找出和該phoneID連結的carID list
		String filter = String.format("phoneID=='%s'", phoneID);
		List<CarPhoneRelation> cpr_list = cpr_service.getPaginationData(filter)
				.getResultList();
		if (null == cpr_list || cpr_list.size() == 0)
			throw new Error("該phoneID尚未有配對的phone");

		// 檢查push notification 是否有 type 和 message
		if (null == json || null == json.getString("type")
				|| null == json.getString("message"))
			throw new Error("提供的訊息內容格式不正確");

		String type = json.getString("type");
		String message = json.getString("message");
		if (StringUtils.isBlank(type) || StringUtils.isBlank(message))
			throw new Error("提供的訊息內容中，type或message錯誤");
		// 正確的儲存push notification
		PushNotificationMessage pnm = new PushNotificationMessage();
		pnm.setAddTime(CTCommon.getNowTime());
		pnm.setType(type);
		pnm.setMessage(message);
		pnm_service.add(pnm);

		retJson = new JSONObject();
		retJson.put("messageID", pnm.getEncodedKey());
		for (CarPhoneRelation o : cpr_list) {
			PhoneCarPushNotification pcpn = new PhoneCarPushNotification();
			pcpn.setAddTime(CTCommon.getNowTime());
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

	@ApiMethod(name = "pcpn.send", httpMethod = HttpMethod.POST)
	public JSONObject postPCPushNotificationSend(
			@Named("phoneID") String phoneID,
			@Named("messageID") String messageID) {
		JSONObject retJson = null;
		// 檢查phoneID是否合法
		PhoneReg pr = pr_service.getDataByID(phoneID);
		if (null == pr)
			throw new Error("該phoneID尚未註冊");

		// 找出和該phoneID連結的message
		String filter = String.format("phoneID=='%s' && messageID=='%s'",
				phoneID, messageID);
		List<PhoneCarPushNotification> pcpn_list = pcpn_service
				.getPaginationData(filter).getResultList();

		if (null == pcpn_list || pcpn_list.size() == 0)
			throw new Error("該messageID無法與phoneID對應");

		PushNotificationMessage pnm = pnm_service.getDataByID(messageID);
		if (null == pnm)
			throw new Error("該messageID不合法");

		// 產生carRegIDList
		List<String> carRegIDList = new ArrayList<String>();
		for (PhoneCarPushNotification o : pcpn_list) {
			// 判斷是否傳送過
			if (o.getIsSend()) {
				CarReg cr = cr_service.getDataByID(o.getCarID());
				carRegIDList.add(cr.getRegisterID());
				o.setIsSend(true);
			}
		}

		if (carRegIDList.size() == 0) {
			throw new Error("該phoneID的所有的message都已傳送過");
		}

		// 取得message的內容
		String type = pnm.getType();
		String message = pnm.getMessage();

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpParams myParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myParams, 8000);
		HttpConnectionParams.setSoTimeout(myParams, 8000);

		JSONObject obj = new JSONObject();
		obj.put("collapse_key", "send_gps");
		obj.put("time_to_live", 108);
		obj.put("delay_while_idle", true);

		JSONObject tempObj = new JSONObject();
		tempObj.put("title", "test title");
		tempObj.put("type", type);
		tempObj.put("message", message);
		obj.put("data", tempObj);

		JSONArray tempArray = new JSONArray();
		for (String o : carRegIDList) {
			// tempArray.add("APA91bHeYhgI7-zfNI-X47bqwHsRESL5iimgyDpirRPKGQ-MmuMD0wnvQo6ZKyTgmD2Fh0Qlvre0czu3ewJkhvvm99sW2TO3lXaxfCaXMnz9PEMNxc8egT69uHQGXXr0pdEdrCyJ1v9oqSWVv5gMdiJMijEy0VdZlg");
			tempArray.add(o);
		}
		obj.put("registration_ids", tempArray);
		/*
		 * { "collapse_key": "send_gps", "time_to_live": 108,
		 * "delay_while_idle": true, "data": { "title": "test", "message":
		 * "msgmsg" }, "registration_ids": [
		 * "APA91bHeYhgI7-zfNI-X47bqwHsRESL5iimgyDpirRPKGQ-MmuMD0wnvQo6ZKyTgmD2Fh0Qlvre0czu3ewJkhvvm99sW2TO3lXaxfCaXMnz9PEMNxc8egT69uHQGXXr0pdEdrCyJ1v9oqSWVv5gMdiJMijEy0VdZlg"
		 * ] }
		 */
		try {
			HttpPost httppost = new HttpPost(CTCommon.GCM_URL);
			httppost.setHeader("Content-Type", "application/json");
			httppost.setHeader("Authorization", "key=" + CTCommon.API_KEY);

			StringEntity se = new StringEntity(obj.toString());
			httppost.setEntity(se);

			HttpResponse response = httpclient.execute(httppost);
			String temp = EntityUtils.toString(response.getEntity());
			// System.out.println(temp);
			retJson = JSON.parseObject(temp);
		} catch (ClientProtocolException e) {
			// e.printStackTrace();
			log.severe(e.getMessage());
		} catch (IOException e) {
			// e.printStackTrace();
			log.severe(e.getMessage());
		}
		return retJson;
	}

	@ApiMethod(name = "cppn.send", httpMethod = HttpMethod.POST)
	public JSONObject postCPPushNotificationSend(@Named("carID") String carID,
			@Named("messageID") String messageID) {
		JSONObject retJson = null;
		// 檢查carID是否合法
		CarReg cr = cr_service.getDataByID(carID);
		if (null == cr)
			throw new Error("該carID尚未註冊");

		// 找出和該carID連結的message
		String filter = String.format("carID=='%s' && messageID=='%s'", carID,
				messageID);
		List<CarPhonePushNotification> cppn_list = cppn_service
				.getPaginationData(filter).getResultList();
		if (null == cppn_list || cppn_list.size() == 0)
			throw new Error("該messageID無法與carID對應");

		PushNotificationMessage pnm = pnm_service.getDataByID(messageID);
		if (null == pnm)
			throw new Error("該messageID不合法");

		// 產生phoneRegIDList
		List<String> phoneRegIDList = new ArrayList<String>();
		for (CarPhonePushNotification o : cppn_list) {
			// 判斷是否傳送過
			if (o.getIsSend()) {
				PhoneReg pr = pr_service.getDataByID(o.getCarID());
				phoneRegIDList.add(pr.getRegisterID());
				o.setIsSend(true);
			}
		}

		if (phoneRegIDList.size() == 0) {
			throw new Error("該carID的所有的message都已傳送過");
		}

		// 取得message的內容
		String type = pnm.getType();
		String message = pnm.getMessage();

		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpParams myParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(myParams, 8000);
		HttpConnectionParams.setSoTimeout(myParams, 8000);

		JSONObject obj = new JSONObject();
		obj.put("collapse_key", "send_gps");
		obj.put("time_to_live", 108);
		obj.put("delay_while_idle", true);

		JSONObject tempObj = new JSONObject();
		tempObj.put("title", "test title");
		tempObj.put("type", type);
		tempObj.put("message", message);
		obj.put("data", tempObj);

		JSONArray tempArray = new JSONArray();
		for (String o : phoneRegIDList) {
			// tempArray.add("APA91bHeYhgI7-zfNI-X47bqwHsRESL5iimgyDpirRPKGQ-MmuMD0wnvQo6ZKyTgmD2Fh0Qlvre0czu3ewJkhvvm99sW2TO3lXaxfCaXMnz9PEMNxc8egT69uHQGXXr0pdEdrCyJ1v9oqSWVv5gMdiJMijEy0VdZlg");
			tempArray.add(o);
		}
		obj.put("registration_ids", tempArray);
		/*
		 * { "collapse_key": "send_gps", "time_to_live": 108,
		 * "delay_while_idle": true, "data": { "title": "test", "message":
		 * "msgmsg" }, "registration_ids": [
		 * "APA91bHeYhgI7-zfNI-X47bqwHsRESL5iimgyDpirRPKGQ-MmuMD0wnvQo6ZKyTgmD2Fh0Qlvre0czu3ewJkhvvm99sW2TO3lXaxfCaXMnz9PEMNxc8egT69uHQGXXr0pdEdrCyJ1v9oqSWVv5gMdiJMijEy0VdZlg"
		 * ] }
		 */
		try {
			HttpPost httppost = new HttpPost(CTCommon.GCM_URL);
			httppost.setHeader("Content-Type", "application/json");
			httppost.setHeader("Authorization", "key=" + CTCommon.API_KEY);

			StringEntity se = new StringEntity(obj.toString());
			httppost.setEntity(se);

			HttpResponse response = httpclient.execute(httppost);
			String temp = EntityUtils.toString(response.getEntity());
			// System.out.println(temp);
			retJson = JSON.parseObject(temp);
		} catch (ClientProtocolException e) {
			// e.printStackTrace();
			log.severe(e.getMessage());
		} catch (IOException e) {
			// e.printStackTrace();
			log.severe(e.getMessage());
		}
		return retJson;
	}
}