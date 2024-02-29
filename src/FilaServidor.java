import dto.TransactionDto;
import models.ConnectionStatus;
import service.ConnectionService;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//class RequestItem{
//    Socket clienteSocket;
//    String mensagemReq;
//    public RequestItem(Socket clienteSocket, String mensagemReq){
//        this.clienteSocket = clienteSocket;
//        this.mensagemReq = mensagemReq;
//    }
//}
public class FilaServidor {

    private static final int PORTA = 12345;

    private static final int PORTA_SERVIDOR_DEBITO = 12347;

    private static final int PORTA_SERVIDOR_CREDITO = 12346;

    private static BlockingQueue<String> fila = new LinkedBlockingQueue<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORTA)) {
            System.out.println("Servidor de Filas iniciado na porta " + PORTA);

            while (true) {
                Socket socketCliente = serverSocket.accept();
                new Thread(new ClienteHandler(socketCliente)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
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

                while (true) {
                    TransactionDto dto = (TransactionDto) in.readObject();
                    fila.offer(dto.getQueue());

                    if (dto.getQueue().equalsIgnoreCase("debito")) {
                        connectionService.sendRequestToServer(PORTA_SERVIDOR_DEBITO, dto.getValue());
                    }

//                    } else if (mensagem.equalsIgnoreCase("credito")) {
//                        res = sendCredito();
//                    } else if ((mensagem.equalsIgnoreCase("retorno-credito"))) {
//                        //fazer a lógica de rotorno do servidor de crédito
//                    } else if ((mensagem.equalsIgnoreCase("retorno-debito"))) {
//                        System.out.println("Recebido retorno do servidor de débito");
//                    }
                    //                System.out.println(res);
                    fila.poll();
                    out.writeObject(dto);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        private String sendCredito() {
            try (
                    Socket socketCredito = new Socket("localhost", 12346);
                    ObjectOutputStream outCredito = new ObjectOutputStream(socketCredito.getOutputStream());
                    ObjectInputStream inCredito = new ObjectInputStream(socketCredito.getInputStream());
            ) {
                String mensagemDaFila = fila.poll();
                outCredito.writeObject("Requisição para o servidor de crédito: " + mensagemDaFila);
                String resOp = (String) inCredito.readObject();
                return resOp;
            } catch (Exception e) {
                e.printStackTrace();
                return ConnectionStatus.ERROR;
            }
        }

        private String sendDebito() {
            try (Socket socketDebito = new Socket("localhost", 12347);
                 ObjectOutputStream outDebito = new ObjectOutputStream(socketDebito.getOutputStream());
                 ObjectInputStream inDebito = new ObjectInputStream(socketDebito.getInputStream())) {
                String mensagemDaFila = fila.poll();
                outDebito.writeObject("Requisição para o servidor de débito: " + mensagemDaFila);
                String resOp = (String) inDebito.readObject();
                return resOp;
            } catch (Exception e) {
                e.printStackTrace();
                return ConnectionStatus.ERROR;
            }
        }
    }
}