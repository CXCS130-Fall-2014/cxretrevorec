/**
 * Copyright (C) 2004 - 2013 Shopzilla, Inc. 
 * All rights reserved. Unauthorized disclosure or distribution is prohibited.
 */
package com.shopzilla.service.product;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.Long;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import com.google.common.collect.Lists;
import com.shopzilla.service.product.data.ProductDao;
import com.shopzilla.service.product.data.ReviewDao;
import com.shopzilla.service.product.data.CategoryDao;
import com.shopzilla.service.product.resource.*;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.assets.AssetsBundle;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.jdbi.DBIFactory;
import com.yammer.dropwizard.jdbi.bundles.DBIExceptionsBundle;
import com.yammer.dropwizard.views.ViewBundle;
import com.yammer.metrics.util.DeadlockHealthCheck;
import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;
import javax.xml.parsers.*;
import org.xml.sax.*;
import org.w3c.dom.*;
import java.io.IOException;

/**
 * Main entry point for product service.
 */
public class ProductService extends Service<ProductServiceConfiguration> {

    public static void main(String[] args) throws Exception {
        new ProductService().run(args);
    }

    @Override
    public void initialize(Bootstrap<ProductServiceConfiguration> bootstrap) {
        bootstrap.addBundle(new ViewBundle());
        bootstrap.addBundle(new AssetsBundle());
        bootstrap.addBundle(new DBIExceptionsBundle());
    }

    @Override
    public void run(ProductServiceConfiguration configuration, Environment environment)
            throws Exception {

        /*
         * database setup
         */
        final DBIFactory factory = new DBIFactory();
        final DBI jdbi = factory.build(environment,
                configuration.getDatabaseConfiguration(),
                "h2");
        Handle handle = jdbi.open();
        // This is so I can change my table
        loadData(handle);

        /*
         * "real" database DAO
         */
        final ProductDao productDao = jdbi.onDemand(ProductDao.class);
        final ReviewDao reviewDao = jdbi.onDemand(ReviewDao.class);
        final CategoryDao categoryDao = jdbi.onDemand(CategoryDao.class);

        /*
        * health checks
         */
        environment.addHealthCheck(new DeadlockHealthCheck());

        /*
         * Dozer mapping
         */
        Mapper mapper = new DozerBeanMapper(Lists.newArrayList("dozerMappings.xml"));

        /*
        * endpoints
         */
        environment.addResource(new ProductResource(productDao, mapper));
        environment.addResource(new ReviewResource(reviewDao, mapper));
        environment.addResource(new CategoryResource(categoryDao, mapper));
        environment.addResource(new AmazonResource());
        environment.addResource(new IndexResource());
    }

    //Helper function to import product data
    String fieldFromLine(String xmlLine) {
        int start = xmlLine.indexOf(">")+1;
        int end = xmlLine.indexOf("<", start);
        return xmlLine.substring(start, end);
    }

    String getCategoryValue(String in) {
        int start = in.indexOf(">")+1;
        int end = in.indexOf("&", start);
        if(end < 0) {
            return "other";
        }
        return in.substring(start, end);
    }

    String getLongValue(String input) {
        Long ret;
        try {
            ret = Long.parseLong(input);
        } catch (NumberFormatException nfe) {
            System.err.println(nfe.getMessage());
            return "NULL";
        }
        return ret.toString();
    }

    public boolean loadData(Handle handle) throws IOException{
        // max title length in the dataset is 150
        // max comment length in the dataset is 1952
        // title and comments need single quotes escaped

        //Create the two tables, products and reviews
        handle.execute("DROP ALL OBJECTS");
        handle.execute("CREATE TABLE IF NOT EXISTS product_entry (" +
                "product_id LONG NOT NULL," +
                "product_name VARCHAR(300) NOT NULL," +
                "product_description VARCHAR(300) NOT NULL," +
                "product_price LONG NOT NULL," +
                "product_upc LONG," +
                "product_category VARCHAR(300) NOT NULL," +
                "product_ean13 LONG," +
                "review_count INTEGER," +
                "PRIMARY KEY (product_id))");
        handle.execute("CREATE TABLE IF NOT EXISTS reviews (" +
                "rid LONG NOT NULL," +
                "pid LONG NOT NULL," +
                "title VARCHAR(250) NOT NULL," +
                "rating INTEGER," +
                "comment VARCHAR(3000) NOT NULL," +
                "PRIMARY KEY (rid))");

        System.out.println("Start loading products");

        //Import new product data from RetrevoProductData.xml
    	RandomAccessFile raf = new RandomAccessFile("RetrevoProductData.xml", "r");
    	try {
            int count = 0;
            for (String s = raf.readLine(); s != null; s=raf.readLine()) {
                if (s.equals("  <ROW>")) {
                    String pid = getLongValue(fieldFromLine(raf.readLine()));
                    // Skip sku
                    raf.readLine();
                    String name = fieldFromLine(raf.readLine()).replaceAll("'", "''");
                    String description = fieldFromLine(raf.readLine()).replaceAll("'", "''");
                    String price = getLongValue(fieldFromLine(raf.readLine()));
                    // Skip sku_norm
                    raf.readLine();
                    String upc = getLongValue(fieldFromLine(raf.readLine()));
                    String cat = getCategoryValue(raf.readLine());
                    String ean13 = getLongValue(fieldFromLine(raf.readLine()));

                    name = name.replace("\\", "/");
                    description = name.replace("\\", "/");
                    cat = cat.replace("\\", "/");
                    String merge = "MERGE INTO product_entry (product_id, product_name, product_description, product_price, product_upc, product_category, product_ean13, review_count) KEY(product_id) VALUES (" + pid + ", \'" + name + "\', \'" + description + "\', " + price + ", " + upc + ", \'" + cat + "\', " + ean13 + ", 0)";
                    System.out.println(merge);
                    handle.execute(merge);
                }
            }
        } finally {
            raf.close();
        }
        

        System.out.println("Finished loading products");

        //Import review data from dataset.xml
        Document dom;
        // Make an instance of the DocumentBuilderFactory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {

            // use the factory to take an instance of the document builder
            DocumentBuilder db = dbf.newDocumentBuilder();
            // parse using the builder to get the DOM mapping of the XML file
            dom = db.parse("reviews.xml");

            long rid = 0;
            String pid = null;
            String title = null;
            String rating = null;
            String comment = null;
            int review_count = 0;

            Node product, review;
            NodeList products = dom.getFirstChild().getChildNodes();
           
            for (int i = 0; i < products.getLength(); i++) {
                product = products.item(i);
                if (product.getNodeType() == Node.ELEMENT_NODE) {
                    NamedNodeMap nnm = product.getAttributes();
                    pid = nnm.getNamedItem("PID").getNodeValue();

                    List<Map<String, Object>> rs = handle.select("SELECT product_id FROM product_entry WHERE product_id = " + pid + "LIMIT 1");
                    // Only care about products in our products table
                    if (rs.isEmpty())
                        continue;

                    Node child = product.getChildNodes().item(1);
                    NodeList reviews = child.getChildNodes();
                    review_count = 0;
                    for (int j = 0; j < reviews.getLength(); j++) {
                        review = reviews.item(j);
                        
                        if (review.getNodeType() == Node.ELEMENT_NODE) {
                            NamedNodeMap nnm2 = review.getAttributes();
                            title = nnm2.getNamedItem("Title").getNodeValue();
                            comment = nnm2.getNamedItem("Value").getNodeValue();
                            rating = nnm2.getNamedItem("Rating").getNodeValue();

                            // single quotes are escaped by single quotes in h2
                            title = title.replace("'", "''");
                            comment = comment.replace("'", "''");
                            // backslashes cause problems in h2
                            title = title.replace("\\", "/");
                            comment = comment.replace("\\", "/");
                            // some ratings are empty
                            rating = (rating.isEmpty()) ? "NULL" : rating;
                            review_count++;
                            String merge = "MERGE INTO reviews (rid, pid, title, rating, comment) VALUES (" + rid + "," + pid + ",'" + title + "'," + rating + ",'" + comment + "')";
                            System.out.println(merge);
                            handle.execute(merge);
                            rid++;
                        }
                    }
                    //Update product table with # of reviews
                    handle.execute("UPDATE product_entry SET review_count = " + review_count + " WHERE product_id = " + pid);
                }
            }

            return true;

        } catch (ParserConfigurationException pce) {
            System.err.println(pce.getMessage());
        } catch (SAXException se) {
            System.err.println(se.getMessage());
        } catch (IOException ioe) {
            System.err.println(ioe.getMessage());
        }

        return false;
    }
}
