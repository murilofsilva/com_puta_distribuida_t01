package service;

import dto.TransactionDto;
import models.Pessoa;
import models.TransactionStatus;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.stream.Collectors;

public class CreditoService {
    private static final int PORTA = 12346;

    private static final List<Pessoa> pessoas = Pessoa.getDefaultPersons();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("SERVIDOR DE CRÉDITO INICIADO");

            while (true) {
                Socket socketCliente = serverSocket.accept();
                new Thread(new CreditoHandler(socketCliente)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class CreditoHandler implements Runnable {
        private final Socket socket;

        public CreditoHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    ObjectInputStream inCredito = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream outCredito = new ObjectOutputStream(socket.getOutputStream())
            ) {

                TransactionDto dto = (TransactionDto) inCredito.readObject();
                Pessoa usuarioOperacao = pessoas.stream().filter(p -> p.getNome().equalsIgnoreCase(dto.getUsuarioOperacao())).collect(Collectors.toList()).get(0);

                if (usuarioOperacao.getContaCredito().compareTo(dto.getValue()) < 0) {
                    throw new ValidationException("O saldo do " + usuarioOperacao.getNome() + " é insulficiente!");
                }

                usuarioOperacao.setContaCredito(usuarioOperacao.getContaCredito().subtract(dto.getValue()));

                System.out.println("Operação de crédito concluída para " + usuarioOperacao.getNome());
                System.out.println("Agora ele possuí " + usuarioOperacao.getContaCredito() + " de limite na conta");

                outCredito.writeObject(TransactionStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }
}