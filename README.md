#Quickstart

<pre>
    # Make sure database.xml is in the cxretrevorec directory and renamed to reviews.xml
    # Reviews in database limited to first 100 so compile time doesnt suck
    mvn clean install
    cd service
    java -Xmx1024M -jar target/product-service-1.0-SNAPSHOT.jar server service.yaml
    # open browser to http://localhost:7500/services/product/v1/
</pre>

