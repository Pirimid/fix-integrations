package com.pirimid.fxFix;

import com.pirimid.uitility.RequestGenerator;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import quickfix.*;
import quickfix.field.SenderCompID;
import quickfix.field.TargetCompID;
import quickfix.fix42.MarketDataRequest;
import quickfix.fix42.MarketDataSnapshotFullRefresh;

import static org.mockito.Mockito.times;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Session.class)
public class ResponseSenderTest extends TestCase {

    private ResponseSender responseSender = new ResponseSender();

    private static final String TEST_SENDER_COMP_ID = "FX-FIX-ACCEPTOR-TEST";
    private static final String TEST_TARGET_COMP_ID = "FX-FIX-INITIATOR-TEST";
    private SessionID sessionID = new SessionID(FixVersions.BEGINSTRING_FIX42, TEST_SENDER_COMP_ID, TEST_TARGET_COMP_ID);

    @Before
    public void setUp() {
        SocketAcceptor socketAcceptor = null;
        try {
            SessionSettings executorSettings = new SessionSettings(
                    "./acceptorSettingsTest.txt");
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
    public void testSendMarketDataFullRefresh() throws Exception {
        MarketDataRequest marketDataRequest = RequestGenerator.generateDummyMarketDataSpotRequest();
        double price = 2;
        PowerMockito.spy(Session.class);

        responseSender.sendMarketDataFullRefreshToClient(marketDataRequest, sessionID, price);

        //Assert
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        ArgumentCaptor<SessionID> sessionIdArgumentCaptor = ArgumentCaptor.forClass(SessionID.class);

        PowerMockito.verifyStatic(Session.class, times(1));
        Session.sendToTarget(messageArgumentCaptor.capture(), sessionIdArgumentCaptor.capture());

        Message returnedMessage = messageArgumentCaptor.getValue();
        SessionID returnedSessionId = sessionIdArgumentCaptor.getValue();
        Message.Header header = returnedMessage.getHeader();

        assertTrue("Messge which sends back to initiator/client need be an instance of MarketDataFullRefresh",
                returnedMessage instanceof MarketDataSnapshotFullRefresh);
        assertEquals("Message which sends back to initator/clinet should contain the same Target Component Id which test session contains",
                TEST_TARGET_COMP_ID, header.getField(new TargetCompID()).getValue());
        assertEquals("Message which sends back to initator/clinet should contain the same Sender Component Id which test session contains",
                TEST_SENDER_COMP_ID, header.getField(new SenderCompID()).getValue());
        assertEquals("SesssionID used while sending message back to initiator/client must be the same SessionID which it gets from request",
                sessionID, returnedSessionId);
    }
}
