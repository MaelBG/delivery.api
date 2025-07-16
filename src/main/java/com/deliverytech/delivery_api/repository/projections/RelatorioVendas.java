package com.deliverytech.delivery_api.repository.projections; // Ajuste o pacote conforme sua estrutura

import java.math.BigDecimal;

public interface RelatorioVendas { //
    String getNomeRestaurante(); //
    BigDecimal getTotalVendas(); //
    Long getQuantidePedidos();   //
}

