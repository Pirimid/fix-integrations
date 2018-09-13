package com.pirimid.fxFix;

import com.pirimid.utility.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.MarketDataRequest;
import quickfix.fix42.MarketDataSnapshotFullRefresh;
import quickfix.fix42.NewOrderSingle;

import java.util.List;

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

    public void onMessage(quickfix.fix42.MarketDataRequest order, SessionID sessionID) throws FieldNotFound {
        logger.info("###New Market Data Request Order Received: " + order.toString());
        logger.info("###Market Data Request Id: " + order.getMDReqID().toString());
        logger.info("###Subscriptoin Request Type: " + order.getSubscriptionRequestType().toString());
        logger.info("###Market Depth: " + order.getMarketDepth().toString());

        responseSender.startSendingMarketDataFullRefresh(order, sessionID);
    }

    public void sendMessageToClient(quickfix.fix42.NewOrderSingle order, SessionID sessionID) {
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

    public void sendMarketDataFullRefreshToClient(MarketDataRequest order, SessionID sessionID, Double price) {
        List<Group> groups = order.getGroups(NoRelatedSym.FIELD);

        for (Group group : groups) {
            try {
                Symbol symbol = new Symbol(group.getString(Symbol.FIELD));
                List<Group> mdEntries = order.getGroups(NoMDEntryTypes.FIELD);
                MarketDataSnapshotFullRefresh marketDataSnapshotFullRefresh = new MarketDataSnapshotFullRefresh(symbol);
                marketDataSnapshotFullRefresh.set(order.getMDReqID());
                marketDataSnapshotFullRefresh.set(new NoMDEntries(mdEntries.size()));
                marketDataSnapshotFullRefresh.setField(order.getMarketDepth());
                for (int i = 0; i < mdEntries.size(); i++) {
                    MarketDataSnapshotFullRefresh.NoMDEntries mdEntryGroup = new MarketDataSnapshotFullRefresh.NoMDEntries();
                    mdEntryGroup.set(new MDEntryType('0'));
                    mdEntryGroup.setField(new MDEntryID("MDEntryId" + i));
                    mdEntryGroup.set(new MDEntryPx(price));
                    mdEntryGroup.set(new MDEntrySize(10000000));
                    mdEntryGroup.set(new QuoteEntryID("QuoteEntryId" + i));
                    mdEntryGroup.set(new MDEntryPositionNo(4));
                    mdEntryGroup.setField(new MDQuoteType(1));
                    mdEntryGroup.set(new MDEntryType('1'));
                    marketDataSnapshotFullRefresh.addGroup(mdEntryGroup);
                }
                SettlDate settlDate = new SettlDate("20171117");
                marketDataSnapshotFullRefresh.setField(settlDate);

                Session.sendToTarget(marketDataSnapshotFullRefresh, sessionID);
            } catch (FieldNotFound fieldNotFound) {
                fieldNotFound.printStackTrace();
            } catch (SessionNotFound sessionNotFound) {
                sessionNotFound.printStackTrace();
            }
        }
    }

    public void setResponseSender(ResponseSender responseSender) {
        this.responseSender = responseSender;
    }
}
