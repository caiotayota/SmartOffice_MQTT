package com.caiotayota.smartoffice;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttSubscriber2 {

    public static void main(String[] args) {

        // Define the MQTT broker URL and client ID for this subscriber
        String broker = "tcp://localhost:1883";
        String clientId = "Subscriber2";

        // Use in-memory persistence for storing messages temporarily
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            // Initialize the MqttClient with the broker URL, client ID, and persistence strategy
            MqttClient sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connectOptions = new MqttConnectOptions();

            // Set connection options: clean session and keep-alive interval
            connectOptions.setCleanSession(true);
            connectOptions.setKeepAliveInterval(180);

            // Set the callback for handling messages and connection loss
            sampleClient.setCallback(new MqttMessageHandler());

            // Output connection status to the console
            System.out.println("... Connecting to broker " + broker + " ...");

            // Connect the client to the MQTT broker using the specified options
            sampleClient.connect(connectOptions);
            System.out.println("* Connected! *");

            // Subscribe to all topics under "floor/room/" using the wildcard "#"
            sampleClient.subscribe("floor/room/#");

            // Keep the application running to listen for messages
            System.out.println("Listening for messages on 'floor/room/#'...");

        } catch (MqttException e) {
            // Handle exceptions by printing detailed information to the console
            System.out.printf(
                    "Reason: %s%n" +
                            "Message: %s%n" +
                            "Local-msg: %s%n" +
                            "Cause: %s%n",
                    e.getReasonCode(), e.getMessage(), e.getLocalizedMessage(), e.getCause());
            e.printStackTrace();
        }
    }
}
