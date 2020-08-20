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
import java.util.Random;
import java.util.UUID;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


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

    private StompSession stompSessionService1;
    private StompSession stompSessionService2;
    private StompSession stompSessionService3;

    @PostConstruct
    // we create 3 session each for service
    private void postConstruct() {
        logger.info("+================Service-Session-Init-Start====================+");
        try {
            this.stompSessionService1 = this.stompClient.connect(this.service1WithResponse, new WebSocketHttpHeaders(),
                    this.getConnectHeaders(), this.sessionHandlerWithNoResponse).get();
        } catch (Exception ex) {
            logger.info("Session Service 1 not running");
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
        }
        try {
            this.stompSessionService2 = this.stompClient.connect(this.service2WithResponse, new WebSocketHttpHeaders(),
                    this.getConnectHeaders(), this.sessionHandlerWithNoResponse).get();
        } catch (Exception ex) {
            logger.info("Session Service 2 not running");
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
        }
        try {
            this.stompSessionService3 = this.stompClient.connect(this.service3WithNoResponse, new WebSocketHttpHeaders(),
                    this.getConnectHeaders(), this.sessionHandlerWithNoResponse).get();
        } catch (Exception ex) {
            logger.info("Session Service 3 not running");
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
        }
        logger.info("+================Service-Session-Init-End====================+");
    }


    @Scheduled(fixedDelay=1000)
    public void requestDistributionWithStompHeader() {

        try {
            int service = this.getRandomNumberUsingInts(1, 4);
            System.out.println("Service Number :- " + service);
            switch (service) {
                case 1:
                    /**
                    this.sessionHandlerWithResponse.subscribeAndSend(stompSession, new RequestMessage(UUID.randomUUID().toString()));
                     */
                     this.sessionHandlerWithNoResponse.subscribeAndSend(this.stompSessionService1, new RequestMessage(UUID.randomUUID().toString()));
                    break;
                case 2:
                    /**
                    this.sessionHandlerWithResponse.subscribeAndSend(stompSession, new RequestMessage(UUID.randomUUID().toString()));
                     */
                    this.sessionHandlerWithNoResponse.subscribeAndSend(this.stompSessionService2, new RequestMessage(UUID.randomUUID().toString()));
                    break;
                case 3:
                    /**
                    this.sessionHandlerWithResponse.subscribeAndSend(stompSession, new RequestMessage(UUID.randomUUID().toString()));
                    */
                    this.sessionHandlerWithNoResponse.subscribeAndSend(this.stompSessionService3, new RequestMessage(UUID.randomUUID().toString()));
                    break;
            }
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
        }
    }

    @PreDestroy
    public void preDestroy() {
        try {
             if(this.stompSessionService1 != null) {
                 logger.info("Stomp Session Service-1 Close for Session ID {}", this.stompSessionService1.getSessionId());
                 this.stompSessionService1.disconnect();
             }
             if(this.stompSessionService2 != null) {
                 logger.info("Stomp Session Service-2 Close for Session ID {}", this.stompSessionService2.getSessionId());
                 this.stompSessionService2.disconnect();
             }
             if(this.stompSessionService3 != null) {
                 logger.info("Stomp Session Service-3 Close for Session ID {}", this.stompSessionService3.getSessionId());
                 this.stompSessionService3.disconnect();
             }
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
        }
    }

    // stomp header
    private StompHeaders getConnectHeaders() {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add(USERNAME, USER);
        connectHeaders.add(PASSWORD, PASS);
        return connectHeaders;
    }

    public int getRandomNumberUsingInts(int min, int max) {
        Random random = new Random();
        return random.ints(min, max).findFirst().getAsInt();
    }

}
