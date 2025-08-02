package com.deliverytech.delivery_api.config.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component("database")
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1)) {
                return Health.up()
                        .withDetail("database", "H2")
                        .withDetail("status", "Conectado")
                        .withDetail("validationQuery", "SELECT 1")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "H2")
                        .withDetail("error", "Conexão inválida")
                        .build();
            }
        } catch (SQLException e) {
            return Health.down()
                    .withDetail("database", "H2")
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}