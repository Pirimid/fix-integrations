package com.pirimid.fxFix;

import com.pirimid.utility.Helper;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.MarketDataRequest;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

import java.util.List;

public class ResponseSender {

    public void startSendingMarketDataFullRefresh(MarketDataRequest order, SessionID sessionID) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Double price = 1.16; // Sample price for EUR/USD
                while (Session.lookupSession(sessionID).hasResponder()) {
                    sendMarketDataFullRefreshToClient(order, sessionID, price);
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
}
