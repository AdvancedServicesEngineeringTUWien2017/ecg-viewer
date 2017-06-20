# ECGViewer Webapp
This application was generated using JHipster 4.5.2.

## Used technologies

This server uses a standard spring boot stack with JWT based Authorization. Live ECG data is collected via a RabbitMQ Broker.
Archive ECG and user data is saved using a MongoDB Cluster. The Frontend is built with Angularjs and chart.js.
Sending data from the server to the client (live ECG) is done using Websockets. 

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.
2. [Yarn][]: We use Yarn to manage Node dependencies.
   Depending on your system, you can install Yarn either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

    yarn install

We use [Gulp][] as our build system. Install the Gulp command-line tool globally with:

    yarn global add gulp-cli

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./mvnw
    gulp

[Bower][] is used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [bower.json](bower.json). You can also run `bower update` and `bower install` to manage dependencies.
Add the `-h` flag on any command to see how you can use it. For example, `bower update -h`.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].


## Building for production

To optimize the ECGViewer application for production, run:

    ./mvnw -Pprod clean package

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
Before running the application you have to set the following environment variables:
-     MONGOURI ==> the ConnectionUri for your mongodb
-     RABBITPW ==> the Password for your RabbitMQ Broker
-     JWTSECRET ==> the JWTSecret you want to use for JWT issuing
-     RABBITUSER ==> the user for your RabbitMQ Broker
-     RABBITHOST ==> the host of your RabbitMQ Broker
-     RABBITVHOST ==> the virtual host you are using for your RabbitMQ Broker
Then run the application simply using:

    java -jar target/*.war

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

## Using Docker

The application is deployed using docker, although it can run natively as well.
To generate the docker image, use the Dockerfile in ```./src/main/docker```. 

Before building the image you have to adjust the file according to the Environment variables
described in the previous section and copy the generated ```war``` file from the target folder and the dockerfile in one directory.
After adjusting the file you can build the image with:

    docker build -t ecgviwer/webapp .
    
To run the container you can use:
    
    docker run -p 8080:8080 ecgviwer/webapp


