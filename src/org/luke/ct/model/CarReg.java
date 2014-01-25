package org.luke.ct.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable = "true")
public class CarReg extends BaseEntity {
  @Persistent
  private String deviceID;
  @Persistent
  private String deviceIP;
  @Persistent
  private String appVersion;
  @Persistent
  private String senderID;
  @Persistent
  private String registerID;

  public CarReg() {
  }

  public String getDeviceID() {
    return deviceID;
  }

  public void setDeviceID(String deviceID) {
    this.deviceID = deviceID;
  }

  public String getDeviceIP() {
    return deviceIP;
  }

  public void setDeviceIP(String deviceIP) {
    this.deviceIP = deviceIP;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public String getSenderID() {
    return senderID;
  }

  public void setSenderID(String senderID) {
    this.senderID = senderID;
  }

  public String getRegisterID() {
    return registerID;
  }

  public void setRegisterID(String registerID) {
    this.registerID = registerID;
  }
}
