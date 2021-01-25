FROM openjdk:11.0
ARG JAR_FILE=target/*.jar
ARG AUTH=target/classes/project-test-gcp-294714-92822bceaa74.json
COPY ${JAR_FILE} app.jar
COPY ${AUTH} project-test-gcp-294714-92822bceaa74.json
RUN mkdir -p /opt/cprof && wget -q -O- https://storage.googleapis.com/cloud-profiler/java/latest/profiler_java_agent.tar.gz | tar xzv -C /opt/cprof
ENTRYPOINT ["java","-agentpath:/opt/cprof/profiler_java_agent.so=-cprof_service=product,-cprof_service_version=10","-jar","/app.jar"]