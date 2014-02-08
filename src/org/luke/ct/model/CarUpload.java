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
public class CarUpload implements BaseEntity {
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
  private Key key;
  @Persistent
  private String encodedKey;
  @Persistent
  private String carID;
  @Persistent
  private String blob_key;
  @Persistent
  private String serving_url;
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

  public String getEncodedKey() {
    return encodedKey;
  }

  @Override
  public void setEncodedKey() {
    this.encodedKey = KeyFactory.keyToString(key);
  }

  public String getCarID() {
    return carID;
  }

  public void setCarID(String carID) {
    this.carID = carID;
  }

  public String getBlob_key() {
    return blob_key;
  }

  public void setBlob_key(String blob_key) {
    this.blob_key = blob_key;
  }

  public String getServing_url() {
    return serving_url;
  }

  public void setServing_url(String serving_url) {
    this.serving_url = serving_url;
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
