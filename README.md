# GaelCravings_Backend

## Prerequisites 
- Docker installed on your Mac
- Java 21 and Gradle installed locally

### Build the Spring Boot JAR
1. First, build your application using Gradle:
    ``` 
    ./gradlew clean bootJar 
    ```
   
2. Build the Docker Image
    ```
    docker build -t springio/gs-spring-boot-docker .
    ```

3. Run the Docker Container
    ```
   docker run -p 8080:8080 springio/gs-spring-boot-docker
   ```
4. Access your Application

    ```
   http://localhost:8080
   ```