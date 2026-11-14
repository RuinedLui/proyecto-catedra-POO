
package Modelo;

import Conexion.ConexionDB;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DepositoDAO {

    public void realizarDeposito(int numeroCuenta, BigDecimal monto)
            throws SQLException {

        // Validar el número de cuenta
        if (numeroCuenta <= 0) {
            throw new IllegalArgumentException(
                    "Ingrese un número de cuenta válido."
            );
        }

        // Validar que el monto sea mayor que cero
        if (monto == null ||
                monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El monto del depósito debe ser mayor que cero."
            );
        }

        String sqlBuscar =
                "SELECT estado FROM cuenta " +
                "WHERE numero_cuenta = ? FOR UPDATE";

        String sqlActualizar =
                "UPDATE cuenta SET saldo = saldo + ? " +
                "WHERE numero_cuenta = ?";

        String sqlMovimiento =
                "INSERT INTO movimiento " +
                "(numero_cuenta, numero_cuenta_destino, " +
                "tipo_movimiento, monto, descripcion) " +
                "VALUES (?, NULL, 'DEPOSITO', ?, ?)";

        try (Connection conn = ConexionDB.conectar()) {

            if (conn == null) {
                throw new SQLException(
                        "No se pudo conectar con la base de datos."
                );
            }

            boolean autoCommitAnterior = conn.getAutoCommit();
            conn.setAutoCommit(false);

            try {

                // 1. Buscar y bloquear la cuenta
                try (PreparedStatement ps =
                        conn.prepareStatement(sqlBuscar)) {

                    ps.setInt(1, numeroCuenta);

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            throw new SQLException(
                                    "La cuenta no existe."
                            );
                        }

                        String estado = rs.getString("estado");

                        if (!"Activa".equalsIgnoreCase(estado.trim())) {
                            throw new SQLException(
                                    "No se pueden realizar depósitos " +
                                    "en cuentas que no estén activas."
                            );
                        }
                    }
                }

                // 2. Actualizar el saldo
                try (PreparedStatement ps =
                        conn.prepareStatement(sqlActualizar)) {

                    ps.setBigDecimal(1, monto);
                    ps.setInt(2, numeroCuenta);

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo actualizar el saldo."
                        );
                    }
                }

                // 3. Registrar el depósito
                try (PreparedStatement ps =
                        conn.prepareStatement(sqlMovimiento)) {

                    ps.setInt(1, numeroCuenta);
                    ps.setBigDecimal(2, monto);
                    ps.setString(3, "Depósito bancario");

                    if (ps.executeUpdate() != 1) {
                        throw new SQLException(
                                "No se pudo registrar el movimiento."
                        );
                    }
                }

                // 4. Confirmar ambas operaciones
                conn.commit();

            } catch (SQLException | RuntimeException e) {

                // Si algo falla, deshacer los cambios
                conn.rollback();
                throw e;

            } finally {
                conn.setAutoCommit(autoCommitAnterior);
            }
        }
    }
}
