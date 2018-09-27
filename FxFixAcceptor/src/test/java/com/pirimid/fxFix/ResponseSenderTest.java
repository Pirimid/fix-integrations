package com.pirimid.fxFix;

import com.pirimid.utils.RequestGenerator;
import com.pirimid.utils.Constants;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import quickfix.*;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.field.*;
import quickfix.fix44.*;

import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Session.class)
public class ResponseSenderTest extends TestCase {

    private ResponseSender responseSender = new ResponseSender();
    private SocketAcceptor socketAcceptor = null;

    private static final String TEST_CONFIG_FILE_NAME = "./acceptorSettingsTest.txt";
    private static final String TEST_SENDER_COMP_ID = "FX-FIX-ACCEPTOR-TEST";
    private static final String TEST_TARGET_COMP_ID = "FX-FIX-INITIATOR-TEST";

    private SessionID sessionID = new SessionID(FixVersions.BEGINSTRING_FIX44, TEST_SENDER_COMP_ID, TEST_TARGET_COMP_ID);

    @Before
    public void setUp() {
        try {
            SessionSettings executorSettings = new SessionSettings(TEST_CONFIG_FILE_NAME);
            Application application = new FxFixAcceptor();
            FileStoreFactory fileStoreFactory = new FileStoreFactory(
                    executorSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            FileLogFactory fileLogFactory = new FileLogFactory(executorSettings);
            socketAcceptor = new SocketAcceptor(application, fileStoreFactory,
                    executorSettings, fileLogFactory, messageFactory);
            socketAcceptor.start();
        } catch (ConfigError configError) {
            configError.printStackTrace();
        }
    }

    @Test
    public void testSendMarketDataFullRefresh_Spot() throws Exception {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Spot_FullRefresh();
        PowerMockito.spy(Session.class);

        responseSender.sendMarketDataFullRefreshToClient(marketDataRequest, sessionID);

        //Assert
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        ArgumentCaptor<SessionID> sessionIdArgumentCaptor = ArgumentCaptor.forClass(SessionID.class);

        PowerMockito.verifyStatic(Session.class, times(1));
        Session.sendToTarget(messageArgumentCaptor.capture(), sessionIdArgumentCaptor.capture());

        Message returnedMessage = messageArgumentCaptor.getValue();
        SessionID returnedSessionId = sessionIdArgumentCaptor.getValue();
        Message.Header header = returnedMessage.getHeader();

        assertMarketDataSnapshotFullRefresh(returnedMessage);
        assert_Sender_Target_Session(returnedSessionId, header);
        assertSpotRequest(returnedMessage);
        assertMDRequestId(marketDataRequest, returnedMessage);
    }
    @Test
    public void testSendMarketDataFullRefresh_Fwd() throws Exception {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Fwd_FullRefresh();
        PowerMockito.spy(Session.class);

        responseSender.sendMarketDataFullRefreshToClient(marketDataRequest, sessionID);

        //Assert
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        ArgumentCaptor<SessionID> sessionIdArgumentCaptor = ArgumentCaptor.forClass(SessionID.class);

        PowerMockito.verifyStatic(Session.class, times(1));
        Session.sendToTarget(messageArgumentCaptor.capture(), sessionIdArgumentCaptor.capture());

        Message returnedMessage = messageArgumentCaptor.getValue();
        SessionID returnedSessionId = sessionIdArgumentCaptor.getValue();
        Message.Header header = returnedMessage.getHeader();

        assertMarketDataSnapshotFullRefresh(returnedMessage);
        assert_Sender_Target_Session(returnedSessionId, header);
        assertForwardOrNDFRequest(returnedMessage);
        assertMDRequestId(marketDataRequest, returnedMessage);
    }

    @Test
    public void testSendMarketDataFullRefresh_NDF() throws Exception {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_NDF_FullRefresh();
        PowerMockito.spy(Session.class);

        responseSender.sendMarketDataFullRefreshToClient(marketDataRequest, sessionID);

        //Assert
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        ArgumentCaptor<SessionID> sessionIdArgumentCaptor = ArgumentCaptor.forClass(SessionID.class);

        PowerMockito.verifyStatic(Session.class, times(1));
        Session.sendToTarget(messageArgumentCaptor.capture(), sessionIdArgumentCaptor.capture());

        Message returnedMessage = messageArgumentCaptor.getValue();
        SessionID returnedSessionId = sessionIdArgumentCaptor.getValue();
        Message.Header header = returnedMessage.getHeader();

        assertMarketDataSnapshotFullRefresh(returnedMessage);
        assert_Sender_Target_Session(returnedSessionId, header);
        assertNDFRequest(returnedMessage);
        assertMDRequestId(marketDataRequest, returnedMessage);
    }

    @Test
    public void testSendMarketDataIncrementalRefresh_Spot() throws Exception {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Spot_IncrementalRefresh();
        PowerMockito.spy(Session.class);

        responseSender.sendMarketDataIncrementalRefreshToClient(marketDataRequest, sessionID);

        //Assert
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        ArgumentCaptor<SessionID> sessionIdArgumentCaptor = ArgumentCaptor.forClass(SessionID.class);

        PowerMockito.verifyStatic(Session.class, times(1));
        Session.sendToTarget(messageArgumentCaptor.capture(), sessionIdArgumentCaptor.capture());

        Message returnedMessage = messageArgumentCaptor.getValue();
        SessionID returnedSessionId = sessionIdArgumentCaptor.getValue();
        Message.Header header = returnedMessage.getHeader();

        assertMarketDataIncrementalRefresh(returnedMessage);
        assert_Sender_Target_Session(returnedSessionId, header);
        assertSpotRequest(returnedMessage);
        assertMDRequestId(marketDataRequest, returnedMessage);
    }

    @Test
    public void testSendMarketDataIncrementalRefresh_Fwd() throws Exception {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_Fwd_IncrementalRefresh();
        PowerMockito.spy(Session.class);

        responseSender.sendMarketDataIncrementalRefreshToClient(marketDataRequest, sessionID);

        //Assert
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        ArgumentCaptor<SessionID> sessionIdArgumentCaptor = ArgumentCaptor.forClass(SessionID.class);

        PowerMockito.verifyStatic(Session.class, times(1));
        Session.sendToTarget(messageArgumentCaptor.capture(), sessionIdArgumentCaptor.capture());

        Message returnedMessage = messageArgumentCaptor.getValue();
        SessionID returnedSessionId = sessionIdArgumentCaptor.getValue();
        Message.Header header = returnedMessage.getHeader();

        assertMarketDataIncrementalRefresh(returnedMessage);
        assert_Sender_Target_Session(returnedSessionId, header);
        assertForwardOrNDFRequest(returnedMessage);
        assertMDRequestId(marketDataRequest, returnedMessage);
    }

    @Test
    public void testSendMarketDataIncrementalRefresh_NDF() throws Exception {
        MarketDataRequest marketDataRequest = RequestGenerator.generateMarketDataRequest_NDF_IncrementalRefresh();
        PowerMockito.spy(Session.class);

        responseSender.sendMarketDataIncrementalRefreshToClient(marketDataRequest, sessionID);

        //Assert
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        ArgumentCaptor<SessionID> sessionIdArgumentCaptor = ArgumentCaptor.forClass(SessionID.class);

        PowerMockito.verifyStatic(Session.class, times(1));
        Session.sendToTarget(messageArgumentCaptor.capture(), sessionIdArgumentCaptor.capture());

        Message returnedMessage = messageArgumentCaptor.getValue();
        SessionID returnedSessionId = sessionIdArgumentCaptor.getValue();
        Message.Header header = returnedMessage.getHeader();

        assertMarketDataIncrementalRefresh(returnedMessage);
        assert_Sender_Target_Session(returnedSessionId, header);
        assertNDFRequest(returnedMessage);
        assertMDRequestId(marketDataRequest, returnedMessage);
    }

    @Test
    public void testSendExecutionReport() throws Exception {
        NewOrderSingle newOrderSingle = RequestGenerator.generateNewOrderSingle();
        PowerMockito.spy(Session.class);

        responseSender.sendExecutionReportToClient(newOrderSingle, sessionID);

        //Assert
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        ArgumentCaptor<SessionID> sessionIdArgumentCaptor = ArgumentCaptor.forClass(SessionID.class);

        PowerMockito.verifyStatic(Session.class, times(1));
        Session.sendToTarget(messageArgumentCaptor.capture(), sessionIdArgumentCaptor.capture());

        Message returnedMessage = messageArgumentCaptor.getValue();
        SessionID returnedSessionId = sessionIdArgumentCaptor.getValue();
        Message.Header header = returnedMessage.getHeader();

        assertExecutionReportRequest(returnedMessage);
        assert_Sender_Target_Session(returnedSessionId, header);
        assertClientOrderId(newOrderSingle, returnedMessage);
    }

    private void assertMarketDataSnapshotFullRefresh(Message returnedMessage) {
        assertTrue("Message which sends back to initiator/client need be an instance of MarketDataSnapshotFullRefresh",
                returnedMessage instanceof MarketDataSnapshotFullRefresh);
    }

    private void assertMarketDataIncrementalRefresh(Message returnedMessage) {
        assertTrue("Message which sends back to initiator/client need be an instance of MarketDataIncrementalRefresh",
                returnedMessage instanceof MarketDataIncrementalRefresh);
    }

    private void assertExecutionReportRequest(Message returnedMessage) {
        assertTrue("Message which sends back to initiator/client need be an instance of ExecutionReport",
                returnedMessage instanceof ExecutionReport);
    }

    private void assert_Sender_Target_Session(SessionID returnedSessionId, Message.Header header) throws FieldNotFound {
        assertTargetComponent(header);
        assertSenderComponent(header);
        assertSession(returnedSessionId);
    }

    private void assertSession(SessionID returnedSessionId) {
        assertEquals("SesssionID used while sending message back to initiator/client must be the same SessionID which it gets from request",
                sessionID, returnedSessionId);
    }

    private void assertSenderComponent(Message.Header header) throws FieldNotFound {
        assertEquals("Message which sends back to initiator/client should contain the same Sender Component Id which test session contains",
                TEST_SENDER_COMP_ID, header.getField(new SenderCompID()).getValue());
    }

    private void assertTargetComponent(Message.Header header) throws FieldNotFound {
        assertEquals("Message which sends back to initiator/client should contain the same Target Component Id which test session contains",
                TEST_TARGET_COMP_ID, header.getField(new TargetCompID()).getValue());
    }

    private void assertSpotRequest(Message returnedMessage) throws FieldNotFound {
        assertEquals("For Spot request, settl type needs to be 0", SettlType.REGULAR, returnedMessage.getField(new SettlType()).getValue());
    }

    private void assertForwardOrNDFRequest(Message returnedMessage) throws FieldNotFound {
        assertEquals("For FWD/NDF request, settl type needs to be 6", SettlType.FUTURE, returnedMessage.getField(new SettlType()).getValue());
    }

    private void assertNDFRequest(Message returnedMessage) throws FieldNotFound {
        assertForwardOrNDFRequest(returnedMessage);
        assertEquals("For NDF request, value for field NDF needs to be 1", '1', returnedMessage.getField(new CharField(Constants.NDF)).getValue());
    }

    private void assertMDRequestId(MarketDataRequest order, Message returnedMessage) {
        String MDRequestId = "";
        String returnedMDRequestId = "";
        try {
            MDRequestId = order.getMDReqID().getValue();
            returnedMDRequestId = returnedMessage.getField(new MDReqID()).getValue();
        } catch (FieldNotFound fieldNotFound) {
            fail("Field " + fieldNotFound.field + " must be available");
        }
        assertEquals("Market Data Request Id of MarketDataRequest and MarketDataRequest's Response must be same", MDRequestId, returnedMDRequestId);
    }

    private void assertClientOrderId(NewOrderSingle newOrderSingle, Message returnedMessage) {
        String clientOrderId = "";
        String executionReportOrderId = "";
        try {
            clientOrderId = newOrderSingle.getClOrdID().getValue();
            executionReportOrderId = returnedMessage.getField(new ClOrdID()).getValue();
        } catch (FieldNotFound fieldNotFound) {
            fail("Field " + fieldNotFound.field + " must be available");
        }
        assertEquals("Client order id of NewOrderSingle request and ExecutionReport must be same", clientOrderId, executionReportOrderId);
    }

    @After
    public void cleanUp() {
        socketAcceptor.stop();
    }
}
