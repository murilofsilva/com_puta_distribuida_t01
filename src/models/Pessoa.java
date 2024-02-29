package models;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class Pessoa {

    public Pessoa() {
    }

    public Pessoa(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    private Long id;
    private String nome;
    private BigDecimal contaDebito = new BigDecimal("3500");
    private BigDecimal contaCredito = new BigDecimal("5000");

    public Long getId() {
        return this.id;
    }

    public String getNome() {
        return this.nome;
    }

    public BigDecimal getContaDebito() {
        return this.contaDebito;
    }

    public BigDecimal getContaCredito() {
        return this.contaCredito;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContaDebito(BigDecimal valor) {
        this.contaDebito = valor;
    }

    public void setContaCredito(BigDecimal valor) {
        this.contaCredito = valor;
    }

    public static List<Pessoa> getDefaultPersons() {
        return Arrays.asList(new Pessoa(1L, "Murilo"),
                new Pessoa(2L, "Ludio"),
                new Pessoa(3L, "Neymar Jr"));
    }

}
