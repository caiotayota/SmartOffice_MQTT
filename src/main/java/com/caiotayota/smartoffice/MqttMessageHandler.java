package com.caiotayota.smartoffice;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Arrays;

public class MqttMessageHandler implements MqttCallback {

    /**
     * This method is called when the connection to the MQTT broker is lost.
     * It can be triggered by network issues or the broker shutting down.
     *
     * @param throwable the reason for the connection loss, containing stack trace information.
     */
    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("The connection is lost!\n" + Arrays.toString(throwable.getStackTrace()));
    }

    /**
     * This method is called when a message arrives from the broker.
     * It handles the received message, which includes the topic it was published on, the message payload, and the QoS level.
     *
     * @param topic   the topic on which the message was published.
     * @param message the actual message received from the broker.
     * @throws Exception if an error occurs while processing the message.
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // Print the message payload, the topic it arrived on, and the QoS level
        System.out.println(message + " arrived from topic " + topic + " with QoS: " + message.getQos());
    }

    /**
     * This method is called when the delivery of a message to the broker is complete.
     * Although typically used by publishers to confirm message delivery, it's required by the MqttCallback interface.
     *
     * @param token the delivery token associated with the message.
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        // Confirm that the delivery of the message is complete
        System.out.println("Delivery is complete! " + token.isComplete());
    }
}
