package com.pirimid.uitility;

import com.pirimid.utils.FieldConstants;
import quickfix.CharField;
import quickfix.field.*;
import quickfix.fix44.MarketDataRequest;

public class RequestGenerator {


    public static final String EUR_USD = "EUR/USD";
    public static final String SAMPLE_MATURITY_DATE = "20181005";
    public static final String SAMPLE_SETTL_DATE = "20181001";

    public static MarketDataRequest generateMarketDataRequest_Spot_FullRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.REGULAR));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_Fwd_FullRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.FUTURE));
        noRelatedSym.setField(new SettlDate(SAMPLE_SETTL_DATE));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_NDF_FullRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.FUTURE));
        noRelatedSym.setField(new SettlDate(SAMPLE_SETTL_DATE));
        noRelatedSym.setField(new CharField(FieldConstants.NDF, '1'));
        noRelatedSym.set(new MaturityDate(SAMPLE_MATURITY_DATE));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_Spot_IncrementalRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.REGULAR));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_Fwd_IncrementalRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.FUTURE));
        noRelatedSym.setField(new SettlDate(SAMPLE_SETTL_DATE));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_NDF_IncrementalRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.FUTURE));
        noRelatedSym.setField(new SettlDate(SAMPLE_SETTL_DATE));
        noRelatedSym.setField(new CharField(FieldConstants.NDF, '1'));
        noRelatedSym.set(new MaturityDate(SAMPLE_MATURITY_DATE));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    private static MarketDataRequest generateMarketDataRequest() {
        MarketDataRequest marketDataRequest = new MarketDataRequest(
                new MDReqID("11"),
                new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES),
                new MarketDepth(15)
        );
        marketDataRequest.set(new NoMDEntryTypes(2));
        marketDataRequest.set(new NoRelatedSym(1));
        MarketDataRequest.NoMDEntryTypes noMDEntryType1 = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryType1.set(new MDEntryType(MDEntryType.BID));
        marketDataRequest.addGroup(noMDEntryType1);
        MarketDataRequest.NoMDEntryTypes noMDEntryType2 = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryType2.set(new MDEntryType(MDEntryType.OFFER));
        marketDataRequest.addGroup(noMDEntryType2);
        return marketDataRequest;
    }

}
