package service;

import models.TransactionStatus;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;

public class ConnectionService {

    private static final String DEFAULT_PORT = "localhost";

    public String sendRequestToServer(int port, BigDecimal value) {
        try (Socket socketDebito = new Socket(DEFAULT_PORT, port);
             ObjectOutputStream outDebito = new ObjectOutputStream(socketDebito.getOutputStream());
             ObjectInputStream inDebito = new ObjectInputStream(socketDebito.getInputStream())) {
            outDebito.writeObject(value);
            String resOp = (String) inDebito.readObject();
            return resOp;
        } catch (Exception e) {
            e.printStackTrace();
            return TransactionStatus.ERROR;
        }
    }
}
