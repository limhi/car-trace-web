package org.luke.ct.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(detachable = "true")
public class CarPhoneRelation extends BaseEntity {
  @Persistent
  private String carID;
  @Persistent
  private String phoneID;

  public CarPhoneRelation() {
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

}
