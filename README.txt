Neb-project

A relatively simple and RESTful work example done by me in which I've used Java 8, Maven, Spring Boot, JPA, HATEOAS, Spring Security, RestTemplate, MySQL and Liquibase.

The example is divided into two sections which both need to be running.
* Nebhistory: userhistory
* Nebusers: userdata, spring security login, login here will send historymessage about login/logout to nebhistory

Requirements:
* Java 8
* MySQL
* Maven 3.3.3

Installation

# Clone your GitHub repository
git clone https://github.com/hannulyri/neb-project.git


Configuration

Both applications have separate application.yml where you can set database username and password for the MySQL-database.


Starting up

# open console 1

# Go to the nebhistory directory
cd neb-project/nebhistory

# Start the application
mvn spring-boot:run

# open console 2

# Go to the nebusers directory
cd neb-project/nebusers

# Start the application
mvn spring-boot:run


API REST

When both applications are running, you can start testing. You find frontpages below. Nebusers port is 8050, nebhistory 8080.
Nebhistory => http://localhost:8080/
Nebusers => http://localhost:8050/


API Nebhistory

Adding historymessage:
curl -i -X POST -H "Content-Type: application/json" -d '{"userId": 1,"message":"Hello World!"}' http://localhost:8080/api/history

Get historymessage:
curl -i -X GET http://localhost:8080/api/history/1

Delete historymessage
curl -i -X DELETE http://localhost:8080/api/history/1


API Nebusers

Adding user:
curl -i -X POST -H "Content-Type: application/json" -d '{"login": "foologin","password": "foopassword","firstName": "foofirstname","lastName": "foolastname","email": "foo@mail.com"}' http://localhost:8050/api/users

Get user data:
curl http://localhost:8050/api/users/1

Login:
curl -i -X POST --data "login=foologin&password=foopassword" http://localhost:8050/api/authentication
HTTP/1.1 200 OK
Server: Apache-Coyote/1.1
Access-Control-Allow-Origin: *
Access-Control-Allow-Methods: POST, GET, DELETE, PUT
Access-Control-Allow-Headers: x-requested-with
Access-Control-Expose-Headers: x-requested-with
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
Set-Cookie: JSESSIONID=72D8352F335A1C617EFE93A00C458E87; Path=/; HttpOnly
Content-Length: 0
Date: Sun, 12 Jul 2015 12:03:38 GMT

Logout (get Set-Cookie from login):
curl -i --cookie "JSESSIONID=72D8352F335A1C617EFE93A00C458E87; Path=/; HttpOnly" http://localhost:8050/api/logout

Fetch userhistory from Nebhistory with resttemplate:
curl http://localhost:8050/api/users/1/history

Update user:
curl -i -X PUT -H "Content-Type: application/json" -d '{"firstName": "CHANGEDfoofirstname","lastName": "CHANGEDfoolastname","email": "CHANGEDfoo@mail.com"}' http://localhost:8050/api/users/1

Delete user:
curl -i -X DELETE http://localhost:8050/api/users/1


Testing

Both Nebhistory and Nebusers have their own integrationtests, which can be run with 'mvn test' in their own directories. 

Because of lack of time, I only test controllers. Controller inputs, outputs and HTTP-codes returned. Even with controllers I didn't test any of the HATEOAS-features, like self links, pagination etc...

Tests that I totally disregarded were testing services, repositories, models, DTO etc...


Exception Handling 

Exception handling was much more easy to do using java 8's Optional-class (https://docs.oracle.com/javase/8/docs/api/java/util/Optional.html). A great new feature!