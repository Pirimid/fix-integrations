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
        logger.debug("Admin Message Received (Initiator) :" + message.toString());
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {

    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        logger.debug("Application Response Received (Initiator) :" + message.toString());
        crack(message, sessionID);
    }

    public void onMessage(MarketDataSnapshotFullRefresh message, SessionID sessionId) throws FieldNotFound {
        String MDReqId = message.getMDReqID().getValue();
        MDEntryPx mdEntryPx = new MDEntryPx();
        Double value = message.getField(mdEntryPx).getValue();
        logger.info("MDReqId: " + MDReqId + " | Price: " + value + " | FullRefresh");
        if (value > 1.92) {
            NewOrderSingle newOrderSingle = RequestGenerator.generateNewSellOrderSingle(message);
            sendNewOrderSingleRequest(newOrderSingle, sessionId);
        }
    }

    public void onMessage(MarketDataIncrementalRefresh message, SessionID sessionId) throws FieldNotFound {
        String MDReqId = message.getMDReqID().getValue();
        MDEntryPx mdEntryPx = new MDEntryPx();
        Double value = message.getGroup(1, NoMDEntries.FIELD).getField(mdEntryPx).getValue();
        logger.info("MDReqId: " + MDReqId + " | Price: " + value + " | IncrementalRefresh");
        if (value < 1.08) {
            NewOrderSingle newOrderSingle = RequestGenerator.generateNewBuyOrderSingle(message);
            sendNewOrderSingleRequest(newOrderSingle, sessionId);
        }
    }

    private void sendNewOrderSingleRequest(NewOrderSingle newOrderSingle, SessionID sessionId) throws FieldNotFound {
        logger.info("NewOrderSingle Sent for Id: " + newOrderSingle.getClOrdID().getValue() + " | Price: " + newOrderSingle.getPrice().getValue() + " | " + newOrderSingle.toString());
        try {
            Session.sendToTarget(newOrderSingle, sessionId);
        } catch (SessionNotFound sessionNotFound) {
            logger.error("Session not found for session id: {}", sessionId.toString(), sessionNotFound);
        }
    }

    public void onMessage(ExecutionReport response, SessionID sessionId) throws FieldNotFound {
        String MDReqId = response.getField(new ClOrdID()).getValue();
        logger.info("Request Id: " + MDReqId + " | ExecutionReport");
    }
}
