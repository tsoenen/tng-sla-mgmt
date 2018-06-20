package eu.tng.messaging;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.codehaus.jettison.json.JSONObject;

import com.esotericsoftware.yamlbeans.YamlReader;
import com.google.gson.Gson;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eu.tng.correlations.cust_sla_corr;
import eu.tng.correlations.db_operations;

/**
 * Application Lifecycle Listener implementation class TestListener2
 *
 */
public class RabbitMqConsumer implements ServletContextListener {

	private final static String QUEUE_NAME = "service.instance.create";

	/**
	 * Default constructor.
	 */
	public RabbitMqConsumer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("Server stopped");
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("Server started");
		RabbitMqConnector connect = new RabbitMqConnector();

		try {
			Connection connection = connect.MqConnector();
			Channel channel = connection.createChannel();

			channel = connection.createChannel();
			channel.queueDeclare(QUEUE_NAME, true, false, false, null);
			//channel.queueDeclare(QUEUE_NAME, false, false, false, null);			
			
			System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

			Consumer consumer = new DefaultConsumer(channel) {
				@Override
				public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
						byte[] body) throws IOException {

					// Initialize variables
					String sla_uuid = null;
					String ns_uuid = null;
					String ns_name = null;
					String cust_uuid = null;
					String cust_email = null;
					String sla_name = null;
					String sla_status = null;
					String correlation_id = null;
					String status = null;

					// Parse headers
					HashMap<String, Object> headers = (HashMap<String, Object>) properties.getHeaders();
					for (Map.Entry<String, Object> header : headers.entrySet()) {
						if (header.getKey().equals("correlation_id")) {
							correlation_id = header.getValue().toString();
							System.out.println("correlation_id ==> " + correlation_id);
						}
					}

					// Parse message payload
					String message = new String(body, "UTF-8");
					YamlReader reader = new YamlReader(message);
					Object object = reader.read();
					Map map = (Map) object;
					// Construct a JSONObject from a Map.
					JSONObject jmessage = new JSONObject(map);

					// Get instntiation request status
					try {
						status = (String) jmessage.get("status");
					} catch (Exception e) {	}

					// if message coming from the GK
					if (status == null) {
						System.out.println("Message from  GK received");

						// Get nsd data
						try {
							Object nsd = jmessage.get("NSD");
							Gson gson = new Gson();
							String nsdInString = gson.toJson(nsd);
							JSONObject jsonNsd = new JSONObject(nsdInString);
							ns_name = (String) jsonNsd.get("name");
							ns_uuid = (String) jsonNsd.get("uuid");

							System.out.println(" NS NAME ==> " + ns_name);
							System.out.println(" NS UUID ==> " + ns_uuid);

						} catch (Exception e) {
							e.printStackTrace();
						}

						// Parse customer data + sla uuid
						try {
							Object user_data = jmessage.get("user_data");
							Gson gson = new Gson();
							String user_dataInString = gson.toJson(user_data);
							JSONObject jsonNsd = new JSONObject(user_dataInString);
							JSONObject customer = (JSONObject) jsonNsd.get("customer");
							cust_uuid = (String) customer.get("uuid");
							cust_email = (String) customer.get("email");
							sla_uuid = (String) customer.get("sla_id");

							System.out.println(" Cust id  ==> " + cust_uuid);
							System.out.println("Cust email  ==> " + cust_email);
							System.out.println("SLA uuid  ==> " + sla_uuid);

						} catch (Exception e) {
							e.printStackTrace();
						}

						cust_sla_corr cust_sla = new cust_sla_corr();
						@SuppressWarnings("unchecked")
						ArrayList<String> SLADetails = cust_sla.getSLAdetails(sla_uuid);
						sla_name = (String) SLADetails.get(1);
						sla_status = (String) SLADetails.get(0);

						System.out.println("SLA name  ==> " + sla_name);
						System.out.println("SLA status  ==> " + sla_status);
						
						String inst_status = "PENDING";

						db_operations.connectPostgreSQL();
						cust_sla_corr.createCustSlaCorr(sla_uuid, sla_name, sla_status, ns_uuid, ns_name, cust_uuid,cust_email, inst_status, correlation_id);

					} 
					// if message coming from the MANO
					else {
						System.out.println("Message from  MANO received");
						System.out.println("status ==> " + status);
						db_operations dbo = new db_operations();
						db_operations.connectPostgreSQL();
						db_operations.UpdateRecordAgreement(status, correlation_id);

					}

				}
			};
			channel.basicConsume(QUEUE_NAME, true, consumer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
