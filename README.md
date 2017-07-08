## atlassian-docker-builder

* Builds an Ansible based docker image
* Generate dockerfiles for all Atlassian application 
* Build the docker containers for the provided Atlassian applications
* While building, it takes care of:
  * using the correct zipfile (Atlassian switched the zip-file-name after >7.0.0 release)
  * stash to bitbucket rename included
  * confluence >6.0.0 release embeds an extra reverse proxy, so you don't have to perform the Synchrony-configuration, it's all there
  * extra specialties are catched with jinja templating. Nothing left to worry about.

## Requirements

* python
* docker-engine locally
* virtualenv/pip

You can install the requirements from requirements.txt (in a virtualenv if you like). You also need Python.
A build.sh is provided which does it all for you on most platforms/systems.

```bash
pip install -r requirements.txt
```

## Usage

```bash
./.venv/bin/ansible-playbook builder.yml
```
or just run build.sh

```bash
./build.sh
```

## Run it !!!
```
docker run --rm -it atlassian/jira:7.2.1
```
Or whatever version you configured. It just works!

## Adding versions

Adding containers for different versions is as simple as adding them to the jinja-list in the following file:
````
roles/meta-atlassian/meta/main.yml
````
Alternatively, you can keep a list of versions you want to maintain in builder.yml, the main playbook for this project. Example is included within the file.

Rerun the build and your images are created idempotently.

## Author

Dieter Verhelst (info@werus.be)

## License

This project is licenced under the MIT License allowing everything to use/reuse the code as is. Contributing back to this project is much appreciated.

## Special thanks

[Hein Couwet](mailto:info@2improveit.eu) for being a sparring-partner, exchanging ideas and helping out on the project with the Groovy-configuration layer.

[Francis Martens](mailto:francis@idalko.net) for testing the containers, using them, providing extensive testing and bugreporting on many of the issues. 

[Mathy Vanvoorden](mailto:mathy@draca.be) for providing the meta-structure allowing the dependency-based build.
