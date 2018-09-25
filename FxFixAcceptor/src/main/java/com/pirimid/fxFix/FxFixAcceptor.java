package com.pirimid.fxFix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
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
        logger.info("Admin Message Received (Acceptor) :" + message.toString());
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {

    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        logger.info("Message recieved from App: " + message.toString() + " for session: " + sessionID.toString());
        crack(message, sessionID);
    }

    public void onMessage(NewOrderSingle order, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        logger.info("###NewOrder Received:" + order.toString());
        logger.info("###Symbol" + order.getSymbol().toString());
        logger.info("###Side" + order.getSide().toString());
        logger.info("###Type" + order.getOrdType().toString());
        logger.info("###TransactioTime" + order.getTransactTime().toString());

        sendMessageToClient(order, sessionID);
    }

    public void onMessage(MarketDataRequest order, SessionID sessionID) throws FieldNotFound {
        logger.info("###New Market Data Request Order Received: " + order.toString());
        logger.info("###Market Data Request Id: " + order.getMDReqID().toString());
        logger.info("###Subscriptoin Request Type: " + order.getSubscriptionRequestType().toString());
        logger.info("###Market Depth: " + order.getMarketDepth().toString());

        responseSender.startSendingMarketDataRefreshResponse(order, sessionID);
    }

    public void sendMessageToClient(NewOrderSingle order, SessionID sessionID) {
        try {
            OrderQty orderQty = null;

            orderQty = new OrderQty(56.0);
            quickfix.fix40.ExecutionReport accept = new quickfix.fix40.ExecutionReport(new OrderID("133456"), new ExecID("789"),
                    new ExecTransType(ExecTransType.NEW), new OrdStatus(OrdStatus.NEW), order.getSymbol(), order.getSide(),
                    orderQty, new LastShares(0), new LastPx(0), new CumQty(0), new AvgPx(0));
            accept.set(order.getClOrdID());
            logger.info("###Sending Order Acceptance:" + accept.toString() + "sessionID:" + sessionID.toString());
            Session.sendToTarget(accept, sessionID);
        } catch (RuntimeException e) {
            LogUtil.logThrowable(sessionID, e.getMessage(), e);
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        } catch (SessionNotFound sessionNotFound) {
            sessionNotFound.printStackTrace();
        }
    }

    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }
}
