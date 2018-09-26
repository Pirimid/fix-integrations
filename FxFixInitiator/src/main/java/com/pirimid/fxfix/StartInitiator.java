package com.pirimid.fxfix;

import com.pirimid.utility.RequestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.Logon;
import quickfix.fix44.MarketDataRequest;

import java.util.concurrent.CountDownLatch;

public class StartInitiator {

    private static final Logger logger = LoggerFactory.getLogger(StartInitiator.class);

    private static final String SAMPLE_SETTL_DATE = "20181001";
    private static final String SAMPLE_MATURITY_DATE = "20181005";

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
                sendMarketDataSpotRequest(sessionId);
                sendMarketDataFwdRequest(sessionId);
                sendMarketDataNDFRequest(sessionId);
//                sendNewOrderSingle(sessionId);
                shutdownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (ConfigError configError) {
            configError.printStackTrace();
        }

    }

//    private static void sendNewOrderSingle(SessionID sessionId) throws SessionNotFound {
//        NewOrderSingle newOrderSingle = new NewOrderSingle(
//                new ClOrdID("456"),
//                new HandlInst('3'),
//                new Symbol("AJCB"),
//                new Side(Side.BUY),
//                new TransactTime(),
//                new OrdType(OrdType.MARKET)
//        );
//        System.out.println("####New Order Sent :" + newOrderSingle.toString());
//        Session.sendToTarget(newOrderSingle, sessionId);
//    }

    private static void sendMarketDataSpotRequest(SessionID sessionId) {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Spot_FullRefresh();
        logger.info("####New Marked Data Spot Full Refresh Request Sent: " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataFwdRequest(SessionID sessionId) {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Fwd_FullRefresh();
        logger.info("####New Marked Data Fwd Full Refresh Request Sent: " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataNDFRequest(SessionID sessionId) {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_NDF_FullRefresh();
        logger.info("####New Marked Data NDF Full Refresh Request Sent: " + marketDataRequest.toString());
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
