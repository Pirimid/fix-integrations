package com.pirimid.fxfix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.Logon;
import quickfix.fix44.MarketDataRequest;

import java.util.concurrent.CountDownLatch;

public class StartInitiator {

    private static CountDownLatch shutdownLatch = new CountDownLatch(1);
    private static final Logger logger = LoggerFactory.getLogger(StartInitiator.class);

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
                logger.error("Session not found for session id: {} : {}", sessionId.toString(), sessionNotFound);
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
        MarketDataRequest marketDataRequest = generateNewMarketDataRequest("11");
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol("EUR/USD"));
        marketDataRequest.addGroup(noRelatedSym);
        logger.info("####New Marked Data Spot Request Sent: " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataFwdRequest(SessionID sessionId) {
        MarketDataRequest marketDataRequest = generateNewMarketDataRequest("12");
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol("EUR/USD"));
        noRelatedSym.setField(new SettlType("6"));
        noRelatedSym.setField(new SettlDate("20181001"));
        marketDataRequest.addGroup(noRelatedSym);
        logger.info("####New Marked Data Fwd Request Sent: " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static void sendMarketDataNDFRequest(SessionID sessionId) {
        MarketDataRequest marketDataRequest = generateNewMarketDataRequest("13");
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol("EUR/USD"));
        noRelatedSym.set(new MaturityDate("20181001"));
        noRelatedSym.setField(new SettlType("6"));
        noRelatedSym.setField(new SettlDate("20181002"));
        noRelatedSym.setField(new CharField(9002, '1'));
        marketDataRequest.addGroup(noRelatedSym);
        logger.info("####New Marked Data NDF Request Sent: " + marketDataRequest.toString());
        sendMessageToTarget(sessionId, marketDataRequest);
    }

    private static MarketDataRequest generateNewMarketDataRequest(String mdReqId) {
        MarketDataRequest marketDataRequest = new MarketDataRequest(
                new MDReqID(mdReqId),
                new SubscriptionRequestType('1'),
                new MarketDepth(0)
        );
        marketDataRequest.set(new MDUpdateType(1));
        marketDataRequest.set(new NoMDEntryTypes(2));
        marketDataRequest.set(new NoRelatedSym(1));
        MarketDataRequest.NoMDEntryTypes noMDEntryType1 = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryType1.set(new MDEntryType('0'));
        marketDataRequest.addGroup(noMDEntryType1);
        MarketDataRequest.NoMDEntryTypes noMDEntryType2 = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryType2.set(new MDEntryType('1'));
        marketDataRequest.addGroup(noMDEntryType2);
        return marketDataRequest;
    }

    private static void sendMessageToTarget(SessionID sessionId, quickfix.fix44.Message message) {
        try {
            Session.sendToTarget(message, sessionId);
        } catch (SessionNotFound sessionNotFound) {
            logger.error("Session not found for session id: {} : {}", sessionId.toString(), sessionNotFound);
        }
    }
}
