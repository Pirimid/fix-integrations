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

    private static CountDownLatch shutdownLatch = new CountDownLatch(1);

    public static void main(String args[]) {
        SocketInitiator socketInitiator = null;
        try {
            SessionSettings initiatorSettings = new SessionSettings(
                    "./initiatorSettings.txt");
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


            Logon logon = new Logon();

            logon.set(new quickfix.field.HeartBtInt(30));
            logon.set(new quickfix.field.ResetSeqNumFlag(true));
            logon.set(new quickfix.field.EncryptMethod(0));

            try {
                Session.sendToTarget(logon, sessionId);
            } catch (SessionNotFound sessionNotFound) {
                logger.error("Session not found for session id: {}", sessionId.toString(), sessionNotFound);
            }

            try {
                Thread.sleep(10000);

                sendMarketDataSpotRequest_FullRefresh(sessionId);
                sendMarketDataFwdRequest_FullRefresh(sessionId);
                sendMarketDataNDFRequest_FullRefresh(sessionId);
                sendMarketDataSpotRequest_IncrementalRefresh(sessionId);
                sendMarketDataFwdRequest_IncrementalRefresh(sessionId);
                sendMarketDataNDFRequest_IncrementalRefresh(sessionId);

                shutdownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (ConfigError configError) {
            configError.printStackTrace();
        }

    }

    private static void sendMarketDataSpotRequest_FullRefresh(SessionID sessionId) {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Spot_FullRefresh();
        logger.info("New Marked Data Spot Full Refresh Request Sent: " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataFwdRequest_FullRefresh(SessionID sessionId) {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Fwd_FullRefresh();
        logger.info("New Marked Data Fwd Full Refresh Request Sent: " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataNDFRequest_FullRefresh(SessionID sessionId) {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_NDF_FullRefresh();
        logger.info("New Marked Data NDF Full Refresh Request Sent: " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataSpotRequest_IncrementalRefresh(SessionID sessionId) {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Spot_IncrementalRefresh();
        logger.info("New Marked Data Spot Incremental Refresh Request Sent: " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataFwdRequest_IncrementalRefresh(SessionID sessionId) {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Fwd_IncrementalRefresh();
        logger.info("New Marked Data Fwd Incremental Refresh Request Sent: " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataNDFRequest_IncrementalRefresh(SessionID sessionId) {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_NDF_IncrementalRefresh();
        logger.info("New Marked Data NDF Incremental Refresh Request Sent: " + marketDataRequest.toString());
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
