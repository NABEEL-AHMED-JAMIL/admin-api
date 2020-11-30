package com.barco.admin.core.sesssion;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;


/**
 * @author Nabeel Ahmed
 */
@Component
@Scope("prototype")
public class SessionHandlerWithNoResponse extends StompSessionHandlerAdapter {

    public Logger logger = LogManager.getLogger(SessionHandlerWithNoResponse.class);

    private final String WS_TOPIC_NO_RESPONSE = "/topic/messagesNoResponse";
    private final String SAMPLE_ENDPOINT_WITHOUT_RESPONSE_MESSAGE_MAPPING = "/app/sampleEndpointWithoutResponse";

    public void subscribeAndSend(StompSession session, Object payload) {
        session.subscribe(WS_TOPIC_NO_RESPONSE, this);
        session.send(SAMPLE_ENDPOINT_WITHOUT_RESPONSE_MESSAGE_MAPPING, payload);
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
