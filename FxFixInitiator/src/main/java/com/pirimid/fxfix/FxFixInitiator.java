package com.pirimid.fxfix;

import com.pirimid.utils.RequestGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.ClOrdID;
import quickfix.field.MDEntryPx;
import quickfix.field.NoMDEntries;
import quickfix.fix44.ExecutionReport;
import quickfix.fix44.MarketDataIncrementalRefresh;
import quickfix.fix44.MarketDataSnapshotFullRefresh;
import quickfix.fix44.NewOrderSingle;

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

    public void onMessage(MarketDataSnapshotFullRefresh message, SessionID sessionId) throws FieldNotFound {
        String MDReqId = message.getMDReqID().getValue();
        MDEntryPx mdEntryPx = new MDEntryPx();
        Double value = message.getField(mdEntryPx).getValue();
        logger.info("MarketDataSnapshotFullRefresh for MDReqId: " + MDReqId + " | Value: " + value);
        if(value > 1.8) {
            sendNewOrderSingleRequest(message, sessionId);
        }
    }

    public void onMessage(MarketDataIncrementalRefresh message, SessionID sessionId) throws FieldNotFound {
        String MDReqId = message.getMDReqID().getValue();
        MDEntryPx mdEntryPx = new MDEntryPx();
        Double value = message.getGroup(1, NoMDEntries.FIELD).getField(mdEntryPx).getValue();
        logger.info("MarketDataIncrementalRefresh for MDReqId: " + MDReqId + " | Value: " + value);
        if(value > 1.8) {
            sendNewOrderSingleRequest(message, sessionId);
        }
    }

    private void sendNewOrderSingleRequest(quickfix.fix44.Message message, SessionID sessionId) {
        NewOrderSingle newOrderSingle = RequestGenerator.generateNewOrderSingle(message);
        logger.info("New Order Single Sent: " + newOrderSingle.toString());
        try {
            Session.sendToTarget(newOrderSingle, sessionId);
        } catch (SessionNotFound sessionNotFound) {
            logger.error("Session not found for session id: {}", sessionId.toString(), sessionNotFound);
        }
    }

    public void onMessage(ExecutionReport response, SessionID sessionId) throws FieldNotFound {
        String MDReqId = response.getField(new ClOrdID()).getValue();
        logger.info("ExecutionReport for Request Id: " + MDReqId + " | " + response.toString());
    }
}
