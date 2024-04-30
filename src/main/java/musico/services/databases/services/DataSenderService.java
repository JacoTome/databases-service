package musico.services.databases.services;

import musico.services.databases.models.Users;
import musico.services.databases.utils.DataGenerator;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class DataSenderService {
    private static final Logger log = LoggerFactory.getLogger(DataSenderService.class);

    private final KafkaTemplate<String, String> producer;

    private static DataGenerator ttlGen = new DataGenerator();

    @Autowired
    public DataSenderService(KafkaTemplate<String, String> producer) {
        this.producer = producer;
        ttlGen = new DataGenerator();
    }

    public void sendUser(Users user) {
        try {
            List<String> data = ttlGen.genUserData(user);
            String fullData = String.join("", data);
            CompletableFuture<SendResult<String, String>> future = producer.send(new ProducerRecord<>("gdb-add", fullData));
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Error sending user data: {}", ex.getMessage());
                } else {
                    log.info("User data sent successfully: {}", result);
                }
            });
        } catch (IllegalAccessException e) {
            log.error("Error sending user data: {}", e.getMessage());
        }
    }
}
