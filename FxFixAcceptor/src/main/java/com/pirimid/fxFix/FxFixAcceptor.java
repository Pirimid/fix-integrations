package com.pirimid.fxFix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.NewOrderSingle;

/**
 * A Fix Acceptor implementation for FX.
 */
public class FxFixAcceptor extends MessageCracker implements Application {

    private static final Logger logger = LoggerFactory.getLogger(FxFixAcceptor.class);
    private ResponseSender responseSender = new ResponseSender();

    @Override
    public void onCreate(SessionID sessionID) {
        logger.info("New session cerated: " + sessionID.toString());
    }

    @Override
    public void onLogon(SessionID sessionID) {
        logger.info("Logged on for session: " + sessionID.toString());
    }

    @Override
    public void onLogout(SessionID sessionID) {
        logger.info("Logged out of session: " + sessionID.toString());
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {

    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        logger.debug("Admin Message Received (Acceptor) :" + message.toString());
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {

    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        logger.debug("Message received from App: " + message.toString() + " for session: " + sessionID.toString());
        crack(message, sessionID);
    }

    public void onMessage(MarketDataRequest request, SessionID sessionID) throws FieldNotFound {
        logger.info("###New Market Data Request Order Received: " + request.toString());
        logger.info("###Market Data Request Id: " + request.getMDReqID().toString());
        logger.info("###Subscriptoin Request Type: " + request.getSubscriptionRequestType().toString());
        logger.info("###Market Depth: " + request.getMarketDepth().toString());

        char subscriptionType = request.getSubscriptionRequestType().getValue();
        if(isSubscribeRequest(subscriptionType)) {
            responseSender.subscribeNewMarketDataRequest(request);
            responseSender.startSendingMarketDataRefreshResponseIfNotStarted(sessionID);
        } else if(isUnsubscribeRequest(subscriptionType)) {
            String reqId = request.getMDReqID().getValue();
            responseSender.unsubscribeMarketDataRequest(reqId);
        }
    }

    private boolean isUnsubscribeRequest(char subscriptionType) {
        return subscriptionType == '2';
    }

    private boolean isSubscribeRequest(char subscriptionType) {
        return subscriptionType == '1';
    }

    public void onMessage(NewOrderSingle order, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        logger.info("###NewOrder Received:" + order.toString());
        logger.info("###Symbol" + order.getSymbol().toString());
        logger.info("###Side" + order.getSide().toString());
        logger.info("###Type" + order.getOrdType().toString());
        logger.info("###TransactioTime" + order.getTransactTime().toString());

        String reqId = order.getClOrdID().getValue();
        responseSender.unsubscribeMarketDataRequest(reqId);
        responseSender.sendExecutionReportToClient(order, sessionID);
    }

    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }
}
