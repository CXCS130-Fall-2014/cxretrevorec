mvn clean install
rm mem.h2.db
java -jar service/target/product-service-1.0-SNAPSHOT.jar server service/service.yaml
