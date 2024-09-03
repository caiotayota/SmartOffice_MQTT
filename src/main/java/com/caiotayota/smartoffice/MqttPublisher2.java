package com.caiotayota.smartoffice;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MqttPublisher2 {

    // MqttClient instance to manage the connection and message publishing
    private static MqttClient sampleClient;

    public static void main(String[] args) throws InterruptedException {

        // Define the MQTT broker URL and the client ID for this publisher
        String broker = "tcp://localhost:1883";
        String clientId = "Publisher2";

        // Use in-memory persistence for storing message data temporarily
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            // Initialize the MqttClient with the broker URL, client ID, and persistence strategy
            sampleClient = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connectOptions = new MqttConnectOptions();

            // Set connection options: clean session and keep-alive interval
            connectOptions.setCleanSession(true);
            connectOptions.setKeepAliveInterval(180);

            // Connect the client to the MQTT broker using the specified options
            sampleClient.connect(connectOptions);

            // Output connection status to the console
            System.out.println("... Connecting to broker " + broker + " ...");
            System.out.println("* Connected! *");

            // Publish 60 messages at 500ms intervals
            for (int i = 0; i < 60; i++) {

                // Randomly generate a light status ("ON" or "OFF")
                String light = Math.random() < 0.5 ? "OFF" : "ON";
                publishMessage("floor/light/ID", light, 1, false);

                // Randomly generate a window status ("OPEN" or "CLOSE")
                String window = Math.random() < 0.5 ? "CLOSE" : "OPEN";
                publishMessage("floor/window/location", window, 1, false);

                // Pause for 500ms before sending the next set of messages
                Thread.sleep(500);
            }

            // Disconnect the client from the broker
            sampleClient.disconnect();
            System.out.println("* Disconnected *");

            // Close the client to release resources
            sampleClient.close();

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

    // Method to publish a message to a specified MQTT topic
    private static void publishMessage(String topic, String payload, int qos, boolean retained) {

        // Determine whether the message is for light or window and format the output
        String sensor = topic.endsWith("ID") ? "Light: " : "Window: ";
        System.out.print(topic.endsWith("ID") ? "\n" : "");
        String time = "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] - ";

        // Output the time, sensor type, and payload to the console
        System.out.println(time + sensor + payload);

        // Create an MQTT message with the given payload and set its properties
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setRetained(retained);
        message.setQos(qos);

        try {
            // Publish the message to the specified topic
            sampleClient.publish(topic, message);
        } catch (MqttPersistenceException e) {
            // Handle persistence exceptions with detailed output
            System.out.printf("Reason Code: %s%n" +
                            "Message: %s%n" +
                            "Local-msg: %s%n" +
                            "Cause: %s%n",
                    e.getReasonCode(), e.getMessage(), e.getLocalizedMessage(), e.getCause());
            e.printStackTrace();
        } catch (MqttException e) {
            // Handle general MQTT exceptions by rethrowing them as runtime exceptions
            throw new RuntimeException(e);
        }
    }
}
