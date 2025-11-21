# SMS Checker / Frontend

The frontend allows users to interact with the model in the backend through a web-based UI.

The frontend is implemented with Spring Boot and only consists of a website and one REST endpoint.
It **requires Java 25+** to run (tested with 25.0.1).
Any classification requests will be delegated to the `backend` service that serves the model.
You must specify the environment variable `MODEL_HOST` to define where the backend is running.

The frontend service can be started through running the `Main` class (e.g., in your IDE) or through Maven (recommended):

    MODEL_HOST="http://localhost:8081" mvn spring-boot:run

The server runs on port 8080 as default. Once its startup has finished, you can access [localhost:8080/sms](http://localhost:8080/sms) in your browser to interact with the application. 

It is possible to change the port through either the docker-compose file, or through the dockerfile within this repository as it is an ENV variable. The reason we wanted to keep the ENV variable also declared in dockerfile was to make sure that the defaults worked properly if the container was run seperately without the docker-compose. The docker-compose overrides the ones declared in the dockerfile.


