# ECGViewer Flink-Stream

This application is the stream processing part of the ECGViewer project.

It uses Flink stream processing and Flink CEP to calculate the heartrate and the
quality of incoming ECG signals. The ECG data then is stored in the mongodb used by
this project. The heartrate is sent to the webapp for diagnostic reasons of the live ECG.

## Requirements
- java 8
- maven >= 3.3.9

## Build

To build this project simply use:

    maven clean install
    
## Run Local

To run this Flink project in local mode download flink binaries in version 1.3.0 and start a local environment
using 

    ./bin/start-local.sh
After the startup of the local Flink instance you can start the Stream with:

    ./bin/flink run <path-to-the-project>/target/cluster-1.0-SNAPSHOT-jar-with-dependencies.jar

The following parameters are required when starting the Flink stream:
- --host ==> rabbitmq host 
- --user ==> rabbitmq user
- --password ==> rabbitmq password
- --port ==> rabbitmq port
- --virtualhost rabbitmq virtualhost
- --queuename ==> queuename for ecg data (standard:"ecg_data_cluster")
- --heartqueuename ==> queuename for heart rate data  (standard:"ecg_heart_rate")
- --mongouri ==> connectionUri for mongodb

The Stream should startup and connect automatically

## Run in cluster
Please refer to the official tutorials: 
- https://ci.apache.org/projects/flink/flink-docs-release-0.8/cluster_setup.html
- https://aws.amazon.com/de/blogs/big-data/use-apache-flink-on-amazon-emr/
