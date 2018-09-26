package com.pirimid.fxfix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.MDEntryPx;
import quickfix.field.NoMDEntries;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.MarketDataIncrementalRefresh;
import quickfix.fix44.MarketDataSnapshotFullRefresh;

public class FxFixInitiator extends MessageCracker implements Application {

    private static final Logger logger = LoggerFactory.getLogger(FxFixInitiator.class);

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
        logger.info("Admin Message Received (Initiator) :" + message.toString());
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {

    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        logger.info("Application Response Received (Initiator) :" + message.toString());
        crack(message, sessionID);
    }

    public void onMessage(MarketDataSnapshotFullRefresh response, SessionID sessionId) throws FieldNotFound {
        String MDReqId = response.getMDReqID().toString();
        MDEntryPx mdEntryPx = new MDEntryPx();
        Double value = response.getField(mdEntryPx).getValue();
        logger.info("MarketDataSnapshotFullRefresh for MDReqId: " + MDReqId + " | Value: " + value);
    }

    public void onMessage(MarketDataIncrementalRefresh response, SessionID sessionId) throws FieldNotFound {
        String MDReqId = response.getMDReqID().toString();
        MDEntryPx mdEntryPx = new MDEntryPx();
        Double value = response.getGroup(1, NoMDEntries.FIELD).getField(mdEntryPx).getValue();
        logger.info("MarketDataIncrementalRefresh for MDReqId: " + MDReqId + " | Value: " + value);
    }

    public void onMessage(ExecutionReport response, SessionID sessionId) throws FieldNotFound {
        logger.info("Execution report repsonse : " + response.toString());
    }
}
