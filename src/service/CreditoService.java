package service;

import models.ConnectionStatus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class CreditoService{
    private static final int PORTA = 12346;

    public static void main(String[] args){
        try(ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("SERVIDOR DE CRÉDITO INICIADO");

            while(true){
                Socket socketCliente = serverSocket.accept();
                new Thread(new CreditoHandler(socketCliente)).start();
            }

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private static class CreditoHandler implements Runnable{
        private final Socket socket;
        public CreditoHandler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run(){
            try(
                    ObjectInputStream inCredito = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
            ) {

                String requestCredito = (String) inCredito.readObject();
                System.out.println(requestCredito);

                out.writeObject(ConnectionStatus.OK);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}