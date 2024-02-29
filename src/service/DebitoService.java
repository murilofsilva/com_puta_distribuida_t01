package service;

import models.TransactionStatus;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;

public class DebitoService{
    private static final int PORTA = 12347;

    public static void main(String[] args){
        try(ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("SERVIDOR DE DÉBITO INICIADO");

            while(true){
                Socket socketCliente = serverSocket.accept();
                new Thread(new DebitoHandler(socketCliente)).start();
            }

        }catch (IOException e){
            e.printStackTrace();
        }

    }

    private static class DebitoHandler implements Runnable{
        private final Socket socket;
        public DebitoHandler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run(){
            try(
                    ObjectInputStream inDebito = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream outDebito = new ObjectOutputStream(socket.getOutputStream())
            ) {

                BigDecimal valor = (BigDecimal) inDebito.readObject();
                System.out.println(valor);

             //   outDebito.writeObject("retorno-debito");
                outDebito.writeObject(TransactionStatus.OK);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}