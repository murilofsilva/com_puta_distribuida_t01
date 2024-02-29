import dto.TransactionDto;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.Scanner;

public class FilaCliente {

    private static final String SERVIDOR_IP = "localhost";
    private static final int PORTA = 12345;

    public static void main(String[] args) {
        try (Socket socket = new Socket(SERVIDOR_IP, PORTA);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)
        ) {
            int count = 0;
            while (count < 100) {
                count++;
                System.out.print("Deseja fazer uma operação de débito ou crédito? ");
                String operation = scanner.nextLine();
                if (!operation.equalsIgnoreCase("debito") && !operation.equalsIgnoreCase("credito")) {
                    throw new ValidationException("Opção inválida!");
                }
                System.out.print("Qual o valor da operação? ");
                BigDecimal value = scanner.nextBigDecimal();
                scanner.nextLine();
                TransactionDto dto = buildDto(operation, value);
                out.writeObject(dto);
       //         String respostaEnvio = (String) in.readObject();
      //          System.out.println("Resposta do servidor: " + respostaEnvio);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    private static TransactionDto buildDto(String operation, BigDecimal value) {
        TransactionDto dto = new TransactionDto();
        dto.setQueue(operation);
        dto.setOperation(operation);
        dto.setValue(value);

        return dto;
    }
}