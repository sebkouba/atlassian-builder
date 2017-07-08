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

Example configfile for JIRA:

```bash
jira {
    disarm true
    minimumMemory "2048m"
    maximumMemory "2048m"
    database {
        user "jirauser"
        password "jirapass"
        host "localhost"
        name "jira"
        type "postgres72"
        schema "public"
    }
    baseUrl "https://jira.intranet.com"
}
```

The same structure is provided for Confluence and Bitbucket. Examples provided.

## Using it

```
docker-compose up -d
```

After a while you will see application running on localhost:(8080|8090|7990)

