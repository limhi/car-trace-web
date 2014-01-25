package org.luke.ct.model;

import java.util.Calendar;
import java.util.Random;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

import org.apache.commons.lang3.StringUtils;
import org.luke.ct.core.CTCommon;

@PersistenceCapable(detachable = "true")
public class CarPhoneRandomNumber extends BaseEntity {
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
    randomID = StringUtils.leftPad(randomID, 6, "0");

    deadTime = CTCommon.getDeadTime(Calendar.MINUTE, 5);
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
