package musico.services.databases.listeners;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musico.services.databases.models.kafka.MusicalWorkQueryParams;
import musico.services.databases.utils.DataRetriever;
import org.eclipse.rdf4j.query.BindingSet;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@AllArgsConstructor
public class QueryParamsListener {

    private final DataRetriever dataRetriever;

    /**
     * 1. Message Received
     * 2. Create Query with the received message
     * 3. Execute Query
     * 4. Return the result
     */
    @KafkaListener(topics = "query_params", groupId = "databases-service",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(@Payload MusicalWorkQueryParams message) {
        log.info("Received message: {}", message);
        List<BindingSet> result = dataRetriever.getMusicalWorkData(message);
        if (result.isEmpty()){
            log.info("No data found for the given query");
        } else {
            for (BindingSet bind:result) {
                log.info("Data found: {}", bind);
            }
        }
    }
}
