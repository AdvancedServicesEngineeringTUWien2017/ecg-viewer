package at.ac.tuwien.dsg.sensor;

import at.ac.tuwien.dsg.cloudconnectivity.rabbitmq.RabbitMQProducer;
import at.ac.tuwien.dsg.common.sdapi.RefreshableProducerDelegate;
import at.ac.tuwien.dsg.common.sdapi.RefreshableSchedulerDelegat;
import at.ac.tuwien.dsg.common.sdapi.SchedulerSettings;


public class SimpleCommandLineGenericSensor {

    public static void main(String[] args) {

        GenericSensor sensor = new GenericSensor();
        // sensor.localInitilization();
        RefreshableSchedulerDelegat schedulerDelegate = new RefreshableSchedulerDelegat();
        RefreshableProducerDelegate producerDelegate = new RefreshableProducerDelegate();
        // provide a page for a mqtt protocol
        RabbitMQProducer protocol = new RabbitMQProducer(args[0]);
        producerDelegate.setProtocol(protocol);
        // provide dataset
        sensor.setDataFileSource(args[1]);
        SchedulerSettings setting = new SchedulerSettings();
        setting.setUpdateRate(1);
        schedulerDelegate.setSettings(setting);
        sensor.setProducerDelegate(producerDelegate);
        sensor.setSchedulerDelegate(schedulerDelegate);
        sensor.start();
    }
}
