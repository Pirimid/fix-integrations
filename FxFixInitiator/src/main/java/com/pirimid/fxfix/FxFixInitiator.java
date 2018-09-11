package com.pirimid.fxfix;

import quickfix.*;
import quickfix.field.BidSpotRate;
import quickfix.field.MDEntryPx;
import quickfix.field.MDReqID;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

public class FxFixInitiator extends MessageCracker implements Application {

    @Override
    public void onCreate(SessionID sessionID) {

    }

    @Override
    public void onLogon(SessionID sessionID) {

    }

    @Override
    public void onLogout(SessionID sessionID) {

    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {

    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        System.out.println("Admin Message Received (Initiator) :" + message.toString());
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {

    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        System.out.println("Application Response Received (Initiator) :" + message.toString());
        crack(message, sessionID);
    }

    public void onMessage(quickfix.fix42.MarketDataSnapshotFullRefresh response, SessionID sessionId) throws FieldNotFound {
        System.out.println("Request Id: " + response.getMDReqID().toString());
        MDEntryPx mdEntryPx = new MDEntryPx();
        Double value = response.getField(mdEntryPx).getValue();
        System.out.println("Value: " + value);
    }

    public void onMessage(quickfix.fix42.ExecutionReport response, SessionID sessionId) throws FieldNotFound {
        System.out.println("Execution report repsonse : " + response.toString());
    }
}
