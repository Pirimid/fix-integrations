package com.pirimid.fxfix;

import com.pirimid.utils.RequestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.fix44.Logon;
import quickfix.fix44.MarketDataRequest;

import java.util.concurrent.CountDownLatch;

public class StartInitiator {

    private static final Logger logger = LoggerFactory.getLogger(StartInitiator.class);
    private static final String CONFIG_FILE_NAME = "./initiatorSettings.txt";

    private static CountDownLatch shutdownLatch = new CountDownLatch(1);

    public static void main(String args[]) {
        try {
            SessionID sessionId = initializeSession();
            logonSession(sessionId);
            try {
                Thread.sleep(10000);
                sendMarketDataRequests(sessionId);
                shutdownLatch.await();
            } catch (InterruptedException e) {
                logger.error("Thread interrupted while sending request", e);
            }
        } catch (ConfigError configError) {
            logger.error("Error while configuring session", configError);
        }

    }

    private static SessionID initializeSession() throws ConfigError {
        SocketInitiator socketInitiator;
        SessionSettings initiatorSettings = new SessionSettings(CONFIG_FILE_NAME);
        Application initiatorApplication = new FxFixInitiator();
        FileStoreFactory fileStoreFactory = new FileStoreFactory(
                initiatorSettings);
        FileLogFactory fileLogFactory = new FileLogFactory(
                initiatorSettings);
        MessageFactory messageFactory = new DefaultMessageFactory();
        socketInitiator = new SocketInitiator(initiatorApplication, fileStoreFactory, initiatorSettings, fileLogFactory, messageFactory);
        socketInitiator.start();
        SessionID sessionId = (SessionID) socketInitiator.getSessions().get(0);
        Session.lookupSession(sessionId).logon();
        return sessionId;
    }

    private static void logonSession(SessionID sessionId) {
        Logon logon = new Logon();

        logon.set(new quickfix.field.HeartBtInt(30));
        logon.set(new quickfix.field.ResetSeqNumFlag(true));
        logon.set(new quickfix.field.EncryptMethod(0));

        try {
            Session.sendToTarget(logon, sessionId);
        } catch (SessionNotFound sessionNotFound) {
            logger.error("Session not found for session id: {}", sessionId.toString(), sessionNotFound);
        }
    }

    private static void sendMarketDataRequests(SessionID sessionId) {
        try {
            sendMarketDataSpotRequest_FullRefresh(sessionId);
            Thread.sleep(2000);
            sendMarketDataFwdRequest_FullRefresh(sessionId);
            Thread.sleep(2000);
            sendMarketDataNDFRequest_FullRefresh(sessionId);
            Thread.sleep(2000);
            sendMarketDataSpotRequest_IncrementalRefresh(sessionId);
            Thread.sleep(2000);
            sendMarketDataFwdRequest_IncrementalRefresh(sessionId);
            Thread.sleep(2000);
            sendMarketDataNDFRequest_IncrementalRefresh(sessionId);
        } catch (InterruptedException e) {
            logger.error("Thread interrupted while sending request", e);
        } catch (FieldNotFound fieldNotFound) {
            logger.error("Field {} not found", fieldNotFound.field, fieldNotFound);
        }
    }

    private static void sendMarketDataSpotRequest_FullRefresh(SessionID sessionId) throws FieldNotFound {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Spot_FullRefresh();
        logger.info("SpotFullRefresh Request Sent for ReqId: " + marketDataRequest.getMDReqID().getValue() + " | " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataFwdRequest_FullRefresh(SessionID sessionId) throws FieldNotFound {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Fwd_FullRefresh();
        logger.info("ForwardFullRefresh Request Sent for ReqId: " + marketDataRequest.getMDReqID().getValue() + " | " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataNDFRequest_FullRefresh(SessionID sessionId) throws FieldNotFound {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_NDF_FullRefresh();
        logger.info("NDFFullRefresh Request Sent for ReqId: " + marketDataRequest.getMDReqID().getValue() + " | " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataSpotRequest_IncrementalRefresh(SessionID sessionId) throws FieldNotFound {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Spot_IncrementalRefresh();
        logger.info("SpotIncrementalRefresh Request Sent for ReqId: " + marketDataRequest.getMDReqID().getValue() + " | " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataFwdRequest_IncrementalRefresh(SessionID sessionId) throws FieldNotFound {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Fwd_IncrementalRefresh();
        logger.info("ForwardIncrementalRefresh Request Sent for ReqId: " + marketDataRequest.getMDReqID().getValue() + " | " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataNDFRequest_IncrementalRefresh(SessionID sessionId) throws FieldNotFound {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_NDF_IncrementalRefresh();
        logger.info("NDFIncrementalRefresh Request Sent for ReqId: " + marketDataRequest.getMDReqID().getValue() + " | " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMessageToTarget(SessionID sessionId, quickfix.fix44.Message message) {
        try {
            Session.sendToTarget(message, sessionId);
        } catch (SessionNotFound sessionNotFound) {
            logger.error("Session not found for session id: {} : {}", sessionId.toString(), sessionNotFound);
        }
    }
}
