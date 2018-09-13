package com.pirimid.fxFix;

import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.MarketDataRequest;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FxFixAcceptorTest extends TestCase {

    @InjectMocks
    Application application = new FxFixAcceptor();
    @Mock
    private ResponseSender responseSender;

    private static final String TEST_SENDER_COMP_ID = "FX-FIX-ACCEPTOR-TEST";
    private static final String TEST_TARGET_COMP_ID = "FX-FIX-INITIATOR-TEST";
    private SessionID sessionID = new SessionID(FixVersions.BEGINSTRING_FIX42, TEST_SENDER_COMP_ID, TEST_TARGET_COMP_ID);

    @Test
    public void testMarketDataSpotRequest() {
        MarketDataRequest marketDataSpotRequest = generateTestMarketDataSpotRequest();

        try {
            application.fromApp(marketDataSpotRequest, sessionID);
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        } catch (IncorrectDataFormat incorrectDataFormat) {
            incorrectDataFormat.printStackTrace();
        } catch (IncorrectTagValue incorrectTagValue) {
            incorrectTagValue.printStackTrace();
        } catch (UnsupportedMessageType unsupportedMessageType) {
            unsupportedMessageType.printStackTrace();
        }

        verify(responseSender).startSendingMarketDataFullRefresh(marketDataSpotRequest, sessionID);

    }

    private MarketDataRequest generateTestMarketDataSpotRequest() {
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
