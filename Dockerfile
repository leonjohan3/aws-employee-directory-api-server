FROM amazoncorretto:17

ARG VERSION=0.0.1
ARG JAR_NAME=aws-employee-directory-api-server-${VERSION}.jar
ARG WORK_DIR=/tmp/app

ENV TZ          Australia/Melbourne

COPY build/libs/${JAR_NAME} /tmp/

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime  &&  echo $TZ > /etc/timezone  && \
    mkdir ${WORK_DIR}  &&  cd ${WORK_DIR}  &&  jar -xf /tmp/${JAR_NAME}  && \
    rm /tmp/${JAR_NAME}

WORKDIR ${WORK_DIR}
USER nobody

EXPOSE 8080
ENTRYPOINT ["java", "-Xms384m", "-Xmx384m", "-XshowSettings", "-cp", "BOOT-INF/classes:BOOT-INF/lib/*", "org.example.employee.EmployeeDirectoryApplication"]
