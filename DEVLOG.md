# Devlog

This file contains the process of the completion of assignments F1, F2 and F11 in this repository.

The solution to these assignments, the `lib-version` Integration chapter in the `README.md` and this devlog have all been created by Kasper van Maasdam.

## F1

I researched how to run the app without having to install java and maven. I came to the following:

~~To run the spring-boot application, I run~~ **(Old! See [F2](#f2))**:

```bash
docker run --rm -it --add-host=host.docker.internal:host-gateway -p 8080:8080 -v ./:/usr/src/app:Z -w /usr/src/app maven:3.9.11-eclipse-temurin-25-noble /bin/bash -c 'MODEL_HOST="http://host.docker.internal:8081" mvn spring-boot:run' 
```

Make sure to have the backend (spam detection model) serving on `localhost:8081`.

I have added a button that fills the message text box with a good sentence. This should become a sentence provided by the `lib-version` library.

## F2

I have added the `lib-version` library package as a dependency, as specified on the package page on the `lib-version` repo. To connect to the repo, I had to create a `settings.xml` file containing a personal access token to my GitHub account. For future use in github actions, I created an organization secret called `MAVEN_DEPLOY_TOKEN`. However, for now, just declaring it the environment works:

```bash
docker run --rm -it --add-host=host.docker.internal:host-gateway -p 8080:8080 -v ./:/usr/src/app:Z -w /usr/src/app maven:3.9.11-eclipse-temurin-25-noble /bin/bash -c 'PAT=ghp_xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx MODEL_HOST="http://host.docker.internal:8081" mvn spring-boot:run --settings .github/workspace/settings.xml' 
```

Inspiration:

- `settings.xml`: https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry
- Personal Access Token: https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/managing-your-personal-access-tokens
- GitHub Action Secrets: https://docs.github.com/en/actions/how-tos/write-workflows/choose-what-workflows-do/use-secrets

