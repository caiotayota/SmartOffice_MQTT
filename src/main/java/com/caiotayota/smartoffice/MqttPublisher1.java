package com.caiotayota.smartoffice;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class MqttPublisher1 {

    // Define constants for MQTT broker details, client ID, and connection settings
    private static final String BROKER_URL = "tcp://localhost:1883";
    private static final String CLIENT_ID = "Publisher1";
    private static final int KEEP_ALIVE_INTERVAL = 180;
    private static final int MESSAGE_COUNT = 60; // Number of messages to publish
    private static final int SLEEP_INTERVAL_MS = 1000; // Interval between messages in milliseconds

    // MqttClient instance for managing the connection and publishing messages
    private MqttClient client;

    // Constructor: Initializes the MqttClient and connects to the broker
    public MqttPublisher1() throws MqttException {
        this.client = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());
        connect();
    }

    // Method to connect the client to the MQTT broker
    private void connect() throws MqttException {
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true); // Ensure a clean session on connect
        connectOptions.setKeepAliveInterval(KEEP_ALIVE_INTERVAL); // Set keep-alive interval
        client.connect(connectOptions); // Connect to the broker
        System.out.println("... Connecting to broker " + BROKER_URL + " ...");
        System.out.println("* Connected! *");
    }

    // Method to disconnect the client from the broker
    private void disconnect() throws MqttException {
        if (client.isConnected()) {
            client.disconnect(); // Disconnect from the broker
            System.out.println("* Disconnected *");
        }
    }

    // Method to close the client and release resources
    public void close() throws MqttException {
        if (client != null) {
            client.close(); // Close the client
        }
    }

    // Method to publish multiple messages in a loop
    public void publishMessages() throws InterruptedException, MqttException {
        for (int i = 0; i < MESSAGE_COUNT; i++) {
            // Generate a random temperature between 20 and 29 degrees Celsius
            int temperature = ThreadLocalRandom.current().nextInt(20, 30);
            publishMessage("floor/room/temperature", temperature + "°C");

            // Generate a random humidity level between 15% and 24%
            int humidity = ThreadLocalRandom.current().nextInt(15, 25);
            publishMessage("floor/room/humidity", humidity + "%");

            // Pause for 1 second before sending the next set of messages
            Thread.sleep(SLEEP_INTERVAL_MS);
        }
    }

    // Method to publish a single message to a specified MQTT topic
    private void publishMessage(String topic, String payload) {
        try {
            // Determine whether the message is related to temperature or humidity
            String sensorType = topic.endsWith("temperature") ? "Temperature: " : "Humidity: ";
            String time = "[" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "] - ";

            // Output the message details to the console
            System.out.println(time + sensorType + payload);

            // Create and configure the MQTT message
            MqttMessage message = new MqttMessage(payload.getBytes());
            message.setQos(1); // Set Quality of Service level to 1
            message.setRetained(false); // Do not retain the message on the broker

            // Publish the message to the specified topic
            client.publish(topic, message);
        } catch (MqttException e) {
            // Handle any exceptions that occur during message publishing
            System.err.printf("Failed to publish message to topic '%s': %s%n", topic, e.getMessage());
            e.printStackTrace();
        }
    }

    // Main method: Entry point of the application
    public static void main(String[] args) {
        MqttPublisher1 publisher = null;
        try {
            // Initialize the publisher and start publishing messages
            publisher = new MqttPublisher1();
            publisher.publishMessages();
        } catch (MqttException | InterruptedException e) {
            // Handle any errors that occur during the operation
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure the client is disconnected and closed, even if an error occurs
            if (publisher != null) {
                try {
                    publisher.disconnect();
                    publisher.close();
                } catch (MqttException e) {
                    // Handle any errors that occur during cleanup
                    System.err.println("Failed to clean up MQTT resources: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
}
