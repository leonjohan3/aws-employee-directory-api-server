FROM amazoncorretto:17

ARG VERSION=0.0.1
ARG JAR_NAME=aws-employee-directory-api-server-${VERSION}.jar

ENV TZ          Australia/Melbourne
ENV HOME        /tmp

ARG WORK_DIR=${HOME}/app

COPY build/libs/${JAR_NAME} ${HOME}

RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime  &&  echo $TZ > /etc/timezone  && \
    mkdir ${WORK_DIR}  &&  cd ${WORK_DIR}  &&  jar -xf /tmp/${JAR_NAME}  && \
    rm /tmp/${JAR_NAME}

WORKDIR ${HOME}
USER nobody

EXPOSE 8080
ENTRYPOINT ["java", "-Duser.home=/tmp", "-Xms384m", "-Xmx384m", "-XshowSettings", "-cp", "app/BOOT-INF/classes:app/BOOT-INF/lib/*", "org.example.employee.EmployeeDirectoryApplication"]
