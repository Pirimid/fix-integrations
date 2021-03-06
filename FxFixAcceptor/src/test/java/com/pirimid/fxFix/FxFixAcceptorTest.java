package com.pirimid.fxFix;

import com.pirimid.utils.RequestGenerator;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import quickfix.*;
import quickfix.fix44.MarketDataRequest;
import quickfix.fix44.NewOrderSingle;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

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
    public void testMarketDataRequest() {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Spot_FullRefresh();

        try {
            application.fromApp(marketDataRequest, sessionID);
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        } catch (IncorrectDataFormat incorrectDataFormat) {
            incorrectDataFormat.printStackTrace();
        } catch (IncorrectTagValue incorrectTagValue) {
            incorrectTagValue.printStackTrace();
        } catch (UnsupportedMessageType unsupportedMessageType) {
            unsupportedMessageType.printStackTrace();
        }

        verify(responseSender, times(1)).subscribeNewMarketDataRequest(marketDataRequest);
        verify(responseSender, times(1)).startSendingMarketDataRefreshResponseIfNotStarted(sessionID);
    }

    @Test
    public void testNewOrderSingleRequest() {
        NewOrderSingle newOrderSingle = RequestGenerator.generateNewOrderSingle();

        try {
            application.fromApp(newOrderSingle, sessionID);
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        } catch (IncorrectDataFormat incorrectDataFormat) {
            incorrectDataFormat.printStackTrace();
        } catch (IncorrectTagValue incorrectTagValue) {
            incorrectTagValue.printStackTrace();
        } catch (UnsupportedMessageType unsupportedMessageType) {
            unsupportedMessageType.printStackTrace();
        }

        try {
            String reqId = newOrderSingle.getClOrdID().getValue();
            verify(responseSender, times(1)).unsubscribeMarketDataRequest(reqId);
        } catch (FieldNotFound fieldNotFound) {
            fail("Field " + fieldNotFound.field + " must be available");
        }
        verify(responseSender, times(1)).sendExecutionReportToClient(newOrderSingle, sessionID);
    }

    @Test
    public void testMarketDataRequest_Unsubscribe() {
        MarketDataRequest marketDataRequestUnsubscribe = RequestGenerator.generateMarketDataRequest_Unsubscribe();

        try {
            application.fromApp(marketDataRequestUnsubscribe, sessionID);
        } catch (FieldNotFound fieldNotFound) {
            fieldNotFound.printStackTrace();
        } catch (IncorrectDataFormat incorrectDataFormat) {
            incorrectDataFormat.printStackTrace();
        } catch (IncorrectTagValue incorrectTagValue) {
            incorrectTagValue.printStackTrace();
        } catch (UnsupportedMessageType unsupportedMessageType) {
            unsupportedMessageType.printStackTrace();
        }

        try {
            String reqId = marketDataRequestUnsubscribe.getMDReqID().getValue();
            verify(responseSender, times(1)).unsubscribeMarketDataRequest(reqId);
        } catch (FieldNotFound fieldNotFound) {
            fail("Field " + fieldNotFound.field + " must be available");
        }
    }

}
