package com.barco.admin.sesssion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;


@Component
public class SessionHandlerWithNoResponse extends StompSessionHandlerAdapter {

    public Logger logger = LogManager.getLogger(SessionHandlerWithNoResponse.class);

    private final String WS_TOPIC_DESTINATION_PREFIX = "/topic";
    private final String WS_TOPIC_NO_RESPONSE = WS_TOPIC_DESTINATION_PREFIX+"/messagesNoResponse";

    public void subscribeAndSend(StompSession session,String utl, Object payload) {
        session.subscribe(WS_TOPIC_NO_RESPONSE, this);
        session.send(utl, payload);
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Void.class;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        logger.info("Response has been received {}", payload);
    }

    @Override
    public void handleException(StompSession session, StompCommand command, StompHeaders headers,
        byte[] payload, Throwable exception) {
        super.handleException(session, command, headers, payload, exception);
    }
}
