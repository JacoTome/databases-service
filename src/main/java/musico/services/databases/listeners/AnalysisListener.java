package musico.services.databases.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musico.services.databases.models.Users;
import musico.services.databases.models.kafka.MusicalWorkQueryParams;
import musico.services.databases.models.kafka.UsersQueryParams;
import musico.services.databases.services.kafka.MusicalWorkQueryParamsService;
import musico.services.databases.services.kafka.UsersQueryParamsService;
import musico.services.databases.utils.DataRetriever;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.sparqlbuilder.graphpattern.GraphPatternNotTriples;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class AnalysisListener {

    private final KafkaTemplate<String, UsersQueryParams> kafkaTemplate;
    private final DataRetriever dataRetriever;
    private final MusicalWorkQueryParamsService musicalWorkQueryParamsService;
    private final UsersQueryParamsService usersQueryParamsService;

    /**
     * This method is annotated with @KafkaListener, which means it is a listener for Kafka messages.
     * It listens to the topic "analysis-query_params" and belongs to the group "databases-service".
     * The containerFactory "kafkaListenerContainerFactory" is used to create the listener container.
     * The method receives parameters that will be used to query the database and send a message for
     * each user that matches the query.
     *
     * @param message The payload of the Kafka message, which is of type MusicalWorkQueryParams.
     */
    @KafkaListener(topics = "analysis-query_params", groupId = "databases-service",
            containerFactory = "musicalWorkQueryParamsListener")
    public void listen(@Payload MusicalWorkQueryParams message) {
        // Log the received message's request ID
        log.debug("Received message from {}", message.requestId());
        try {
            // Build a query graph pattern based on the received message
            GraphPatternNotTriples query = musicalWorkQueryParamsService.buildQueryGraphPattern(message);
            // Execute the select query and iterate over the result
            for (BindingSet row : dataRetriever.createAndExecuteSelectQuery(query)) {
                // Log each row of the result
                log.debug("Row: {}", row);
                // If the user value contains "ex:Jaco", skip the current iteration
                if (row.getValue("user").toString().contains("ex")) {
                    continue;
                }
                // Build a Users object based on the user ID extracted from the row
                Users user = Users.builder()
                        .userId(Integer.parseInt(row.getValue("user").stringValue().split("/")[row.getValue("user").stringValue().split("/").length - 1]))
                        .build();
                // Execute a select query based on the user and get the result
                List<BindingSet> users = dataRetriever.createAndExecuteSelectQuery(user.buildGenericQueryGraphPattern(user));
                // Build a response message based on the users and the original message's request ID
                UsersQueryParams response = usersQueryParamsService.getResponseMessageFromQueryResults(users).requestID(message.requestId()).build();

                // Log the response message
                log.debug("Response: {}", response);
                // Send the response message to the "analysis-query_params_response" topic
                kafkaTemplate.send("analysis-query_params_response", response);
            }
        } catch (Exception e) {
            // Log the error message and the stack trace if an exception occurs
            log.error("Error: {}", e.getMessage());
            Arrays.asList(e.getStackTrace()).forEach(
                    stackTraceElement -> log.error("Error: {}", stackTraceElement.toString())
            );
        }
    }
}
