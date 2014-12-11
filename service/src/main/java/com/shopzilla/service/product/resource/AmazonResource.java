package com.shopzilla.service.product.resource;

import com.fasterxml.jackson.jaxrs.json.annotation.JSONP;
import com.shopzilla.service.product.Format;
import com.yammer.metrics.annotation.Timed;
import org.apache.commons.collections.CollectionUtils;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.validation.Valid;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
 
import javax.net.ssl.HttpsURLConnection;
 
@Path("/amazon")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_JSON})
public class AmazonResource {
    private static final String access_key = "AKIAICOFLE2S6RWUIJ7A";
    private static final String secret_key = "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";
    private static final String associate_tag = "retrevo01-20";
    private static final String endpoint = "ecs.amazonaws.com";
    private final String USER_AGENT = "Mozilla/5.0";

    public AmazonResource() {
    }

    @Timed(name = "getAmazon")
    @GET
    @JSONP
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    @Path("upc/{upc}")
    public String getAmazonResults(@PathParam("upc") Long upc,
                                     @QueryParam("format") Format format) throws Exception {
        SignedRequestsHelper helper;
        try {
            helper = SignedRequestsHelper.getInstance(endpoint, access_key, secret_key);
        } catch (Exception e) {
            e.printStackTrace();
            return "Fail";
        }

        String requestUrl = null;
        String title = null;
        String UPC = upc.toString();
        while(UPC.length() < 12) {
            UPC = "0" + UPC;
        }
        Map<String, String> params = new HashMap<String, String>();
        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemLookup");
        params.put("IdType", "UPC");
        params.put("SearchIndex", "All");
        params.put("ItemId", UPC);
        params.put("ResponseGroup", "Large");
        params.put("AssociateTag", associate_tag);
       
        requestUrl = helper.sign(params);
        return sendGet(requestUrl);
    }

    private String sendGet(String url) throws Exception {
 
	URL obj = new URL(url);
	HttpURLConnection con = (HttpURLConnection) obj.openConnection();
 
	// optional default is GET
	con.setRequestMethod("GET");
 
	//add request header
	con.setRequestProperty("User-Agent", USER_AGENT);

	int responseCode = con.getResponseCode();

	BufferedReader in = new BufferedReader(
	        new InputStreamReader(con.getInputStream()));
	String inputLine;
	StringBuffer response = new StringBuffer();

	while ((inputLine = in.readLine()) != null) {
		response.append(inputLine);
	}
	in.close();
 
	//print result
	return(response.toString());

    }

}
