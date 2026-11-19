/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Lenovo
 */
public class TransferenciaDAO {
    
    public boolean realizarTransferencia(Connection conn, int cuentaOrigen, int cuentaDestino, BigDecimal monto)
            throws Exception {

        if (conn == null) {
            throw new IllegalStateException("No hay conexión activa con la base de datos.");
        }
        if (cuentaOrigen <= 0 || cuentaDestino <= 0) {
            throw new IllegalArgumentException("Los números de cuenta deben ser válidos.");
        }
        if (cuentaOrigen == cuentaDestino) {
            throw new IllegalArgumentException("La cuenta origen y la cuenta destino no pueden ser la misma.");
        }
        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a transferir debe ser mayor que cero.");
        }

        String sqlConsultaCuenta = "SELECT saldo, estado FROM cuenta WHERE numero_cuenta = ? FOR UPDATE";
        String sqlDebitar = "UPDATE cuenta SET saldo = saldo - ? WHERE numero_cuenta = ?";
        String sqlAcreditar = "UPDATE cuenta SET saldo = saldo + ? WHERE numero_cuenta = ?";
        String sqlInsertarMovimiento = "INSERT INTO movimiento "
                + "(numero_cuenta, numero_cuenta_destino, tipo_movimiento, monto, descripcion) "
                + "VALUES (?, ?, 'TRANSFERENCIA', ?, ?)";

        boolean autoCommitOriginal = conn.getAutoCommit();
        conn.setAutoCommit(false);

        try {
            // 1. Validar cuenta origen (existencia, estado 'Activa' y saldo suficiente)
            BigDecimal saldoOrigen;
            try (PreparedStatement psOrigen = conn.prepareStatement(sqlConsultaCuenta)) {
                psOrigen.setInt(1, cuentaOrigen);
                try (ResultSet rsOrigen = psOrigen.executeQuery()) {
                    if (!rsOrigen.next()) {
                        throw new IllegalStateException("La cuenta origen #" + cuentaOrigen + " no existe.");
                    }
                    String estadoOrigen = rsOrigen.getString("estado");
                    if (!"Activa".equalsIgnoreCase(estadoOrigen)) {
                        throw new IllegalStateException("La cuenta origen #" + cuentaOrigen + " no está activa (Estado: " + estadoOrigen + ").");
                    }
                    saldoOrigen = rsOrigen.getBigDecimal("saldo");
                }
            }

            if (saldoOrigen.compareTo(monto) < 0) {
                throw new IllegalStateException("Saldo insuficiente en la cuenta origen. Saldo disponible: $" + saldoOrigen);
            }

            // 2. Validar cuenta destino (existencia y estado 'Activa')
            try (PreparedStatement psDestino = conn.prepareStatement(sqlConsultaCuenta)) {
                psDestino.setInt(1, cuentaDestino);
                try (ResultSet rsDestino = psDestino.executeQuery()) {
                    if (!rsDestino.next()) {
                        throw new IllegalStateException("La cuenta destino #" + cuentaDestino + " no existe.");
                    }
                    String estadoDestino = rsDestino.getString("estado");
                    if (!"Activa".equalsIgnoreCase(estadoDestino)) {
                        throw new IllegalStateException("La cuenta destino #" + cuentaDestino + " no está activa (Estado: " + estadoDestino + ").");
                    }
                }
            }

            // 3. Descontar saldo de cuenta origen
            try (PreparedStatement psDebitar = conn.prepareStatement(sqlDebitar)) {
                psDebitar.setBigDecimal(1, monto);
                psDebitar.setInt(2, cuentaOrigen);
                if (psDebitar.executeUpdate() != 1) {
                    throw new SQLException("No se pudo actualizar el saldo de la cuenta origen.");
                }
            }

            // 4. Acreditar saldo en cuenta destino
            try (PreparedStatement psAcreditar = conn.prepareStatement(sqlAcreditar)) {
                psAcreditar.setBigDecimal(1, monto);
                psAcreditar.setInt(2, cuentaDestino);
                if (psAcreditar.executeUpdate() != 1) {
                    throw new SQLException("No se pudo actualizar el saldo de la cuenta destino.");
                }
            }

            // 5. Registrar el movimiento en la tabla 'movimiento'
            try (PreparedStatement psMov = conn.prepareStatement(sqlInsertarMovimiento)) {
                psMov.setInt(1, cuentaOrigen);
                psMov.setInt(2, cuentaDestino);
                psMov.setBigDecimal(3, monto);
                psMov.setString(4, "Transferencia de cuenta #" + cuentaOrigen + " a cuenta #" + cuentaDestino);
                if (psMov.executeUpdate() != 1) {
                    throw new SQLException("No se pudo registrar el movimiento de transferencia.");
                }
            }

            conn.commit();
            return true;

        } catch (Exception ex) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                ex.addSuppressed(rollbackEx);
            }
            throw ex;
        } finally {
            try {
                conn.setAutoCommit(autoCommitOriginal);
            } catch (SQLException ignored) {
            }
        }
    }
    
}
