package com.pirimid.uitility;

import quickfix.field.*;
import quickfix.fix42.MarketDataRequest;

public class RequestGenerator {

    public static MarketDataRequest generateDummyMarketDataSpotRequest() {
        MarketDataRequest marketDataRequest = new MarketDataRequest(
                new MDReqID("11"),
                new SubscriptionRequestType('1'),
                new MarketDepth(15)
        );
        marketDataRequest.set(new MDUpdateType(0));
        marketDataRequest.set(new NoMDEntryTypes(2));
        marketDataRequest.set(new NoRelatedSym(1));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol("EUR/USD"));
        marketDataRequest.addGroup(noRelatedSym);
        MarketDataRequest.NoMDEntryTypes noMDEntryType1 = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryType1.set(new MDEntryType('0'));
        marketDataRequest.addGroup(noMDEntryType1);
        MarketDataRequest.NoMDEntryTypes noMDEntryType2 = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryType2.set(new MDEntryType('1'));
        marketDataRequest.addGroup(noMDEntryType2);
        return marketDataRequest;
    }
}
