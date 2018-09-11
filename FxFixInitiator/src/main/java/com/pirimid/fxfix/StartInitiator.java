package com.pirimid.fxfix;

import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.Logon;
import quickfix.fix42.MarketDataRequest;
import quickfix.fix42.NewOrderSingle;
import quickfix.fix50.component.InstrmtGrp;
import quickfix.fix50.component.RFQReqGrp;

public class StartInitiator {

    public static void main(String args[]) throws FieldNotFound {
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
            SessionID sessionId =  (SessionID) socketInitiator.getSessions().get(0);
            Session.lookupSession(sessionId).logon();


            Logon logon = new Logon();

            logon.set(new quickfix.field.HeartBtInt(30));
            logon.set(new quickfix.field.ResetSeqNumFlag(true));
            logon.set(new quickfix.field.EncryptMethod(0));

            try {
                Session.sendToTarget(logon, sessionId);
            } catch (SessionNotFound sessionNotFound) {
                sessionNotFound.printStackTrace();
            }


            for(int j = 0; j < 2; j ++){
                try {
                    Thread.sleep(10000);
                    sendMarketDataRequest(sessionId);
                    sendNewOrderSingle(sessionId);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SessionNotFound sessionNotFound) {
                    sessionNotFound.printStackTrace();
                }
            }

        }  catch (ConfigError configError) {
            configError.printStackTrace();
        }
    }

    private static void sendNewOrderSingle(SessionID sessionId) throws SessionNotFound {
        NewOrderSingle newOrderSingle = new NewOrderSingle(
                new ClOrdID("456"),
                new HandlInst('3'),
                new Symbol("AJCB"),
                new Side(Side.BUY),
                new TransactTime(),
                new OrdType(OrdType.MARKET)
        );
        System.out.println("####New Order Sent :" + newOrderSingle.toString());
        Session.sendToTarget(newOrderSingle, sessionId);
    }

    private static void sendMarketDataRequest(SessionID sessionId) throws SessionNotFound {
        MarketDataRequest marketDataRequest = new MarketDataRequest(
                new MDReqID("11"),
                new SubscriptionRequestType('1'),
                new MarketDepth(0)
        );
        marketDataRequest.set(new MDUpdateType(1));
        marketDataRequest.set(new NoMDEntryTypes(2));
        marketDataRequest.set(new NoRelatedSym(1));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol("EUR/USD"));
        marketDataRequest.addGroup(noRelatedSym);
        MarketDataRequest.NoMDEntryTypes noMDEntryTypes = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryTypes.set(new MDEntryType('0'));
        noMDEntryTypes.set(new MDEntryType('1'));
        marketDataRequest.addGroup(noMDEntryTypes);
        System.out.println("####New Marked Data Request Sent: " + marketDataRequest.toString());
        Session.sendToTarget(marketDataRequest, sessionId);
    }
}
