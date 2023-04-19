package com.company;

import java.io.IOException;
import java.net.Socket;
import org.apache.log4j.Logger;

/**
 * class used for test purpose
 */
public class RawConnectionTest extends Thread {
    private final int port;

    static final Logger logger = Logger.getLogger(RawConnectionTest.class.getName());

    public RawConnectionTest(int port) {this.port = port;}

    @Override
    public void run() {
        while (true){
            try {
                Socket socket = new Socket("localhost", port);
                logger.info(socket);
            } catch (IOException ex) {
//            not empty
            }
        }
    }
}
