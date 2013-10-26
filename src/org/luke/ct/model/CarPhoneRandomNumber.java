package org.luke.ct.model;

import java.util.Calendar;
import java.util.Random;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang3.StringUtils;
import org.luke.ct.core.CTCommon;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable = "true")
public class CarPhoneRandomNumber {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
  private Key key;
  @Persistent
  private String encodedKey;
  @Persistent
  private String carID;
  @Persistent
  private String randomID;
  @Persistent
  private String deadTime;


  public CarPhoneRandomNumber() {
    Random rand = new Random(System.currentTimeMillis());
    Long randLong = (long) (rand.nextDouble() * 100000000);
    randomID = "" + randLong;
    randomID = StringUtils.leftPad(randomID, 8, "0");

    deadTime = CTCommon.getDeadTime(Calendar.MINUTE, 5);
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

  public String getRandomID() {
    return randomID;
  }

  public String getDeadTime() {
    return deadTime;
  }
}
