package org.luke.ct.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable = "true")
public class PhoneCarPushNotification extends BaseEntity {
  @Persistent
  private String carID;
  @Persistent
  private String phoneID;
  @Persistent
  private String messageID;
  @Persistent
  private Boolean isSend = false;
  @Persistent
  private String sendTime;

  public PhoneCarPushNotification() {
  }

  public String getCarID() {
    return carID;
  }

  public void setCarID(String carID) {
    this.carID = carID;
  }

  public String getPhoneID() {
    return phoneID;
  }

  public void setPhoneID(String phoneID) {
    this.phoneID = phoneID;
  }

  public String getMessageID() {
    return messageID;
  }

  public void setMessageID(String messageID) {
    this.messageID = messageID;
  }

  public Boolean getIsSend() {
    return isSend;
  }

  public void setIsSend(Boolean isSend) {
    this.isSend = isSend;
  }

  public String getSendTime() {
    return sendTime;
  }

  public void setSendTime(String sendTime) {
    this.sendTime = sendTime;
  }
}
