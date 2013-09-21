package org.luke.ct.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable = "true")
public class CarReg {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;
  @Persistent
  private String deviceID;
  @Persistent
  private String appVersion;
  @Persistent
  private String senderID;
  @Persistent
  private String registerID;
  @Persistent
  private String addTime;
  @Persistent
  private String modTime;

  public Key getKey() {
    return key;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  public String getDeviceID() {
    return deviceID;
  }

  public void setDeviceID(String deviceID) {
    this.deviceID = deviceID;
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

  public String getAddTime() {
    return addTime;
  }

  public void setAddTime(String addTime) {
    this.addTime = addTime;
  }

  public String getModTime() {
    return modTime;
  }

  public void setModTime(String modTime) {
    this.modTime = modTime;
  }
}
