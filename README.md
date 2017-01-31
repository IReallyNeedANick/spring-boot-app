# POC Spring Boot Application


This is simple spring boot application to show how some technologies work together
technologies/patterns used
 - maven profiles are tied to spring profile. 2 profiles used: test for testing, dev for development
 - spring boot 1.4.3 (latest January 2017)
 - spring for IOC, scheduling,...
 - swagger 2.6.1
 - Spring web MVC for REST controllers
 - Jackson for data binding
 - Hibernate validators for validating model objects (departmentDto)
 - H2 for database (with spring JPA)
 - logback-classic for logging
 - assertJ for fluent assertion
 - mockito for mocking
 - spring boot test for testing running application
 - spring actuator for monitoring 

### building & running application

application settings are in /config file, split by profile. 

to build application run:

```
mvn clean install
```

start spring boot application:

- from maven
  ```
  mvn spring-boot:run
  ```
- running as packed application
  ```
  java -jar target/basic-xml-parser-1.0.jar
  ```
- running ```com.sbapp.Application``` class ```main``` method manually from IDE

Default profile is ```dev```, for test ```test```. There is also prod profile enabled but it has no properties (add them to ```config/appplication-prod.properties```).

Application runs by default on port 8080.

To access swagger UI go to:
```
http://localhost:8080/swagger-ui.html
```

from here you can test application calls.