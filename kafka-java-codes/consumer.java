package kafkacon;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONException;
import org.json.JSONObject;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException, JSONException {
		
		Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.137.155:9092,192.168.137.134:9092,192.168.137.236:9092,192.168.137.63:9092,192.168.137.176:9092");
		props.put("group.id", "consumerProducer");
	    	props.put("enable.auto.commit", "false");
	    	props.put("auto.commit.interval.ms", "1000");
	    	props.put("auto.offset.reset", "latest");
	    	props.put("session.timeout.ms", "30000");
	    	props.put("heartbeat.interval.ms", "6000");
	    	//props.put("request.timeout.ms", "80000");
	    	//props.put("group.max.session.timeout.ms", "600000");
	    	props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	    	props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	    	String topic1 = "node1";
	    	String topic2 = "node2";
	    	String topic3 = "node3";
	    	String topic4 = "node4";
	    	String topic5 = "node5";
	    
	    	Properties propsProducer = new Properties();
	    	propsProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "86.50.168.167:9092,86.50.169.10:9092");
	    	propsProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
	    	propsProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
	    	String topicP = "cameraData";
		 
	    	@SuppressWarnings("resource")
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
		consumer.subscribe(Arrays.asList(topic1, topic2, topic3, topic4, topic5));
		
		@SuppressWarnings("resource")
		KafkaProducer<String,String> producer = new KafkaProducer<String,String>(propsProducer);
		
	    	int i = 1;
		while (true) {
		
			ConsumerRecords<String, String> records = consumer.poll(10000);
			if (!records.isEmpty()) 
			{	
				for (ConsumerRecord<String, String> record : records)
				{
					/*String name = "/data/img" + i + ".txt";
					i = i + 1;
					File filetxt = new File(name);
					if (!filetxt.exists()) {
						filetxt.createNewFile();
					}
					
					FileWriter fw = new FileWriter(filetxt.getAbsoluteFile());
					BufferedWriter bw = new BufferedWriter(fw);*/
					
					String name = "/data/img" + i + ".jpg";
					JSONObject jObject = new JSONObject(record.value());
            				String img_v = jObject.getString("value");
            		
            				String key = Long.toString(System.currentTimeMillis());
					ProducerRecord<String, String> data = new ProducerRecord<String, String>(topicP, key, record.value());
					producer.send(data);
					
					FileOutputStream imageOutFile = new FileOutputStream(name);
					byte[] imageData = Base64.decodeBase64(img_v);
					imageOutFile.write(imageData);
					
					imageOutFile.close();
					i = i + 1;
					
					//bw.write(record.value());
					//bw.close();
					Thread.sleep(1000);
				}
			}
		
			Thread.sleep(2000);
		}
	}
}


/*package kafkacon;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.json.JSONException;
import org.json.JSONObject;

public class Main {
	public static void main(String[] args) throws IOException, InterruptedException, JSONException {
		
		Properties propsConsumer = new Properties();
		propsConsumer.put("bootstrap.servers", "192.168.137.94:9092,192.168.137.166:9092,192.168.137.125:9092,192.168.137.237:9092,192.168.137.168:9092");
		propsConsumer.put("group.id", "test");
		propsConsumer.put("auto.offset.reset", "earliest");
		propsConsumer.put("enable.auto.commit", "true");
		propsConsumer.put("auto.commit.interval.ms", "1000");
		propsConsumer.put("session.timeout.ms", "30000");
		propsConsumer.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		propsConsumer.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
	    String topicC = "master2";
	    
	    Properties propsProducer = new Properties();
	    propsProducer.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "86.50.168.167:9092,86.50.169.10:9092,86.50.168.167:9092");
	    propsProducer.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
	    propsProducer.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
	    String topicP = "camera2";
		 
	    @SuppressWarnings("resource")
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(propsConsumer);
		consumer.subscribe(Arrays.asList(topicC));
		
		@SuppressWarnings("resource")
		KafkaProducer<String,String> producer = new KafkaProducer<String,String>(propsProducer);
		
	    int i = 1;
		while (true) {
			
			ConsumerRecords<String, String> records = consumer.poll(10000);
			if (!records.isEmpty()) {
				
				for (ConsumerRecord<String, String> record : records)
				{
					String name = "/data/img" + i + ".jpg";
					JSONObject jObject = new JSONObject(record.value());
            		String img_v = jObject.getString("value");
				
					
					String key = Long.toString(System.currentTimeMillis());
					ProducerRecord<String, String> data = new ProducerRecord<String, String>(topicP, key, record.value());
					producer.send(data);
					producer.flush();
					FileOutputStream imageOutFile = new FileOutputStream(name);
					byte[] imageData = Base64.decodeBase64(img_v);
					imageOutFile.write(imageData);
					
					imageOutFile.close();
					i = i + 1;
				}
			}
		
			Thread.sleep(1000);
		}
	}
}*/
