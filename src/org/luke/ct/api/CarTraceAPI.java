package org.luke.ct.api;

import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import org.luke.ct.model.CarReg;
import org.luke.ct.model.CarRegService;
import org.luke.ct.model.CarRegServiceImpl;

import com.alibaba.fastjson.JSON;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;

@Api(name = "ct", version = "v1")
public class CarTraceAPI {
  private static CarRegService cr_service = new CarRegServiceImpl();
  private static final Logger log = Logger.getLogger(CarTraceAPI.class.getName());

  @ApiMethod(name = "reg", httpMethod = HttpMethod.GET)
  public CarReg getReg(@Named("id") String id) {
    List<CarReg> list = cr_service.getPaginationData().getResultList();
    int i = 0;
    log.info("count=" + list.size());
    for (CarReg o : list) {
      log.info("index:" + (++i) + ", CarReg=" + JSON.toJSONString(o));
    }    
    CarReg reg = cr_service.getDataByID(id);
    return reg;
  }
}
