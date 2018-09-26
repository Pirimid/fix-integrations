package com.pirimid.fxFix;

import com.pirimid.utility.Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix44.MarketDataIncrementalRefresh;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.MarketDataSnapshotFullRefresh;
import quickfix.fix44.Message;

import java.util.List;

import static com.pirimid.utility.FieldConstants.MDQuoteType;
import static com.pirimid.utility.FieldConstants.NDF;

public class ResponseSender {

    private static final Logger logger = LoggerFactory.getLogger(ResponseSender.class);

    public void startSendingMarketDataRefreshResponse(MarketDataRequest order, SessionID sessionID) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                MDUpdateType mdUpdateType = new MDUpdateType();
                int mdUpdateTypeValue = 1;
                try {
                    mdUpdateTypeValue = order.get(mdUpdateType).getValue();
                } catch (FieldNotFound fieldNotFound) {
                    logger.error("Filed {} not found in order {}", mdUpdateType.getField(), order.toString());
                }
                Double price = 1.16; // Sample price for EUR/USD
                while (Session.lookupSession(sessionID).hasResponder()) {
                    if (isIncrementalRefreshRequested(mdUpdateTypeValue)) {
                        sendMarketDataIncrementalRefreshToClient(order, sessionID, price);
                    } else {
                        sendMarketDataFullRefreshToClient(order, sessionID, price);
                    }
                    price = Helper.generateNextPrice(price);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
    }

    protected void sendMarketDataFullRefreshToClient(MarketDataRequest order, SessionID sessionID, Double price) {
        List<Group> groups = order.getGroups(NoRelatedSym.FIELD);

        for (Group group : groups) {
            try {
                Symbol symbol = new Symbol(group.getString(Symbol.FIELD));
                List<Group> mdEntries = order.getGroups(NoMDEntryTypes.FIELD);
                MarketDataSnapshotFullRefresh marketDataSnapshotFullRefresh = new MarketDataSnapshotFullRefresh();
                marketDataSnapshotFullRefresh.set(symbol);
                marketDataSnapshotFullRefresh.set(order.getMDReqID());
                marketDataSnapshotFullRefresh.set(new NoMDEntries(mdEntries.size()));
                marketDataSnapshotFullRefresh.setField(order.getMarketDepth());
                String settlTypeValue = "0";
                if(isSetField(group, SettlType.FIELD)) {
                    settlTypeValue = group.getField(new SettlType()).getValue();
                    marketDataSnapshotFullRefresh.setField(group.getField(new SettlType()));
                }
                String settlDateValue = "20180925";
                if(isSetField(group, SettlDate.FIELD)) {
                    settlDateValue = group.getField(new SettlDate()).getValue();
                }
                marketDataSnapshotFullRefresh.setField(new SettlDate(settlDateValue));
                if(isSetField(group, MaturityDate.FIELD)) {
                    marketDataSnapshotFullRefresh.setField(group.getField(new MaturityDate()));
                }
                if(isSetField(group, NDF)) {
                    marketDataSnapshotFullRefresh.setField(group.getField(new CharField(NDF)));
                }
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

                sendMessageToTarget(sessionID, marketDataSnapshotFullRefresh);
            } catch (FieldNotFound fieldNotFound) {
                logger.error("Field {} not found", fieldNotFound.field, fieldNotFound);
            }
        }
    }

    protected void sendMarketDataIncrementalRefreshToClient(MarketDataRequest order, SessionID sessionID, Double price) {
        List<Group> groups = order.getGroups(NoRelatedSym.FIELD);

        for (Group group : groups) {
            try {
                Symbol symbol = new Symbol(group.getString(Symbol.FIELD));
                List<Group> mdEntries = order.getGroups(NoMDEntryTypes.FIELD);
                MarketDataIncrementalRefresh marketDataIncrementalRefresh = new MarketDataIncrementalRefresh();
                marketDataIncrementalRefresh.setField(symbol);
                marketDataIncrementalRefresh.set(order.getMDReqID());
                marketDataIncrementalRefresh.set(new NoMDEntries(mdEntries.size()));
                marketDataIncrementalRefresh.setField(order.getMarketDepth());
                String settlTypeValue = "0";
                if(isSetField(group, SettlType.FIELD)) {
                    settlTypeValue = group.getField(new SettlType()).getValue();
                    marketDataIncrementalRefresh.setField(group.getField(new SettlType()));
                }
                String settlDateValue = "20180925";
                if(isSetField(group, SettlDate.FIELD)) {
                    settlDateValue = group.getField(new SettlDate()).getValue();
                }
                marketDataIncrementalRefresh.setField(new SettlDate(settlDateValue));
                if(isSetField(group, MaturityDate.FIELD)) {
                    marketDataIncrementalRefresh.setField(group.getField(new MaturityDate()));
                }
                if(isSetField(group, NDF)) {
                    marketDataIncrementalRefresh.setField(group.getField(new CharField(NDF)));
                }
                for (int i = 0; i < mdEntries.size(); i++) {
                    MarketDataSnapshotFullRefresh.NoMDEntries mdEntryGroup = new MarketDataSnapshotFullRefresh.NoMDEntries();
                    Group entryGroup = mdEntries.get(i);
                    mdEntryGroup.set(new MDEntryType('0'));
                    mdEntryGroup.setField(new MDUpdateAction('0'));
                    char mdEntryTypeValue = entryGroup.getField(new MDEntryType()).getValue();
                    mdEntryGroup.set(new MDEntryType(mdEntryTypeValue));
                    if(isBidEntry(mdEntryTypeValue)) {
                        if(isSpotRequest(settlTypeValue)) {
                            mdEntryGroup.setField(new BidSpotRate(price));
                        } else {
                            mdEntryGroup.setField(new BidForwardPoints(price));
                        }
                    } else {
                        if(isSpotRequest(settlTypeValue)) {
                            mdEntryGroup.setField(new OfferSpotRate(price));
                        } else {
                            mdEntryGroup.setField(new OfferForwardPoints(price));
                        }
                    }
                    mdEntryGroup.setField(new MDEntryID("MDEntryId" + i));
                    mdEntryGroup.set(new MDEntryPx(price));
                    mdEntryGroup.set(new MDEntrySize(10000000));
                    mdEntryGroup.set(new QuoteEntryID("QuoteEntryId" + i));
                    mdEntryGroup.set(new MDEntryPositionNo(4));
                    mdEntryGroup.setField(new CharField(MDQuoteType, '1'));
                    marketDataIncrementalRefresh.addGroup(mdEntryGroup);
                }

                sendMessageToTarget(sessionID, marketDataIncrementalRefresh);
            } catch (FieldNotFound fieldNotFound) {
                logger.error("Field {} not found", fieldNotFound.field, fieldNotFound);
            }
        }
    }

    private void sendMessageToTarget(SessionID sessionID, Message message) {
        try {
            Session.sendToTarget(message, sessionID);
        } catch (SessionNotFound sessionNotFound) {
            logger.error("Session not found for session id: {} : {}", sessionID.toString(), sessionNotFound);
        }
    }

    private boolean isIncrementalRefreshRequested(int mdUpdateTypeValue) {
        return mdUpdateTypeValue == 1;
    }

    private boolean isBidEntry(char mdEntryTypeValue) {
        return mdEntryTypeValue == '0';
    }

    private boolean isSpotRequest(String settlTypeValue) {
        return "0".equals(settlTypeValue);
    }

    private boolean isSetField(Group group, int field) {
        return group.isSetField(field);
    }

}
