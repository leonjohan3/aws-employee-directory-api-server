# FROM amazoncorretto:17
FROM public.ecr.aws/amazoncorretto/amazoncorretto:17

ARG VERSION=0.0.1
ARG JAR_NAME=aws-employee-directory-api-server-${VERSION}.jar
# ARG JAVA_AGENT_JAR_NAME=aws-opentelemetry-agent-1.32.1.jar

ENV TZ          Australia/Sydney
ENV HOME        /tmp

ARG WORK_DIR=${HOME}/app
ARG HEALTH_CHECK_APP=${HOME}/health_check.py

COPY build/libs/${JAR_NAME} ${HOME}
COPY health_check.py ${HOME}
COPY start-microservice.sh ${HOME}
# COPY ${JAVA_AGENT_JAR_NAME}  ${HOME}
# ADD https://github.com/aws-observability/aws-otel-java-instrumentation/releases/latest/download/aws-opentelemetry-agent.jar /tmp/aws-opentelemetry-agent.jar
# ENV JAVA_TOOL_OPTIONS=-javaagent:/tmp/aws-opentelemetry-agent.jar

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime  &&  echo $TZ > /etc/timezone  && \
#     yum install -y bind-utils procps iproute nmap && \
    yum install -y less unzip groff && \
    curl -s https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip -o /tmp/awscliv2.zip && \
    unzip -q -d /tmp/ /tmp/awscliv2.zip && /tmp/aws/install && \
    mkdir ${WORK_DIR}  &&  cd ${WORK_DIR}  &&  jar -xf /tmp/${JAR_NAME}  && \
    rm /tmp/${JAR_NAME} && chown nobody ${HEALTH_CHECK_APP} ${HOME}/start-microservice.sh && chmod u+x ${HEALTH_CHECK_APP} ${HOME}/start-microservice.sh && ls -l $HOME

WORKDIR ${HOME}
USER nobody
#RUN aws sts get-caller-identity

EXPOSE 8080
ENTRYPOINT ["./start-microservice.sh"]
# ENTRYPOINT ["java", "-Duser.home=/tmp", "-Xms384m", "-Xmx384m", "-Xlog:gc", "-cp", "app/BOOT-INF/classes:app/BOOT-INF/lib/*", "org.example.employee.EmployeeDirectoryApplication"]
# ENTRYPOINT ["java", "-Duser.home=/tmp", "-Xms768m", "-Xmx768m", "-Xlog:gc", "-cp", "app/BOOT-INF/classes:app/BOOT-INF/lib/*", "org.example.employee.EmployeeDirectoryApplication"]
# ENTRYPOINT ["java", "-javaagent:aws-opentelemetry-agent-1.32.1.jar", "-Duser.home=/tmp", "-Xms384m", "-Xmx384m", "-Xlog:gc", "-cp", "app/BOOT-INF/classes:app/BOOT-INF/lib/*", "org.example.employee.EmployeeDirectoryApplication"]
