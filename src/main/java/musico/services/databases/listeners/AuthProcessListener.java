package musico.services.databases.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import musico.services.user.dto.AuthProcessDTO;
import musico.services.databases.enums.REGISTRATION_ENUMS;
import musico.services.databases.services.AuthProcessService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;


@Slf4j
//@Component
@RequiredArgsConstructor
public class AuthProcessListener {

    private final AuthProcessService authProcessService;

//    @KafkaListener(topics = "registration")
//    @SendTo
//    public Message<AuthProcessDTO> listenRegistration(AuthProcessDTO userSignup) {
//        log.info("Received registration request: {}", userSignup.toString());
//        REGISTRATION_ENUMS check = authProcessService.checkSignup(userSignup.getUsername(), userSignup.getEmail());
//        if (check != REGISTRATION_ENUMS.CHECK_VALID) {
//            userSignup.setERROR_CODE(check.getValue());
//            return MessageBuilder.withPayload(userSignup).build();
//        }
//        REGISTRATION_ENUMS register = authProcessService.registerUser(userSignup);
//        if (register != REGISTRATION_ENUMS.REGISTRATION_SUCCESS) {
//            userSignup.setERROR_CODE(register.getValue());
//            return MessageBuilder.withPayload(userSignup).build();
//        }
//        return MessageBuilder.withPayload(userSignup).build();
//    }
//
//    @KafkaListener(topics = "login")
//    @SendTo
//    public Message<AuthProcessDTO> listenLogin(AuthProcessDTO userSignup) {
//        log.info("Received login request: {}", userSignup.toString());
//        REGISTRATION_ENUMS check = authProcessService.login(userSignup.getUsername(), userSignup.getPassword());
//        if (check != REGISTRATION_ENUMS.LOGIN_SUCCESS) {
//            userSignup.setERROR_CODE(check.getValue());
//            return MessageBuilder.withPayload(userSignup).build();
//        }
//        return MessageBuilder.withPayload(userSignup).build();
//    }
//
}
