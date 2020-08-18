package com.barco.admin.core;

import com.barco.admin.sesssion.SessionHandlerWithNoResponse;
import com.barco.admin.sesssion.SessionHandlerWithResponse;
import com.barco.common.utility.ExceptionUtil;
import com.barco.model.RequestMessage;
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
import java.util.concurrent.TimeUnit;


@Component
public class RequestDistributionScheduler {

    public final Logger logger = LogManager.getLogger(RequestDistributionScheduler.class);

    private String HEADER_NAME = "X-Authorization";

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
    public void requestDistribution() {
        StompSession stompSession = null;
        try {
            int service = 1;
            switch (service) {
                case 1:
                    stompSession = this.stompClient.connect(this.service1WithResponse, new WebSocketHttpHeaders(),
                            this.getTokenForStompHeaders(), this.sessionHandlerWithResponse).get(1, TimeUnit.SECONDS);
                    this.sessionHandlerWithResponse.subscribeAndSend(stompSession,
                            this.service1WithResponse, new RequestMessage(UUID.randomUUID().toString()));
                    // stop session for other service
//                    stompSession = this.stompClient.connect(this.service1WithResponse,
//                            this.sessionHandlerWithNoResponse).get(1, TimeUnit.SECONDS);
//                    this.sessionHandlerWithResponse.subscribeAndSend(stompSession,
//                            this.service1WithResponse, new RequestMessage(UUID.randomUUID().toString()));
                    break;
                case 2:
                    stompSession = this.stompClient.connect(this.service2WithResponse,
                            this.sessionHandlerWithResponse).get(1, TimeUnit.SECONDS);;
                    this.sessionHandlerWithResponse.subscribeAndSend(stompSession,
                            this.service1WithResponse, new RequestMessage(UUID.randomUUID().toString()));
                    // stop session for other service
//                    stompSession = this.stompClient.connect(this.service2WithNoResponse,
//                            this.sessionHandlerWithNoResponse).get(1, TimeUnit.SECONDS);;
//                    this.sessionHandlerWithResponse.subscribeAndSend(stompSession,
//                            this.service1WithResponse, new RequestMessage(UUID.randomUUID().toString()));
                    break;
                case 3:
                    stompSession = this.stompClient.connect(this.service3WithResponse,
                            this.sessionHandlerWithResponse).get(1, TimeUnit.SECONDS);;
                    this.sessionHandlerWithResponse.subscribeAndSend(stompSession,
                            this.service1WithResponse, new RequestMessage(UUID.randomUUID().toString()));
                    // stop session for other service
//                    stompSession = this.stompClient.connect(this.service3WithNoResponse,
//                            this.sessionHandlerWithNoResponse).get(1, TimeUnit.SECONDS);;
//                    this.sessionHandlerWithResponse.subscribeAndSend(stompSession,
//                            this.service1WithResponse, new RequestMessage(UUID.randomUUID().toString()));
                    break;
            }
        } catch (Exception ex) {
            logger.error("Exception :- " + ExceptionUtil.getRootCauseMessage(ex));
        } finally {
            // afer send reqeust discount session
            if(stompSession != null) {
                stompSession.disconnect();
            }
        }
    }

    // here we cache our token for specif time after that we re-login
    // 10 minutes
    public StompHeaders getTokenForStompHeaders() {
        StompHeaders connectHeaders = new StompHeaders();
        connectHeaders.add(HEADER_NAME, "Pakistan Zindabad");
        return connectHeaders;
    }

}
