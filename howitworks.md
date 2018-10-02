build.sh checks python then runs builder.yml

builder.yml runs
    - generate.all
    - build.all

### generate.all 
only has meta.yaml - tasks are run before anything else, in this case exclusively
dependencies:
  - { role: meta-atlassian, os: alpine, osversion: "3.6" }
  - { role: postgres }
  - { role: meta-atlassian-sdk, os: alpine, osversion: "3.6" }

### build.all
- builds docker images from ../../tmp/buildall.yml

### meta-atlassian
- runs specific applications with version and java version i.e.
- { role: atlassian-jira, version: 7.4.2, java: 8 }

### atlassian-jira
- creates jira and ssl dir
- creates dockerfile with volumes etc
- creates entrypoint.sh
- copies the groovy script (which writes the db config)
- writes line into /tmp/buildall.yml