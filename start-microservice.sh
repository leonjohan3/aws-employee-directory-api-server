#!/bin/bash

set -eu -o pipefail

aws sts get-caller-identity
aws appconfigdata start-configuration-session --application-identifier acs/aws-employee-directory --environment-identifier prod --configuration-profile-identifier prod --output text > /tmp/app-config-session.txt
SESSION=$(</tmp/app-config-session.txt)
aws appconfigdata get-latest-configuration --configuration-token "$SESSION" $HOME/config/application.yaml > /dev/null

exec java -Duser.home=/tmp -Xms384m -Xmx384m -Xlog:gc -cp app/BOOT-INF/classes:app/BOOT-INF/lib/* org.example.employee.EmployeeDirectoryApplication
