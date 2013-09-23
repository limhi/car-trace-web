package org.luke.ct.model;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang3.StringUtils;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiSerializationProperty;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable = "true")
public class CarPhoneRandomNumber {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @ApiSerializationProperty(ignored = AnnotationBoolean.TRUE)
  private Key key;
  @Persistent
  private String encodedKey;
  @Persistent
  private String carID;
  @Persistent
  private String phoneID;
  @Persistent
  private String randomID;
  @Persistent
  private String deadTime;

  public CarPhoneRandomNumber() {
    // 產生隨機數
    Random rand = new Random(System.currentTimeMillis());
    Long randLong = (long) (rand.nextDouble() * 100000000);
    randomID = "" + randLong;
    randomID = StringUtils.leftPad(randomID, 8, "0");
    
    // 設定過期時間為5分鐘
    Calendar deadCal = Calendar.getInstance();
    deadCal.add(Calendar.MINUTE, 5);
    deadTime = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(deadCal.getTime());
  }

  public Key getKey() {
    return key;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  public String getEncodedKey() {
    return KeyFactory.keyToString(key);
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

  public String getRandomID() {
    return randomID;
  }

  public String getDeadTime() {
    return deadTime;
  }
}
