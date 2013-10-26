package org.luke.ct.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable(detachable = "true")
public class PushNotificationMessage {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
  private Key key;
  @Persistent
  private String encodedKey;
  @Persistent
  private String type;
  @Persistent
  private String message;
  @Persistent
  private String addTime;

  public PushNotificationMessage() {
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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getAddTime() {
    return addTime;
  }

  public void setAddTime(String addTime) {
    this.addTime = addTime;
  }

}
