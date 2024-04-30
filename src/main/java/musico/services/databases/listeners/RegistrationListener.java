package musico.services.databases.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import musico.services.auth.dto.AuthKafkaDTO;
import musico.services.databases.enums.REGISTRATION_ENUMS;
import musico.services.databases.services.RegistrationService;
import musico.services.databases.services.UserService;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Slf4j
@Component
@RequiredArgsConstructor
@KafkaListener(topics = "registration")
public class RegistrationListener {

    private final RegistrationService registrationService;

    @KafkaHandler
    @SendTo
    public Message<AuthKafkaDTO> listen(AuthKafkaDTO userSignup) {

        log.info("Received request: {}", userSignup.getId());
        REGISTRATION_ENUMS check = registrationService.checkSignup(userSignup.getUsername(), userSignup.getEmail());
        if (check != REGISTRATION_ENUMS.CHECK_VALID) {
            userSignup.setERROR_CODE(check.getValue());
            return MessageBuilder.withPayload(userSignup).build();
        }
        REGISTRATION_ENUMS register = registrationService.registerUser(userSignup);
        if (register != REGISTRATION_ENUMS.REGISTRATION_SUCCESS) {
            userSignup.setERROR_CODE(register.getValue());
            return MessageBuilder.withPayload(userSignup).build();
        }
        return MessageBuilder.withPayload(userSignup).build();
    }
    

}
