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

public class DebitoService {
    private static final int PORTA = 12347;

    private static final List<Pessoa> pessoas = Pessoa.getDefaultPersons();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("SERVIDOR DE DÉBITO INICIADO");

            while (true) {
                Socket socketCliente = serverSocket.accept();
                new Thread(new DebitoHandler(socketCliente)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static class DebitoHandler implements Runnable {
        private final Socket socket;

        public DebitoHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    ObjectInputStream inDebito = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream outDebito = new ObjectOutputStream(socket.getOutputStream())
            ) {

                TransactionDto dto = (TransactionDto) inDebito.readObject();
                Pessoa usuarioOperacao = pessoas.stream().filter(p -> p.getNome().equalsIgnoreCase(dto.getUsuarioOperacao())).collect(Collectors.toList()).get(0);

                if (usuarioOperacao.getContaDebito().compareTo(dto.getValue()) < 0) {
                    throw new ValidationException("O saldo do " + usuarioOperacao.getNome() + " é insuficiente!");
                }

                usuarioOperacao.setContaDebito(usuarioOperacao.getContaDebito().subtract(dto.getValue()));

                System.out.println("Operação de débito concluída para " + usuarioOperacao.getNome());
                System.out.println("Agora ele possuí " + usuarioOperacao.getContaDebito() + " reais");

                outDebito.writeObject(TransactionStatus.OK);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}