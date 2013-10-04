package org.luke.ct.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.luke.ct.core.CTCommon;

import com.alibaba.fastjson.JSONObject;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(name = "cttest", version = "v1")
public class JustTest {
  private static final Logger log = Logger.getLogger(JustTest.class.getName());

  @ApiMethod(name = "t01", httpMethod = HttpMethod.GET)
  public JSONObject getCarPhoneRandomNumberList() {
    JSONObject retObj = new JSONObject();
    retObj.put("message", "test msg");

    List<String> devices = new ArrayList<String>();
    devices
        .add("APA91bHeYhgI7-zfNI-X47bqwHsRESL5iimgyDpirRPKGQ-MmuMD0wnvQo6ZKyTgmD2Fh0Qlvre0czu3ewJkhvvm99sW2TO3lXaxfCaXMnz9PEMNxc8egT69uHQGXXr0pdEdrCyJ1v9oqSWVv5gMdiJMijEy0VdZlg");

    MulticastResult result = sendMessageToDevice(CTCommon.API_KEY, devices, "test title", "test msg");
    log.severe("result = " + result.toString());
    return retObj;
  }

  private MulticastResult sendMessageToDevice(String apiKey, List<String> devices, String title, String message) {
    Sender sender = new Sender(apiKey);
    Message gcmMessage = new Message.Builder().addData("title", title).addData("message", message).build();
    MulticastResult result = null;
    try {
      result = sender.send(gcmMessage, devices, 3);
    } catch (IOException e) {
      //e.printStackTrace();
      log.severe(e.getMessage());
    }
    return result;
  }
}
