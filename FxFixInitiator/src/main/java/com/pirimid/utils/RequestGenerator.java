package com.pirimid.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.CharField;
import quickfix.FieldNotFound;
import quickfix.Group;
import quickfix.field.*;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.Message;
import quickfix.fix44.NewOrderSingle;

import java.util.Date;

public class RequestGenerator {

    private static final Logger logger = LoggerFactory.getLogger(RequestGenerator.class);

    public static final String SAMPLE_MATURITY_DATE = "20181005";
    public static final String SAMPLE_SETTL_DATE = "20181001";

    public static MarketDataRequest generateMarketDataRequest_Spot_FullRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(Constants.EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.REGULAR));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_Fwd_FullRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.FULL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(Constants.EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.FUTURE));
        noRelatedSym.setField(new SettlDate(SAMPLE_SETTL_DATE));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_NDF_FullRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataRequest();
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
        MarketDataRequest marketDataRequest = generateMarketDataRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(Constants.EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.REGULAR));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_Fwd_IncrementalRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataRequest();
        marketDataRequest.set(new MDUpdateType(MDUpdateType.INCREMENTAL_REFRESH));
        MarketDataRequest.NoRelatedSym noRelatedSym = new MarketDataRequest.NoRelatedSym();
        noRelatedSym.set(new Symbol(Constants.EUR_USD));
        noRelatedSym.setField(new SettlType(SettlType.FUTURE));
        noRelatedSym.setField(new SettlDate(SAMPLE_SETTL_DATE));
        marketDataRequest.addGroup(noRelatedSym);
        return marketDataRequest;
    }

    public static MarketDataRequest generateMarketDataRequest_NDF_IncrementalRefresh() {
        MarketDataRequest marketDataRequest = generateMarketDataRequest();
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

    private static MarketDataRequest generateMarketDataRequest() {
        MarketDataRequest marketDataRequest = new MarketDataRequest(
                new MDReqID(String.valueOf(Helper.generateNextRequestId())),
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

    public static NewOrderSingle generateNewOrderSingle(Message message) {
        NewOrderSingle newOrderSingle = null;
        try {
            String orderId = "1234";
            if (message.isSetField(MDReqID.FIELD)) {
                orderId = message.getField(new MDReqID()).getValue();
            }
            newOrderSingle = new NewOrderSingle(new ClOrdID(orderId), new Side(Side.BUY), new TransactTime(new Date()), new OrdType(OrdType.FOREX_PREVIOUSLY_QUOTED));
            if (message.isSetField(new QuoteEntryID())) {
                newOrderSingle.set(new QuoteID(message.getField(new QuoteEntryID()).getValue()));
            } else {
                newOrderSingle.set(new QuoteID("QuoteID"));
            }
            newOrderSingle.set(new Account("1432"));
            Symbol symbol = (Symbol) message.getField(new Symbol());
            newOrderSingle.setField(symbol);
            String CFICodeValue = getCFICodeValue(message);
            newOrderSingle.set(new CFICode(CFICodeValue));
            newOrderSingle.set(new OrderQty(15));
            MDEntryPx mdEntryPx = new MDEntryPx();
            Double value = message.getGroup(1, NoMDEntries.FIELD).getField(mdEntryPx).getValue();
            newOrderSingle.set(new Price(value));
            String currencyISOCode = symbol.getValue().split("/")[0];
            newOrderSingle.set(new Currency(currencyISOCode));
            newOrderSingle.set(new TimeInForce(TimeInForce.FILL_OR_KILL));
            if (message.isSetField(SettlType.FIELD)) {
                newOrderSingle.setField(message.getField(new SettlType()));
            }
            if (message.isSetField(SettlDate.FIELD)) {
                newOrderSingle.setField(message.getField(new SettlDate()));
            }
            if (message.isSetField(MaturityDate.FIELD)) {
                newOrderSingle.setField(message.getField(new MaturityDate()));
            }
        } catch (FieldNotFound fieldNotFound) {
            logger.error("Field {} not found", fieldNotFound.field, fieldNotFound);
        }
        return newOrderSingle;
    }

    private static String getCFICodeValue(Message message) throws FieldNotFound {
        String CFICodeValue = "";
        String settlTypeValue = "0";
        if (message.isSetField(SettlType.FIELD)) {
            settlTypeValue = message.getField(new SettlType()).getValue();
        }
        char NDFValue = '0';
        if (message.isSetField(Constants.NDF)) {
            NDFValue = message.getField(new CharField(Constants.NDF)).getValue();
        }
        if (isNDFOrder(settlTypeValue, NDFValue)) {
            CFICodeValue = Constants.CFI_CODE_NDF;
        } else if (SettlType.FUTURE.equals(settlTypeValue)) {
            CFICodeValue = Constants.CFI_CODE_FWD;
        } else if (SettlType.REGULAR.equals(settlTypeValue)) {
            CFICodeValue = Constants.CFI_CODE_SPOT;
        } else {
            CFICodeValue = Constants.CFI_CODE_SWAP;
        }
        return CFICodeValue;
    }

    private static boolean isNDFOrder(String settlTypeValue, char NDFValue) {
        return '1' == NDFValue && SettlType.FUTURE.equals(settlTypeValue);
    }

    private boolean isSetField(Group group, int field) {
        return group.isSetField(field);
    }

}
