#!/bin/bash

set -eu -o pipefail

aws sts get-caller-identity

java -Duser.home=/tmp -Xms384m -Xmx384m -Xlog:gc -cp app/BOOT-INF/classes:app/BOOT-INF/lib/* org.example.employee.EmployeeDirectoryApplication
