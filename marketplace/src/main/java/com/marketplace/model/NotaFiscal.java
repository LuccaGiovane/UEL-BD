package com.marketplace.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class NotaFiscal {

    private int usuarioId;
    private double valorTotal;
    private LocalDateTime dtPagamento;
    private List<Midia> alugadas;
    private List<Midia> compradas;

}
