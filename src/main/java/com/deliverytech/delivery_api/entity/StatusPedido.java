package com.deliverytech.delivery_api.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status possíveis de um pedido no sistema", enumAsRef = true)
public enum StatusPedido {

    @Schema(description = "Pedido foi criado mas ainda não foi confirmado pelo restaurante")
    PENDENTE("Pendente"),

    @Schema(description = "Pedido foi confirmado pelo restaurante e está sendo preparado")
    CONFIRMADO("Confirmado"),

    @Schema(description = "Pedido está sendo preparado na cozinha")
    PREPARANDO("Preparando"),

    @Schema(description = "Pedido saiu para entrega")
    SAIU_PARA_ENTREGA("Saiu para Entrega"),

    @Schema(description = "Pedido foi entregue com sucesso ao cliente")
    ENTREGUE("Entregue"),

    @Schema(description = "Pedido foi cancelado pelo cliente ou restaurante")
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}