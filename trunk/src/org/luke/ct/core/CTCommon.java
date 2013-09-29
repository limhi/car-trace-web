package org.luke.ct.core;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class CTCommon {
  public static String getNowTime() {
    Calendar nowTime = Calendar.getInstance();
    nowTime.add(Calendar.HOUR_OF_DAY, 8);
    return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(nowTime.getTime());
  }

  public static String getDeadTime(int unit, int amount) {
    Calendar nowTime = Calendar.getInstance();
    nowTime.add(unit, amount);

    nowTime.add(Calendar.HOUR_OF_DAY, 8);
    return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(nowTime.getTime());
  }
}
