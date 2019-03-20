/*
 * 
 *  Copyright (c) 2015 SONATA-NFV, 2017 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 *  ALL RIGHTS RESERVED.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *  
 *  Neither the name of the SONATA-NFV, 5GTANGO [, ANY ADDITIONAL AFFILIATION]
 *  nor the names of its contributors may be used to endorse or promote
 *  products derived from this software without specific prior written
 *  permission.
 *  
 *  This work has been performed in the framework of the SONATA project,
 *  funded by the European Commission under Grant number 671517 through
 *  the Horizon 2020 and 5G-PPP programmes. The authors would like to
 *  acknowledge the contributions of their colleagues of the SONATA
 *  partner consortium (www.sonata-nfv.eu).
 *  
 *  This work has been performed in the framework of the 5GTANGO project,
 *  funded by the European Commission under Grant number 761493 through
 *  the Horizon 2020 and 5G-PPP programmes. The authors would like to
 *  acknowledge the contributions of their colleagues of the 5GTANGO
 *  partner consortium (www.5gtango.eu).
 * 
 */

package eu.tng.messaging;

import java.io.IOException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.JSONException;
import org.json.JSONObject;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.CancelCallback;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Envelope;

public class MqSlaViolationsConsumer implements ServletContextListener {

    private static final String EXCHANGE_NAME = System.getenv("BROKER_EXCHANGE");
    //private static final String EXCHANGE_NAME = "son-kernel";

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        System.out.println("Listener SLA Violations stopped");
        
    }

    @Override
    public void contextInitialized(ServletContextEvent arg0) {
        final Channel channel_violations;
        Connection connection;
        String queueName_sla_violations;

        try {
            RabbitMqConnector connect = new RabbitMqConnector();
            connection = connect.getconnection();

            channel_violations = connection.createChannel();
            channel_violations.exchangeDeclare(EXCHANGE_NAME, "topic");
            queueName_sla_violations = "slas.tng.sla.violation";
            channel_violations.queueDeclare(queueName_sla_violations, true, false, false, null);
            System.out.println(" [*]  Binding queue to topic...");
            channel_violations.basicQos(1);
            channel_violations.queueBind(queueName_sla_violations, EXCHANGE_NAME, "tng.sla.violation");
            System.out.println(" [*] Bound to topic \"tng.sla.violation\"");
            System.out.println(" [*] Waiting for messages.");

            DeliverCallback deliverCallback = new DeliverCallback() {
                @Override
                public void handle(String consumerTag, Delivery delivery) throws IOException {

                    JSONObject jmessage = null;                  
                    try {
                        String message = new String(delivery.getBody(), "UTF-8");
                        jmessage = new JSONObject(message);
                        System.out.println("VIOLATION MESSAGE " + jmessage);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        System.out.println("ERROR: " + e.getMessage());
                    }
                    channel_violations.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                }

            };

            // consumer
            channel_violations.basicConsume(queueName_sla_violations, false, deliverCallback, new CancelCallback() {
                @Override
                public void handle(String consumerTag) throws IOException { }
            });

        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.out.println("ERROR Connecting to MQ!" + e.getMessage());
        }
    }

}
