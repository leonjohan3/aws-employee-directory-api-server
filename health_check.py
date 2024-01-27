#!/usr/bin/env python2

import urllib2
import json
contents = json.loads(urllib2.urlopen("http://localhost:8080/actuator/health").read())

if contents['status'] != 'UP':
    raise Exception(contents['status'])
