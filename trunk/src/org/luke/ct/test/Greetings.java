package org.luke.ct.test;

import java.util.ArrayList;

import javax.inject.Named;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.appengine.api.users.User;

/**
 * Defines v1 of a helloworld API, which provides simple "greeting" methods.
 */
@Api(name = "helloworld", version = "v1", clientIds = { com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID })
public class Greetings {
  public static ArrayList<HelloGreeting> greetings = new ArrayList<HelloGreeting>();

  static {
    greetings.add(new HelloGreeting("hello world!"));
    greetings.add(new HelloGreeting("goodbye world!"));
  }

  @ApiMethod(name = "greeting", httpMethod = HttpMethod.GET)
  public HelloGreeting getGreeting(@Named("id") Integer id) {
    return greetings.get(id);
  }

  @ApiMethod(name = "greetings.multiply", httpMethod = HttpMethod.POST)
  public HelloGreeting insertGreeting(@Named("times") Integer times, HelloGreeting greeting) {
    HelloGreeting response = new HelloGreeting();
    StringBuilder responseBuilder = new StringBuilder();
    for (int i = 0; i < times; i++) {
      responseBuilder.append(greeting.getMessage());
    }
    response.setMessage(responseBuilder.toString());
    return response;
  }

  @ApiMethod(name = "greetings.authed", path = "greeting/authed")
  public HelloGreeting authedGreeting(User user) {
    HelloGreeting response = new HelloGreeting("hello " + user.getEmail());
    return response;
  }

}
