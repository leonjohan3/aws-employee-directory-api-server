#!/bin/bash

set -eu -o pipefail

# Note:
# SPRING_PROFILES_ACTIVE env var is set in the SAM template and specified when running `sam deploy --parameter-overrides SpringProfilesActive=prod`

#aws sts get-caller-identity
#aws appconfigdata start-configuration-session --application-identifier acs/aws-employee-directory --environment-identifier prod --configuration-profile-identifier prod --output text > /tmp/app-config-session.txt
#SESSION=$(</tmp/app-config-session.txt)
#aws appconfigdata get-latest-configuration --configuration-token "$SESSION" $HOME/config/application.yaml > /dev/null
#aws sts get-caller-identity
./getLatestConfig -a bla/aws-employee-directory -e $SPRING_PROFILES_ACTIVE -o config/application.yaml

#exec java -Duser.home=/tmp -Xms384m -Xmx384m -Xlog:gc -cp app/BOOT-INF/classes:app/BOOT-INF/lib/* org.example.employee.EmployeeDirectoryApplication
exec java -Dspring.profiles.active=$SPRING_PROFILES_ACTIVE -Duser.home=/tmp -Xms768m -Xmx768m -Xlog:gc -cp app/BOOT-INF/classes:app/BOOT-INF/lib/* org.example.employee.EmployeeDirectoryApplication
