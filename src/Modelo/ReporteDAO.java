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
public class ReporteDAO {
    
    public static class ResumenMontos {
        private final BigDecimal totalDepositado;
        private final BigDecimal totalRetirado;
        private final BigDecimal totalTransferido;

        public ResumenMontos(BigDecimal totalDepositado, BigDecimal totalRetirado, BigDecimal totalTransferido) {
            this.totalDepositado = totalDepositado;
            this.totalRetirado = totalRetirado;
            this.totalTransferido = totalTransferido;
        }

        public BigDecimal getTotalDepositado() {
            return totalDepositado;
        }

        public BigDecimal getTotalRetirado() {
            return totalRetirado;
        }

        public BigDecimal getTotalTransferido() {
            return totalTransferido;
        }
    }

    public ResumenMontos obtenerResumenMontos(Connection conn) throws SQLException {
        String sql = "SELECT "
                + "COALESCE(SUM(CASE WHEN tipo_movimiento = 'DEPOSITO' THEN monto ELSE 0 END), 0.00) AS total_depositado, "
                + "COALESCE(SUM(CASE WHEN tipo_movimiento = 'RETIRO' THEN monto ELSE 0 END), 0.00) AS total_retirado, "
                + "COALESCE(SUM(CASE WHEN tipo_movimiento = 'TRANSFERENCIA' THEN monto ELSE 0 END), 0.00) AS total_transferido "
                + "FROM movimiento";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                BigDecimal depositado = rs.getBigDecimal("total_depositado");
                BigDecimal retirado = rs.getBigDecimal("total_retirado");
                BigDecimal transferido = rs.getBigDecimal("total_transferido");

                return new ResumenMontos(
                        depositado != null ? depositado : BigDecimal.ZERO,
                        retirado != null ? retirado : BigDecimal.ZERO,
                        transferido != null ? transferido : BigDecimal.ZERO
                );
            }
        }

        return new ResumenMontos(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
    
}
