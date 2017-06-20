# Sensor
This Sensor implementation is a custom version of the iCOMOTSensor (https://github.com/tuwiendsg/iCOMOTSensors); 
This Sensor reads data from the specified file and sends it to the specified RabbitMQ Broker.

To run this sensor:
- build the mevan projects in their respective subfolders (sdcloudconnectivity, sdcommon and sdsensor)
- copy the generated jars sdsensor-0.0.1-SNAPSHOT-jar-with-dependencies.jar, sdcommon-0.0.1-SNAPSHOT.jar and cloud-connectivity-0.0.1-SNAPSHOT-jar-with-dependencies.jar into a directory of your choice
- copy the files from ./sdsensor/bin, ./sdsensor/datasample and ./sdsensor/conf to the same folder
- alter mqttcloud.json, so that it can connect to your RabbitMQ instance.
- start the sensors with start_sensor_1.sh, respectively start_sensor_2.sh