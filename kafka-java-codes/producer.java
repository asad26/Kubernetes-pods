package kafka;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FilenameUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.PropertyConfigurator;

import kafka.admin.AdminUtils;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;

public class Main {
	
    public static void main(String[] args) throws IOException, InterruptedException {
    	
    	String log4jConfPath = "/opt/log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
		String topicP = null;
		boolean flag = true;
		while (flag)
		{
			try {
				String zookeeperConnect = "192.168.137.155:2181,192.168.137.134:2181,192.168.137.236:2181,192.168.137.63:2181,192.168.137.176:2181";

		        ZkClient zkClient = new ZkClient(zookeeperConnect, 10000, 8000, ZKStringSerializer$.MODULE$);
		       
		        boolean isSecureKafkaCluster = false;
		       
		        ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperConnect), isSecureKafkaCluster);

		        String topic = "node5";
		        topicP = topic;
		        int partitions = 1;
		        int replication = 3;

		        Properties topicConfig = new Properties();

		        AdminUtils.createTopic(zkUtils, topic, partitions, replication, topicConfig);
		        zkClient.close();
		        flag = false;
			} catch (Exception e){}
		
		}
		
        Properties props = new Properties();
		props.put("bootstrap.servers", "192.168.137.155:9092,192.168.137.134:9092,192.168.137.236:9092,192.168.137.63:9092,192.168.137.176:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		@SuppressWarnings("resource")
		Producer<String, String> producer = new KafkaProducer<>(props);
		
		String folderpath = "/camera5/";
		File folder = new File(folderpath);
		
		final String s1 = "{\"id\":\"C5";
		final String s2 = "\",\"date\":\"";
		final String s3 = "\",\"time\":\"";
		final String s4 = "\",\"value\":\"";
		final String s5 = "\"}";
		
		while (true) {
			
			if (folder.listFiles().length != 0) {
				for (File f : folder.listFiles()) {
					File file = new File(f.getAbsolutePath());
					String[] result = file.getName().split("-");
					String time = FilenameUtils.removeExtension(result[1]);
					FileInputStream imageInFile = new FileInputStream(file);
					byte imageData[] = new byte[(int) file.length()];
					imageInFile.read(imageData);
					String imageDataString = Base64.encodeBase64URLSafeString(imageData);
					String value = imageDataString;
					String finalJSON = s1 + s2 + result[0] + s3 + time + s4 + value + s5;
					String key = Long.toString(System.currentTimeMillis());
					ProducerRecord<String, String> producerRecord = new ProducerRecord<String, String>(topicP, key, finalJSON);
					producer.send(producerRecord);
					imageInFile.close();
					f.delete();
					Thread.sleep(1000);
				}
			}	
			Thread.sleep(2000);
		}
    }
}
