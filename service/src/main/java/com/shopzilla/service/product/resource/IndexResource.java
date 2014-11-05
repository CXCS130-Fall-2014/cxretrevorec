package com.shopzilla.service.product.resource;

import com.yammer.dropwizard.views.View;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Path("/")
@Produces(MediaType.TEXT_HTML)
public class IndexResource {

  @GET
  public IndexView handle() {
    IndexView view = new IndexView("/index.mustache");
    return view;
  }

  public static class IndexView extends View {
    protected IndexView(String templateName) {
      super(templateName);
    }
  }
}  
