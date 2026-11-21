package Modelo;

import Conexion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MovimientoDAO {

    // Obtiene todo el historial de movimientos
    public List<Object[]> obtenerMovimientos() {

        List<Object[]> movimientos = new ArrayList<>();

        String sql = """
            SELECT 
                id_movimiento,
                numero_cuenta,
                numero_cuenta_destino,
                tipo_movimiento,
                monto,
                fecha_movimiento,
                descripcion
            FROM movimiento
            ORDER BY fecha_movimiento DESC
        """;

        try (
            Connection conn = ConexionDB.conectar();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {

            while (rs.next()) {

                movimientos.add(new Object[]{
                    rs.getInt("id_movimiento"),
                    rs.getInt("numero_cuenta"),
                    rs.getObject("numero_cuenta_destino"),
                    rs.getString("tipo_movimiento"),
                    rs.getBigDecimal("monto"),
                    rs.getTimestamp("fecha_movimiento"),
                    rs.getString("descripcion")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movimientos;
    }

    // Obtiene movimientos de una cuenta específica
    public List<Object[]> obtenerMovimientosPorCuenta(int numeroCuenta) {

        List<Object[]> movimientos = new ArrayList<>();

        String sql = """
            SELECT 
                id_movimiento,
                numero_cuenta,
                numero_cuenta_destino,
                tipo_movimiento,
                monto,
                fecha_movimiento,
                descripcion
            FROM movimiento
            WHERE numero_cuenta = ?
            ORDER BY fecha_movimiento DESC
        """;

        try (
            Connection conn = ConexionDB.conectar();
            PreparedStatement ps = conn.prepareStatement(sql);
        ) {

            ps.setInt(1, numeroCuenta);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    movimientos.add(new Object[]{
                        rs.getInt("id_movimiento"),
                        rs.getInt("numero_cuenta"),
                        rs.getObject("numero_cuenta_destino"),
                        rs.getString("tipo_movimiento"),
                        rs.getBigDecimal("monto"),
                        rs.getTimestamp("fecha_movimiento"),
                        rs.getString("descripcion")
                    });
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movimientos;
    }
}