# ECGViewer
Mini Project for Advanced Services Engineering

This project shows and processes live ECG data. 
This data is also saved and can then be viewed or downloaded form an archive.

An overview of the communiaction can be seen in: 
	
	implementation.png
For comparison, also the planned architecture can be seen in:

	planned_implementation.png

## Components
The Project consists of five components:

- Simulated sensor, located in the subfolder 'sensor'
- A Flink Processing Stream, located in the subfolder 'flink-stream'
- A Spring Boot server with an angularjs GUI located in the subfolder 'webapp'
- A RabbitMQ Broker hosted on https://www.cloudamqp.com/
- A MongoDB Cluster hosted on https://www.cloud.mongodb.com/

To build the individual Components, please refer to the readmes in the subfolders.

### Webapp


The Webapp runs on a AWS t2.micro instance using docker. And can be accessd via:

	http://ec2-54-93-59-85.eu-central-1.compute.amazonaws.com:8080/

Run the Service on the AWS instance:
- Create AWS t2.micro instance
- add rule for tcp 8080 to security group on instance
- install docker
- upload the created .war file via scp
- upload the DockerFile and adjust it accordingly
- build the Docker image ```docker build -t ecgviewer/webapp```
- run the docker container ```docker run -d -p 8080:8080 ecgviewer/webapp```

### Flink Stream
The Flink dashboard of the stream runs on a AWS t2.large instance using a one node cluster. The dashboard can be accessed via:
    
    http://ec2-54-93-72-22.eu-central-1.compute.amazonaws.com:8081/

Run the Service on the AWS instance:
- Create AWS t2.large instance
- add rule for tcp 8081 to security group on instance
- install java jdk
- set JAVA_HOME environment variable
- install Flink 1.3.0
    - https://flink.apache.org/downloads.html
- upload the built .jar file (with dependencies) via scp
- start Flink cluster via ```./bin/start-cluster.sh```
- start stream job ```./bin/flink run -d cluster-1.0-SNAPSHOT-jar-with-dependencies.jar```

### Sensor
Sensors are started locally using the instructions given in the respective readme.

### MongoDB and RabbitMQ Broker
When using the previously proposed services, there is no need to start these components, because they are already running
