# consumer-producer
* This is a kafka spring boot application which listen

### Program Arguments
* -Dspring.application.name=CONSUMER-PRODUCER-SERVICE

### Confluent Kafka Commands
* confluent local services start
* confluent local services stop
* confluent local destroy

### Producer Commands
* kafka-topics.sh --zookeeper localhost:2181 --list
* kafka-topics.sh --create --topic event-input -zookeeper localhost:2181 --replication-factor 1 --partitions 1
* kafka-console-producer.sh --broker-list localhost:9092 --topic event-input
# Sample Data
{"bookId": 1, "bookName": "Kafka", "bookAuthor": "Sameer", "isGood": true}

### Consumer Commands
* kafka-topics.sh --create --topic event-output -zookeeper localhost:2181 --replication-factor 1 --partitions 1
* kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic event-output

### Delete topics
* kafka-topics.sh --zookeeper localhost:2181 --delete --topic event-input
* kafka-topics.sh --zookeeper localhost:2181 --delete --topic event-output

