# atm

To build use: mvn clean install

To run use: mvn spring-boot:run

Using in memory h2 database with two basic stand-alone tables (account, bank_note). data.sql populates these

To test: Swagger Url - http://localhost:8080/swagger-ui.html

This project uses lombok so plugin may need to be installed in IDE if not already done (this shoudln't affect building or running the application)

Further updates to be made to project to improve exception handling in the form of a rest exception handler to provide correct http status codes
