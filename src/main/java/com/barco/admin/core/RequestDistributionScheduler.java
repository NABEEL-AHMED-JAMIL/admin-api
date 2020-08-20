package com.barco.admin.core;

import com.barco.admin.sesssion.SessionHandlerWithNoResponse;
import com.barco.admin.sesssion.SessionHandlerWithResponse;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.wsm.RequestMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import java.util.UUID;


@Component
public class RequestDistributionScheduler {

    public final Logger logger = LogManager.getLogger(RequestDistributionScheduler.class);

    // STOM HEADER DETAIL
    private String USERNAME = "username";
    private String PASSWORD = "password";
    private String USER = "nabeel.amd93@gmail.com";
    private String PASS = "B@llistic1";

    @Value("${server.service1-with-response}")
    private String service1WithResponse;

    @Value("${server.service1-no-response}")
    private String service1WithNoResponse;

    @Value("${server.service2-with-response}")
    private String service2WithResponse;

    @Value("${server.service2-no-response}")
    private String service2WithNoResponse;

    @Value("${server.service3-with-response}")
    private String service3WithResponse;

    @Value("${server.service3-no-response}")
    private String service3WithNoResponse;

    @Autowired
    private WebSocketStompClient stompClient;
    @Autowired
    private SessionHandlerWithNoResponse sessionHandlerWithNoResponse;
    @Autowired
    private SessionHandlerWithResponse sessionHandlerWithResponse;

    @Scheduled(fixedDelay=5000)
    public void requestDistributionWithStompHeader() {
        StompSession stompSession = null;
        try {
            int service = 1;
            switch (service) {
                case 1:
                    stompSession = this.stompClient.connect(this.service1WithResponse, new WebSocketHttpHeaders(), this.getConnectHeaders(),
                            this.sessionHandlerWithResponse).get();
                    this.sessionHandlerWithResponse.subscribeAndSend(stompSession, new RequestMessage(UUID.randomUUID().toString()));
                    // stop session for other service
                    stompSession = this.stompClient.connect(this.service1WithResponse, new WebSocketHttpHeaders(), this.getConnectHeaders(),
                            this.sessionHandlerWithNoResponse).get();
                    this.sessionHandlerWithNoResponse.subscribeAndSend(stompSession, new RequestMessage(UUID.randomUUID().toString()));
                    break;
                case 2:
                    stompSession = this.stompClient.connect(this.service2WithResponse, new WebSocketHttpHeaders(), this.getConnectHeaders(),
                            this.sessionHandlerWithResponse).get();
                    this.sessionHandlerWithResponse.subscribeAndSend(stompSession, new RequestMessage(UUID.randomUUID().toString()));
                    // stop session for other service
                    stompSession = this.stompClient.connect(this.service2WithNoResponse,new WebSocketHttpHeaders(), this.getConnectHeaders(),
                            this.sessionHandlerWithNoResponse).get();
                    this.sessionHandlerWithNoResponse.subscribeAndSend(stompSession, new RequestMessage(UUID.randomUUID().toString()));
                    break;
                case 3:
                    stompSession = this.stompClient.connect(this.service3WithResponse, new WebSocketHttpHeaders(), this.getConnectHeaders(),
                            this.sessionHandlerWithResponse).get();
                    this.sessionHandlerWithResponse.subscribeAndSend(stompSession, new RequestMessage(UUID.randomUUID().toString()));
                    // stop session for other service
                    stompSession = this.stompClient.connect(this.service3WithNoResponse, new WebSocketHttpHeaders(), this.getConnectHeaders(),
                            this.sessionHandlerWithNoResponse).get();
                    this.sessionHandlerWithNoResponse.subscribeAndSend(stompSession, new RequestMessage(UUID.randomUUID().toString()));
                    break;
            }
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
        } finally {
            // afer send reqeust discount session
            //if(stompSession != null) {
                //logger.info("Stomp Session Close for Session ID {}", stompSession.getSessionId());
                //stompSession.disconnect();
            //}
        }
    }

    // stomp header
    private StompHeaders getConnectHeaders() {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add(USERNAME, USER);
        connectHeaders.add(PASSWORD, PASS);
        return connectHeaders;
    }

}
