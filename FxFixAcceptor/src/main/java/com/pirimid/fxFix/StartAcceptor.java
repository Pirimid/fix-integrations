package com.pirimid.fxFix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quickfix.*;

public class StartAcceptor {

    private static final Logger logger = LoggerFactory.getLogger(StartAcceptor.class);
    private static final String CONFIG_FILE_NAME = "./acceptorSettings.txt";

    public static void main(String args[]) {
        SocketAcceptor socketAcceptor = null;
        try {
            SessionSettings executorSettings = new SessionSettings(CONFIG_FILE_NAME);
            Application application = new FxFixAcceptor();
            FileStoreFactory fileStoreFactory = new FileStoreFactory(
                    executorSettings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            FileLogFactory fileLogFactory = new FileLogFactory(executorSettings);
            socketAcceptor = new SocketAcceptor(application, fileStoreFactory,
                    executorSettings, fileLogFactory, messageFactory);
            socketAcceptor.start();
        } catch (ConfigError configError) {
            logger.error("Error while configuring session", configError);
        }
    }
}
