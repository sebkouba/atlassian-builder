## using the atlassian-docker containers

The containers (JIRA,confluence,bitbucket) include a special layer containing a groovy-scripting with thanks to [Hein Couwet](mailto:info@2improveit.eu)
These scripts execute whatever is defined in the $application.config provided with it.

If you map JIRA.config to /opt/jira/config/jira.config in the container, the container will always reconfigure itself to those values at startup or at initial startup. 
Elements configured automatically:
* server.xml for tomcat. So your container actually uses the virtualhost you define it to use.
* memory parameters
* database-url is configured at startup
* JIRA: you can opt to start a container with notifications disabled (fantastic usecase in test-environment)
* ...

**Optional** example configfile for JIRA:

```bash
jira {
    disarm true
    minimumMemory "2048m"
    maximumMemory "2048m"
    database {
        user "atlassian"
        password "atlassian"
        host "jiradb"
        name "jira"
        type "postgres72"
        schema "public"
    }
    baseUrl "https://jira.intranet.local"
}
```

The same structure is provided for Confluence and Bitbucket. Examples provided.

## Using it

```
docker-compose up -d
```

Embedded is a reverse proxy which connects to the VIRTUAL_HOST parameter. If you point dns/hostsfile to the host running the containers, you will be able to navigate and set up:
* http://jira.intranet.local
* http://confluence.intranet.local
* http://bitbucket.intranet.local

Changing the virtualhost/fqdn is only changing the VIRTUAL_HOST parameter and running 'docker-compose up -d' . 

## Configuring the databases

Database hosts/names can be viewed in the docker-compose file. For your convenience:
| Application   | Hostname      | Database   | Username  | Password  | Url                              |
| ------------- | ------------- | ---------- | --------- | --------- | -------------------------------- |
| jira          | jiradb        | jira       | atlassian | atlassian | http://jira.intranet.local       |
| confluence    | confluencedb  | confluence | atlassian | atlassian | http://confluence.intranet.local |
| bitbucket     | bitbucketdb   | bitbucket  | atlassian | atlassian | http://bitbucket.intranet.local  |


