package com.pirimid.utils;

import quickfix.CharField;
import quickfix.field.*;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.NewOrderSingle;

import java.util.Date;

public class RequestGenerator {


    public static final String SAMPLE_MATURITY_DATE = "20181005";
    public static final String SAMPLE_SETTL_DATE = "20181001";

    public static MarketDataRequest generateMarketDataRequest_Spot_FullRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataSubscribeRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(Constants.EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.REGULAR));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_Fwd_FullRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataSubscribeRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(Constants.EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.FUTURE));
        noRelatedSym.setField(new SettlDate(SAMPLE_SETTL_DATE));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_NDF_FullRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataSubscribeRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(Constants.EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.FUTURE));
        noRelatedSym.setField(new SettlDate(SAMPLE_SETTL_DATE));
        noRelatedSym.setField(new CharField(Constants.NDF, '1'));
        noRelatedSym.set(new MaturityDate(SAMPLE_MATURITY_DATE));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_Spot_IncrementalRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataSubscribeRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(Constants.EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.REGULAR));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_Fwd_IncrementalRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataSubscribeRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(Constants.EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.FUTURE));
        noRelatedSym.setField(new SettlDate(SAMPLE_SETTL_DATE));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_NDF_IncrementalRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataSubscribeRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(Constants.EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.FUTURE));
        noRelatedSym.setField(new SettlDate(SAMPLE_SETTL_DATE));
        noRelatedSym.setField(new CharField(Constants.NDF, '1'));
        noRelatedSym.set(new MaturityDate(SAMPLE_MATURITY_DATE));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    private static MarketDataRequest generateMarketDataSubscribeRequest() {
        MarketDataRequest marketDataRequest = new MarketDataRequest(
                new MDReqID("11"),
                new SubscriptionRequestType(SubscriptionRequestType.SNAPSHOT_PLUS_UPDATES),
                new MarketDepth(15)
        );
        setDefaultFieldsOfMarketDataRequest(marketDataRequest);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_Unsubscribe() {
        MarketDataRequest marketDataRequest = generateMarketDataUnsubscribeRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(Constants.EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.REGULAR));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    private static MarketDataRequest generateMarketDataUnsubscribeRequest() {
        MarketDataRequest marketDataRequest = new MarketDataRequest(
                new MDReqID("11"),
                new SubscriptionRequestType(SubscriptionRequestType.DISABLE_PREVIOUS_SNAPSHOT_PLUS_UPDATE_REQUEST),
                new MarketDepth(15)
        );
        setDefaultFieldsOfMarketDataRequest(marketDataRequest);
        return marketDataRequest;
    }

    private static void setDefaultFieldsOfMarketDataRequest(MarketDataRequest marketDataRequest) {
        marketDataRequest.set(new NoMDEntryTypes(2));
        marketDataRequest.set(new NoRelatedSym(1));
        MarketDataRequest.NoMDEntryTypes noMDEntryType1 = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryType1.set(new MDEntryType(MDEntryType.BID));
        marketDataRequest.addGroup(noMDEntryType1);
        MarketDataRequest.NoMDEntryTypes noMDEntryType2 = new MarketDataRequest.NoMDEntryTypes();
        noMDEntryType2.set(new MDEntryType(MDEntryType.OFFER));
        marketDataRequest.addGroup(noMDEntryType2);
    }

    public static NewOrderSingle generateNewOrderSingle() {
        NewOrderSingle newOrderSingle = new NewOrderSingle(new ClOrdID("1234"), new Side(Side.BUY), new TransactTime(new Date()), new OrdType(OrdType.FOREX_PREVIOUSLY_QUOTED));
        newOrderSingle.set(new QuoteID("QuoteID"));
        newOrderSingle.set(new Account("1432"));
        newOrderSingle.setField(new Symbol(Constants.EUR_USD));
        newOrderSingle.set(new CFICode(Constants.CFI_CODE_SPOT));
        newOrderSingle.set(new OrderQty(15));
        MDEntryPx mdEntryPx = new MDEntryPx();
        newOrderSingle.set(new Price(1.16));
        String currencyISOCode = Constants.EUR_USD.split("/")[0];
        newOrderSingle.set(new Currency(currencyISOCode));
        newOrderSingle.set(new TimeInForce(TimeInForce.FILL_OR_KILL));
        return newOrderSingle;
    }

}
