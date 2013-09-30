package org.luke.ct.test;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.luke.ct.dao.CarRegService;
import org.luke.ct.dao.CarRegServiceImpl;
import org.luke.ct.model.CarReg;

import com.alibaba.fastjson.JSON;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class CarTraceServlet extends HttpServlet {
  private static final Logger log = Logger.getLogger(CarTraceServlet.class.getName());

  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    resp.setContentType("text/plain");
    resp.getWriter().println("Hello, world");
    // PersistenceManager pm = PMF.get().getPersistenceManager();

    CarRegService service = new CarRegServiceImpl();

//    CarReg cr = new CarReg();
//
//    cr.setDeviceID("deviceID");
//
//    service.add(cr);

    List<CarReg> list = service.getPaginationData().getResultList();
    int i = 0;
    Long key = null;
    resp.getWriter().println("count=" + list.size());
    for (CarReg o : list) {
      resp.getWriter().println("index:" + (++i) + ", CarReg=" + JSON.toJSONString(o));
      resp.getWriter().println("index:" + (++i) + ", key=" + KeyFactory.keyToString(o.getKey()));
      key = o.getKey().getId();
      // service.delete(o.getKey());
    }

    resp.getWriter().println("get last=" + JSON.toJSONString(service.getDataByID(key)));

    // Query q = pm.newQuery(CarReg.class);
    // try {
    // List<CarReg> list = (List<CarReg>) q.execute();
    // int i = 0;
    // for (CarReg o : list) {
    // resp.getWriter().println("index:" + (++i) + ", CarReg=" + JSON.toJSONString(o));
    // }
    // } catch (Exception e) {
    // // TODO: handle exception
    // } finally {
    // pm.close();
    // }

    // CarReg ci = new CarReg();
    // ci.setDeviceID("deviceID");
    // ci.setLatitude("latitude");
    // ci.setLongitude("longitude");
    // ci.setRegisterID("registerID");
    // ci.setSenderID("senderID");
    // ci.setUserName("userName");
    //
    // try {
    // pm.makePersistent(ci);
    // } catch (Exception e) {
    // log.severe("save CarReg error = " + e.getMessage());
    // } finally {
    // pm.close();
    // }
  }
}
