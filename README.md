# SMS Checker / Frontend

The frontend allows users to interact with the model in the backend through a web-based UI.

The frontend is implemented with Spring Boot and only consists of a website and one REST endpoint.
It **requires Java 25+** to run (tested with 25.0.1).
Any classification requests will be delegated to the `backend` service that serves the model.
You must specify the environment variable `MODEL_HOST` to define where the backend is running.

The frontend service can be started through running the `Main` class (e.g., in your IDE) or through Maven (recommended):

    MODEL_HOST="http://localhost:8081" mvn spring-boot:run

The server runs on port 8080. Once its startup has finished, you can access [localhost:8080/sms](http://localhost:8080/sms) in your browser to interact with the application.

## F1

To run the spring-boot application, I run:

```bash
docker run --rm -it --add-host=host.docker.internal:host-gateway -p 8080:8080 -v ./:/usr/src/app:Z -w /usr/src/app maven:3.9.11-eclipse-temurin-25-noble /bin/bash -c 'MODEL_HOST="http://host.docker.internal:8081" mvn spring-boot:run' 
```

Make sure to have the backend (spam detection model) serving on `localhost:8081`.

I have added a button that fills the message text box with a good sentence. This should become a sentence provided by the `lib-version` library. (Yet to be implemented)
