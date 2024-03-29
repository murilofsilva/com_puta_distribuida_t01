import dto.TransactionDto;
import service.ConnectionService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class FilaServidor {

    private static final int PORTA = 12345;

    private static final int PORTA_SERVIDOR_DEBITO = 12347;

    private static final int PORTA_SERVIDOR_CREDITO = 12346;

    private static BlockingQueue<Socket> fila = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor de Filas iniciado na porta " + PORTA);
            Thread queueAttendentThread = new Thread();
            while (true) {
                Socket socketCliente = serverSocket.accept();
                fila.offer(socketCliente);
                if (!queueAttendentThread.isAlive()) {
                    queueAttendentThread = new Thread(new QueueAttendent());
                    queueAttendentThread.start();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class QueueAttendent implements Runnable {
        public void run() {
            while (!fila.isEmpty()) {
                new Thread(new ClienteHandler(fila.poll())).start();
            }
        }
    }

    private static class ClienteHandler implements Runnable {
        private Socket socket;

        public ClienteHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream())
            ) {
                ConnectionService connectionService = new ConnectionService();

                TransactionDto dto = (TransactionDto) in.readObject();
                String res = "";

                if (dto.getQueue().equalsIgnoreCase("debito")) {
                    res = connectionService.sendRequestToServer(PORTA_SERVIDOR_DEBITO, dto);
                }
                if (dto.getQueue().equalsIgnoreCase("credito")) {
                    res = connectionService.sendRequestToServer(PORTA_SERVIDOR_CREDITO, dto);
                }
                dto.setStatus(res);
                out.writeObject(dto);

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}