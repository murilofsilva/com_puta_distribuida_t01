import dto.TransactionDto;
import models.TransactionStatus;

import javax.swing.*;
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
        int count = 0;
        String[] opcoesOperacoes = {"Credito", "Debito"};

        while (count < 100) {
            try (Socket socket = new Socket(SERVIDOR_IP, PORTA); ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream()); ObjectInputStream in = new ObjectInputStream(socket.getInputStream())

            ) {
                count++;

                int operationIdx = JOptionPane.showOptionDialog(null, "Deseja fazer uma operação de débito ou crédito?", "Ops", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, opcoesOperacoes, opcoesOperacoes[0]);
                String operation = opcoesOperacoes[operationIdx];

                if (!operation.equalsIgnoreCase("credito") && !operation.equalsIgnoreCase("debito")) {
                    throw new ValidationException("Opção inválida!");
                }

                BigDecimal value = BigDecimal.ZERO;
                while(value.compareTo(BigDecimal.ZERO) != 1){
                    value = new BigDecimal(JOptionPane.showInputDialog(null, "Qual o valor da operação?", 0));
                }

                TransactionDto dto = buildDto(operation, value);
                out.writeObject(dto);

                TransactionDto res = (TransactionDto) in.readObject();
                if(res.getStatus().equals(TransactionStatus.ERROR)){
                    JOptionPane.showMessageDialog(null, "Nao foi possivel completar a operacao","Erro",  JOptionPane.ERROR_MESSAGE);
                }else{
                    JOptionPane.showMessageDialog(null, "A operacao foi realizada com sucesso","Sucesso",  JOptionPane.INFORMATION_MESSAGE);
                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
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