package Modelo;

import Conexion.ConexionDB;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RetiroDAO {

    /**
     * Realiza un retiro y devuelve el nuevo saldo de la cuenta.
     * El descuento del saldo y el registro del movimiento se hacen
     * en una sola transacción: o se guardan los dos o no se guarda nada.
     */
    public BigDecimal realizarRetiro(int numeroCuenta, BigDecimal monto)
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
                    "El monto del retiro debe ser mayor que cero."
            );
        }

        // Validar que no tenga más de 2 decimales
        if (monto.stripTrailingZeros().scale() > 2) {
            throw new IllegalArgumentException(
                    "El monto no puede tener más de 2 decimales."
            );
        }

        String sqlBuscar =
                "SELECT saldo, estado FROM cuenta " +
                "WHERE numero_cuenta = ? FOR UPDATE";

        String sqlActualizar =
                "UPDATE cuenta SET saldo = saldo - ? " +
                "WHERE numero_cuenta = ?";

        String sqlMovimiento =
                "INSERT INTO movimiento " +
                "(numero_cuenta, numero_cuenta_destino, " +
                "tipo_movimiento, monto, descripcion) " +
                "VALUES (?, NULL, 'RETIRO', ?, ?)";

        BigDecimal nuevoSaldo = null;

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
                        BigDecimal saldoActual = rs.getBigDecimal("saldo");

                        if (estado == null ||
                                !"Activa".equalsIgnoreCase(estado.trim())) {
                            throw new SQLException(
                                    "No se pueden realizar retiros " +
                                    "en cuentas que no estén activas."
                            );
                        }

                        if (saldoActual.compareTo(monto) < 0) {
                            throw new SQLException(
                                    "Saldo insuficiente. Saldo disponible: $"
                                    + saldoActual.toPlainString()
                            );
                        }

                        nuevoSaldo = saldoActual.subtract(monto);
                    }
                }

                // 2. Descontar el saldo
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

                // 3. Registrar el retiro en la tabla movimiento
                try (PreparedStatement ps =
                        conn.prepareStatement(sqlMovimiento)) {

                    ps.setInt(1, numeroCuenta);
                    ps.setBigDecimal(2, monto);
                    ps.setString(3, "Retiro bancario");

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

        return nuevoSaldo;
    }

    /**
     * Reporte de cuentas bloqueadas.
     * Cada fila: número de cuenta, código cliente, tipo de cuenta, saldo, estado.
     */
    public List<Object[]> obtenerCuentasBloqueadas() throws SQLException {

        String sql =
                "SELECT numero_cuenta, codigo_cliente, tipo_cuenta, " +
                "saldo, estado " +
                "FROM cuenta " +
                "WHERE estado = 'Bloqueada' " +
                "ORDER BY numero_cuenta";

        List<Object[]> filas = new ArrayList<>();

        try (Connection conn = ConexionDB.conectar()) {

            if (conn == null) {
                throw new SQLException(
                        "No se pudo conectar con la base de datos."
                );
            }

            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    filas.add(new Object[]{
                        rs.getInt("numero_cuenta"),
                        rs.getInt("codigo_cliente"),
                        rs.getString("tipo_cuenta"),
                        rs.getBigDecimal("saldo"),
                        rs.getString("estado")
                    });
                }
            }
        }

        return filas;
    }
}
