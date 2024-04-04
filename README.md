- [CS 1632 - Software Quality Assurance](#cs-1632---software-quality-assurance)
  * [Description](#description)
  * [Part 2: Dockers](#part-2-dockers)
    + [Prerequisites](#prerequisites)
    + [Do some sanity tests](#do-some-sanity-tests)
    + [Create Docker image](#create-docker-image)
    + [Import Selenium JUnit Tests](#import-selenium-junit-tests)
    + [Add CI Test Workflow](#add-ci-test-workflow)
    + [Add Docker Publish Workflow](#add-docker-publish-workflow)
    + [Pull published Docker image and launch from desktop](#pull-published-docker-image-and-launch-from-desktop)
- [Submission](#submission)
- [Groupwork Plan](#groupwork-plan)

# CS 1632 - Software Quality Assurance
Spring Semester 2024 - Supplementary Exercise 4

* DUE: April 12 (Friday), 2024 11:59 PM

## Description

During the semester, we learned various ways in which we can automate testing.
But all that automation is of no use if your software organization as a whole
does not invoke those automated test scripts diligently.  Preferably, those test
scripts should be run before every single source code change to the repository,
and for good measure, regularly every night or every weekend just in case.  Now,
there are many reasons why this does not happen if left to individual
developers:

1. Developers are human beings so they forget.  Or, they remember to run
   some tests, but not all the test suites that are relevant to the changes
they have made.

1. Developers are sometimes on a tight schedule, so they are tempted to skip
   testing that may delay them, especially if they are not automated.  They
justify their actions by telling themselves that they will fix the failing
tests "as soon as possible", or that the test cases are not testing anything
important, or that failing test cases in modules under the purview of
another team "is not my problem".

In Part 1 of this exercise, we will learn how to build an automated
"pipeline" of events that get triggered automatically under certain
conditions (e.g. a source code push).  A pipeline can automate the entire
process from source code push to software delivery to end users, making sure
that a suite of tests are invooked as part of the process before software is
delivered.  Pipelines that are built for this purpose are called CI/CD
(Continuous Integration / Continuous Delivery) pipelines, because they
enable continuous delivery of software to the end user at at high velocity
while still maintaining high quality.  We will learn how to build a fully
functioning pipeline for the (Rent-A-Cat application)[../exercises/2] that
we tested for Exercise 2 on our GitHub repository.

In Part 2, we will learn how to use dockers to both test and deploy
software as part of a CI/CD pipeline.  Dockers are virtualized execution
environments which can emulate the execution environments in the deployment
sites (OS, libraries, webservers, databases, etc.) so that software can be
tested in situ.  In our case, we will create a docker image out of the
(Rent-A-Cat website)[cs1632.appspot.com] that we tested for Deliverable 3
for testing and deployment.

## Part 2: Dockers

**GitHub Classroom Link:** TBD

In Part 2, we will use Docker to test and deploy the Rent-A-Cat website that
we tested in Deliverable 3.  We will test the website using the Selenium
JUnit tests that you wrote for the assignment.

Docker runs software in a self-contained virtualized environment called
containers.  A container is launched from a Docker image, which is a binary
file that contains the file system that the container is launched from.  You
can think of Docker images as comparable to Linux images.  Docker images can
be built from any operating system version supported by Docker and can come
with any software or files pre-installed.  As such, Docker images are the
preferred method of deployment for many software organizations.  The Docker
image is to run reliably on any user machine or on a cloud service provider
without a hitch since everything comes pre-packaged.

Deploying software encapsulated in a Docker image makes testing simpler and
more rigorous at the same time. Now the tester does not have to think about
myriad preconditions that can impact the software such as operating system
versions, whether libraries and packages of certain version are installed,
or whether environment variables and configuration files are set with
correct values.  And these preconditions are usually what causes software to
fail during deployment.

### Prerequisites

1. You will need to download and install Docker Desktop for this exercise:
   https://www.docker.com/products/docker-desktop/

   You need the Docker Engine included in Docker Desktop to launch Docker containers.

1. Please install the Chrome web browser: https://www.google.com/chrome/

1. Please install/update the Chrome web driver.  Sorry, this exercise only works on Chrome not Firefox.
   
   ```
   selenium\manager\windows\selenium-manager.exe --browser chrome
   ```

   If you use MacOS:

   ```
   selenium/manager/macos/selenium-manager --browser chrome
   ```

   If you use Linux:

   ```
   selenium/manager/linux/selenium-manager --browser chrome
   ```

### Do some sanity tests

This project contains a Spring Boot application, which is a Java framework
for creating web servers.  First, let's start by launching the web server
and making sure it is working.  You can launch using the following command:

```
mvn spring-boot:run
```

The output from this command should end in these two lines:

```
...
2024-03-25 18:33:15.380  INFO 21180 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
2024-03-25 18:33:15.395  INFO 21180 --- [           main] c.s.ServingWebContentApplication         : Started ServingWebContentApplication in 2.46 seconds (JVM running for 2.852)
```

Note this starts the Tomcat web server listening on port 8080.  Try opening
the web app on a browser using the URL:

```
http://localhost:8080/
```

You should our Rent-A-Cat website.  Now leave the server running and open
another terminal and then invoke Maven test after cd'ing into the selenium
directory.

```
cd selenium
mvn test
```

There is a single Selenium JUnit test in this project that tests that the
web server can service HTTP reqeusts on port 8080:

```
  @Test
  public void testConnection() {
    // Test that the webserver is ready to service an HTTP request
    driver.get("http://localhost:8080/");
  }
```

And of course, it should pass.

### Create Docker image

We want to deploy our web app as a Docker image.  Creating a Docker image is
much simpler than you think!  You simply start from a base Linux image and
then "layer" changes on top of it to create your custom image.  You
write this process into something called a Dockerfile and you are done.

Create a Dockerfile at the root of your repository with the following content:

```
# specify base image
FROM adoptopenjdk/openjdk11:slim

# install Maven on top of base image
RUN apt-get update && apt-get install -y --no-install-recommends maven

# define working directory
WORKDIR /app

# copy over app files
COPY pom.xml .
COPY src src

# expose default Spring Boot port 8080
EXPOSE 8080

# define default command
CMD ["/bin/sh", "-c", "mvn spring-boot:run"]
```

The description of the base image adoptopenjdk/openjdk11:ubi can be found here:

https://hub.docker.com/r/adoptopenjdk/openjdk11

Base images of all imaginable OS versions and with all widely used packages
can be found at Docker Hub:

https://hub.docker.com/search

For our image, we add the Maven build system and copy over files required to
launch our web app.  We also expose TCP port 8080 to the outside world since
that is the port that Spring Boot is going to be using.  If we don't
explicitly expose ports, they will not be accessible in the Docker container
created out of this image.  Lastly, we define the command that will be
executed by default when the image is launched in a container, which is "mvn
spring-boot:run".

Now let's try creating a Docker container out of this image to test it.
There is a convenient tool for this called docker-compose.  The
docker-compose tool knows how to compose one or more Docker containers and
network them together into a distributed system.  It is configured using
another file in YAML format named docker-compose.yaml.  In our case, we just
have one container so it is rather simple:

```
version: '3'

services:
  server:
    build:
      context: '.'
      dockerfile: Dockerfile
    container_name: rentacat
    ports:
      - "8080:8080"
```

Add a docker-compose.yaml file with the above content at the root of the
repository.  The file consists of a list of services that are launched
together.  In our case, we just have one service named "server".  A service
can be created from a Docker image pulled from Docker Hub or some other
registry, or, as in our case, built locally when the "build:" keyword is
specified.  The "context:" and "dockerfile:" values specify the context
within which the Docker image will be built and the name of the Dockerfile.
The "ports:" values specify a port mapping between container and host --- in
this case, port 8080 is mapped to the same port on the host.

Now we are going to use this YAML file to launch a container listening on
port 8080.  But before doing so, we need to kill the web server that we
launched previously or we are going to have a port conflict.  Go to the
terminal where Spring Boot is running and kill the process using Ctrl+C.
Make sure the server is dead by reloading the page http://localhost:8080/ on
your web browser and confirming that the server is not found.

Now let's first start Docker Desktop, which will start Docker Engine
included in the application.  After it is running, invoke the
docker-compose tool to bring up the container:

```
docker-compose up
```

You should soon see the image registered on Docker Desktop:

<img alt="Docker Desktop Images" src=img/docker_1.png>

And also a container instance running with the name "rentacat" with port
8080 open:

<img alt="Docker Desktop Containers" src=img/docker_2.png>

Now try reloading http://localhost:8080/ again and you should see the page
back up.  Congrats, you have created your first Docker container!

To stop the container, you only need to click on the trash bin icon that
appears when you hover over the container.  Or you can do on the
commandline:

```
docker-compose down
```

You may also go to the "Images" menu and delete the image if you wish to do
so.  As long as the image is still there, you can relaunch the container
using the "Run" button, but when you do, make sure that you open the
"Optional Settings" and enter the port mapping 8080 on Local Host:

<img alt="Port mapping to 8080" src=img/docker_3.png>

### Import Selenium JUnit Tests

If you have stopped the container, fire up the container again because we
are going to write some tests for it.  We want to add some actual Selenium
tests that test the web app.  Well, we already wrote those tests for
Deliverable 3, so let's just import D3Test.java from that project into
selenium/src/test/java/edu/pitt/cs.

You need to do these three things though in D3Test.java:

1. Replace the root URL of the web pages accessed with "http://localhost:8080".  Please make sure you use http:// and not https://.

1. Remove the tests that fail because they trigger defects on the web app
   (remember the tests whose names start with DEFECT?).

Make sure everything passes with:

```
mvn test
```

### Add CI Test Workflow

You can imagine developers not wanting to run those Selenium tests on their
desktops every time they do a commit.  Let's make a CI test workflow out
of these tests so they don't have to.

Add a new workflow file named docker-ci.yml to your GitHub repository with
the following content (if you forgot how to do that already, review the
[instructions for Maven CI](#add-maven-ci-workflow)):

```
name: Docker CI

on:
  workflow_dispatch:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]

jobs:

  test_dockerized_webserver:

    runs-on: ubuntu-latest

    permissions:
      contents: read

    steps:

      - name: Checkout repository
        uses: actions/checkout@v3

      - name: Set up JDK 11
        uses: actions/setup-java@v3
        with:
          java-version: '11'
          distribution: 'temurin'
          cache: maven

      - name: Setup Docker buildx
        uses: docker/setup-buildx-action@v3

      - name: Install Chrome Web Browser
        run: sudo apt-get -y install google-chrome-stable

      - name: Install Chrome Web Driver
        run: selenium/manager/linux/selenium-manager --browser chrome

      - name: Launch Web Service
        run: docker-compose up -d

      - name: Run Selenium Tests
        run: cd selenium && mvn test
```

The steps are a replica of what we just did manually.

Before we commit the workflow, there is just one more thing we need to do,
and that is to make our Chrome browser headless when launched from the web
driver on the GitHub Runners.  These Runners would not have a display
attached to them so if we launch Chrome as-is, it will crash immediately
saying there is no display.  So, we need to modify D3Test.java once more to
create a headless Chrome in the @Before setUp():

```
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--headless");
    driver = new ChromeDriver(options);
```

Please do the same for ConnectTest.java, or you can choose to remove that test
entirely since it is subsumed by D3Test.  Commit and push all these changes and
the Docker CI workflow will trigger immediately.  Now, most likely, all the
workflow will be successful.  For good measure, let's try running the workflow
a few more times manually by triggering it using the "Run workflow" button.
You will notice that every so often the workflow will fail.  If you peek inside
a failing run, you will see something like this at the end of mvn test:

```
Results :

Tests in error: 
  tEST10GREETACAT(edu.pitt.cs.D3Test): unknown error: net::ERR_CONNECTION_RESET(..)
  tEST11GREETACATWITHNAME(edu.pitt.cs.D3Test): <unknown>: Failed to set the 'cookie' property on 'Document': Access is denied for this document.(..)
  tEST1LINKS(edu.pitt.cs.D3Test): <unknown>: Failed to set the 'cookie' property on 'Document': Access is denied for this document.(..)
  tEST2RESET(edu.pitt.cs.D3Test): <unknown>: Failed to set the 'cookie' property on 'Document': Access is denied for this document.(..)
  tEST3CATALOG(edu.pitt.cs.D3Test): unknown error: net::ERR_CONNECTION_RESET(..)
  tEST4LISTING(edu.pitt.cs.D3Test): unknown error: net::ERR_CONNECTION_RESET(..)
  tEST5RENTACAT(edu.pitt.cs.D3Test): <unknown>: Failed to set the 'cookie' property on 'Document': Access is denied for this document.(..)
  tEST6RENT(edu.pitt.cs.D3Test): <unknown>: Failed to set the 'cookie' property on 'Document': Access is denied for this document.(..)
  tEST7RETURN(edu.pitt.cs.D3Test): <unknown>: Failed to set the 'cookie' property on 'Document': Access is denied for this document.(..)

Tests run: 12, Failures: 0, Errors: 9, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD FAILURE
[INFO] ------------------------------------------------------------------------
```

Note that some tests failed due to net::ERR_CONNECTION_RESET and some due to
a failure in setting coockies.  And which tests suffer which errors may
change every time the workflow is run.  What's happening?  There seems to be
an issue with connecting or interacting with the web server, and it does not
seem to be an issue with the web app itself.

Let's go back to our docker-ci.yml file.  The step "docker-compose up -d"
includes an option "-d" that we didn't use before.  The "-d" option is short
for "detached" and allows docker-compose to execute detached from the
terminal so that the commandline can immediately return and continue
executing the next steps (you can try it yourself on a terminal if you wish
to).  That means that by the time you get to the Selenium tests, the
container may still be in the middle of launch.  Or, even if the container
is up, the web server may not be ready yet.  Another **race condition**!
This is exactly why the errors occurred nondeterministically, and the errors
were more likely to occur starting from the second run because by then all
the Maven packages would have been cached so that "mvn test" would not have
to download them from Maven Central, making it run faster and more likely to
overtake the web server.

Again, to solve this issue, we have to put in some kind of synchronization
to avoid the race condition.  The form of synchronization will necessarily
differ depending on the type of service you are waiting to be ready (e.g.
checking that a web server is ready will look different from checking that a
database server is ready).  I wrote a custom script for you to wait for the
web server:

```
#!/bin/sh

set -e
  
until curl http://localhost:8080/; do
  >&2 echo "Web service is unavailable - sleeping"
  sleep 1
done
  
>&2 echo "Webservice is up - continuing"
```

The curl comamndline tool fetches the page from the given URL and prints it
on the screen.  More important for our purposes, it returns an exit code of
0 if successful and a non-zero value if not (just like most Linux tools).
So the script will poll the URL every second until it can fetch the page.

Save the above script to a file named "wait-for-webserver.sh".  And insert
the invocation of that script right before running the Selenium tests.  I'll
leave it to you to name the step however you want.

After you push that change, try launching the workflow several times.  Now,
you will see the workflow reliably succeeding.  In fact, if you peek into a
workflow and look into the "wait-for-webserver.sh" step, you will see the
script waiting for the web server to start up:

<img alt="Waiting for web server to start" src=img/wait_for_webserver_1.png>

Only when the web server responds with a page does the script return and
allow the workflow to continue on to Selenium testing:

<img alt="Waiting for web server to start" src=img/wait_for_webserver_2.png>

### Add Docker Publish Workflow

Now we CI tests all set up and passing, time to create the delivery
workflow.  Create a new docker-publish.yml file with the following content:

```
name: Docker Publish

on:
  workflow_dispatch:
  release:
    types: [created]

env:
  # ghcr.io is the Docker registery maintained by GitHub
  REGISTRY: ghcr.io
  # github.repository as <account>/<repo>
  IMAGE_NAME: ${{ github.repository }}


jobs:
  build:

    runs-on: ubuntu-latest
    permissions:
      contents: read
      packages: write

    steps:
      - name: Checkout repository
        uses: actions/checkout@v3

      - name: Setup Docker buildx
        uses: docker/setup-buildx-action@v3

      # Login against a Docker registry
      # https://github.com/docker/login-action
      - name: Log into registry ${{ env.REGISTRY }}
        uses: docker/login-action@v2
        with:
          registry: ${{ env.REGISTRY }}
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      # Extract metadata (tags, labels) for Docker
      # https://github.com/docker/metadata-action
      - name: Extract Docker metadata
        id: meta
        uses: docker/metadata-action@v4
        with:
          images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}

      # Build and push Docker image with Buildx
      # https://github.com/docker/build-push-action
      - name: Build and push Docker image
        id: build-and-push
        uses: docker/build-push-action@v3
        with:
          context: .
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
          cache-from: type=gha
          cache-to: type=gha,mode=max
          platforms: linux/amd64,linux/arm64,linux/arm/v7
```

The workflow publishes the web server Docker image to ghcr.io (the GitHub
Docker image registery).  The docker/build-push-action@v3 GitHub action does
all the heavy lifting.

Once this workflow is committed and pushed, trigger it by creating a new
release on the "<> Code" tab.  Add a tag by clicking on the "Choose a tag" drop
down, such as "v1.2" (since that is the version number in the pom.xml file).
After the worflow completes, go to the "<> Code" tab and you should see a new
package in the Packages section on the bottom right.  If you click on the
package link, you should see something like the below:

<img alt="Published Docker package" src=img/docker_publish.png>

### Pull published Docker image and launch from desktop

Since your repository is private, you need to authenticate to your GitHub
repository before pulling the package.  You will use the PAT (Personal
Authentication Token) that you generated previously for this purpose.  TO do
so, you need to add permissions to access packages to the PAT.  On GitHub,
go to Account > Settings > Developer Settings > Personal Access Tokens >
Tokens (classic) to find your PAT and check write:packages (which should
automatically check read:packages as well).  If you forgot the PAT string,
click on "Regenerate Token" to obtain the string again.

Now on the commandline do:

```
docker login ghcr.io -u <github_username>
```

It is going to ask for your password and this is where you provide your PAT.
This is how the interaction should look like:

```
$ docker login ghcr.io -u wonsunahn
Password: 
Login Succeeded
```

Next, copy the "Install from the command line" text from your GitHub package
page, which was in my case:

```
docker pull ghcr.io/cs1632-spring2024/supplementary-exercise-4-ci-cd-dockers-wonsunahn:main
```

Then your commandline on the terminal.  This will pull the published image
on to your Docker Desktop.  If you check the "Images" menu, you will see a new
image created:

<img alt="Published Docker image pulled" src=img/docker_4.png>

Try removing the container you created from your locally built image (if you
haven't already), and then run the published image (taking care that you map
port 8080 in "Optional Settings").  Now if load http://localhost:8080/ on
your browser, it should work as expected.

# Submission

I expect each of you to go through this exercise and then work on tasks that
you were not able to complete together.  When you have done all the tasks
you can, please submit "Supplementary Exercise 4 Report" on GradeScope.  The
report consists of "Yes" or "No" questions on whether you were able to
complete a task and reflections.  If either one of you in a group was not
able to complete a task, please mark "No".

# Groupwork Plan

I expect each group member to experience CI/CD pipelines.  I created
individual repositories for each of you, so please work on your own
repositories to implement the pipelines.  After both of you are done,
compare the YAML files that each of you wrote.  Discuss, resolve any
differences, and submit the GitHub repository of your choice.
