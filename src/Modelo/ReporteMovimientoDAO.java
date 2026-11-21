package Modelo;

import Conexion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReporteMovimientoDAO {

    // Historial completo
    public List<Object[]> obtenerHistorialCompleto() {

        List<Object[]> lista = new ArrayList<>();

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

                lista.add(new Object[]{
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

        return lista;
    }

    // Movimientos por cuenta
    public List<Object[]> obtenerMovimientosPorCuenta(
            int numeroCuenta) {

        List<Object[]> lista = new ArrayList<>();

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

                    lista.add(new Object[]{
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

        return lista;
    }

    // Clientes registrados
    public List<Object[]> obtenerClientesRegistrados() {

        List<Object[]> lista = new ArrayList<>();

        String sql = """
            SELECT
                codigo_cliente,
                dui,
                nombre_completo,
                telefono,
                correo_electronico,
                estado
            FROM cliente
            ORDER BY nombre_completo
        """;

        try (
            Connection conn = ConexionDB.conectar();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {

            while (rs.next()) {

                lista.add(new Object[]{
                    rs.getInt("codigo_cliente"),
                    rs.getString("dui"),
                    rs.getString("nombre_completo"),
                    rs.getString("telefono"),
                    rs.getString("correo_electronico"),
                    rs.getString("estado")
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }
}