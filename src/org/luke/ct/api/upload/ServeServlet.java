package org.luke.ct.api.upload;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

public class ServeServlet extends HttpServlet {
  private static final long serialVersionUID = -1648179235728538463L;
  private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
    String key = req.getParameter("blob_key");
    if (StringUtils.isBlank(key)) {
      sendError(res, "未提供blob_key");
      return;
    }
    BlobKey blobKey = new BlobKey(key);
    if (null == blobKey.getKeyString()) {
      sendError(res, "不合法的blob_key:" + key);
      return;
    }
    blobstoreService.serve(blobKey, res);
  }

  private void sendError(HttpServletResponse res, String errorMessage) throws IOException {
    JSONObject json = new JSONObject();
    json.put("errorMessage", errorMessage);

    res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    res.setContentType("application/json");
    PrintWriter out = res.getWriter();
    out.print(json.toString());
    out.flush();
    out.close();
  }
}
