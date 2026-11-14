
package Modelo;

import Conexion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ReporteCuentasActivasDAO {

    public List<Cuenta> obtenerCuentasActivas() throws SQLException {

        List<Cuenta> cuentas = new ArrayList<>();

        String sql = """
            SELECT numero_cuenta, codigo_cliente,
                   tipo_cuenta, saldo, estado
            FROM cuenta
            WHERE estado = 'Activa'
            ORDER BY numero_cuenta
            """;

        Connection conexion = ConexionDB.conectar();

        if (conexion == null) {
            throw new SQLException(
                    "No se pudo conectar con la base de datos."
            );
        }

        try (Connection conn = conexion;
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Cuenta cuenta = new Cuenta(
                    rs.getInt("numero_cuenta"),
                    rs.getInt("codigo_cliente"),
                    rs.getString("tipo_cuenta"),
                    rs.getBigDecimal("saldo"),
                    rs.getString("estado")
                );

                cuentas.add(cuenta);
            }
        }

        return cuentas;
    }
}
