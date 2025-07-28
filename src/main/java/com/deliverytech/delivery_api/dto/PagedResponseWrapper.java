package com.deliverytech.delivery_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;
import java.util.List;
import lombok.Data; // Importe o Lombok Data
import lombok.NoArgsConstructor; // Importe o Lombok NoArgsConstructor
import lombok.AllArgsConstructor; // Importe o Lombok AllArgsConstructor (para PageInfo e PageLinks)

@Data // Gera getters, setters, toString, equals e hashCode
@NoArgsConstructor // Necessário para a desserialização JSON
@Schema(description = "Wrapper para respostas paginadas")
public class PagedResponseWrapper<T> {

    @Schema(description = "Lista de itens da página atual")
    private List<T> content;

    @Schema(description = "Informações de paginação")
    private PageInfo page;

    @Schema(description = "Links de navegação")
    private PageLinks links;

    public PagedResponseWrapper(Page<T> page) {
        this.content = page.getContent();
        this.page = new PageInfo(
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
        this.links = new PageLinks(page);
    }

    @Data // Gera getters, setters, toString, equals e hashCode para a classe aninhada
    @NoArgsConstructor // Gera construtor sem argumentos
    @AllArgsConstructor // Gera construtor com todos os argumentos
    @Schema(description = "Informações de paginação")
    public static class PageInfo {
        @Schema(description = "Número da página atual (base 0)", example = "0")
        private int number;
        @Schema(description = "Tamanho da página", example = "10")
        private int size;
        @Schema(description = "Total de elementos", example = "50")
        private long totalElements;
        @Schema(description = "Total de páginas", example = "5")
        private int totalPages;
        @Schema(description = "É a primeira página", example = "true")
        private boolean first;
        @Schema(description = "É a última página", example = "false")
        private boolean last;
    }

    @Data // Gera getters, setters, toString, equals e hashCode para a classe aninhada
    @NoArgsConstructor // Gera construtor sem argumentos
    @Schema(description = "Links de navegação")
    public static class PageLinks {
        @Schema(description = "Link para primeira página")
        private String first;
        @Schema(description = "Link para última página")
        private String last;
        @Schema(description = "Link para próxima página")
        private String next;
        @Schema(description = "Link para página anterior")
        private String prev;

        public PageLinks(Page<?> page) {
            String baseUrl = "/api/restaurantes"; // Base URL para links - AJUSTE CONFORME SEU ENDPOINT BASE DO CONTROLLER
            this.first = baseUrl + "?page=0&size=" + page.getSize();
            this.last = baseUrl + "?page=" + (page.getTotalPages() - 1) + "&size=" + page.getSize();

            if (page.hasNext()) {
                this.next = baseUrl + "?page=" + (page.getNumber() + 1) + "&size=" + page.getSize();
            }
            if (page.hasPrevious()) {
                this.prev = baseUrl + "?page=" + (page.getNumber() - 1) + "&size=" + page.getSize();
            }
        }
    }
}