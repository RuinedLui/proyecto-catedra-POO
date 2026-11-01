/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

import java.math.BigDecimal;
import Conexion.ConexionDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Lenovo
 */
public class Cuenta {
    
    // 1. Atributos
    private int numeroCuenta;
    private int codigoCliente;
    private String tipoCuenta;
    private BigDecimal saldo;
    private String estado;

    // 2. Constructores del Modelo
    public Cuenta() {
        this.saldo = BigDecimal.ZERO;
    }

    public Cuenta(int numeroCuenta, int codigoCliente, String tipoCuenta, BigDecimal saldo, String estado) {
        this.numeroCuenta = numeroCuenta;
        this.codigoCliente = codigoCliente;
        this.tipoCuenta = tipoCuenta;
        this.saldo = saldo;
        this.estado = estado;
    }

    // 3. Getters y Setters
    public int getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(int numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public int getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(int codigoCliente) { this.codigoCliente = codigoCliente; }

    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    @Override
    public String toString() {
        return "Cuenta #" + numeroCuenta + " (" + tipoCuenta + ") - Saldo: $" + saldo;
    }

    // =====================================================
    // GESTIÓN DE CUENTAS EN LA BD
    // =====================================================
    public static class CuentaDAO {
        
        private Connection obtenerConexion() throws SQLException {
            Connection conn = ConexionDB.conectar();
            if (conn == null) {
                throw new SQLException("No se pudo establecer la conexión con la base de datos.");
            }
            return conn;
        }

        // Método para Crear/Insertar Cuenta
        public boolean insertar(Cuenta cuenta) {
            String sql = "INSERT INTO cuenta (codigo_cliente, tipo_cuenta, saldo, estado) VALUES (?, ?, ?, ?)";
            
            try (Connection conn = obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, cuenta.getCodigoCliente());
                pstmt.setString(2, cuenta.getTipoCuenta());
                pstmt.setBigDecimal(3, cuenta.getSaldo());
                pstmt.setString(4, cuenta.getEstado() == null ? "Activa" : cuenta.getEstado());
                
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error al insertar cuenta: " + e.getMessage());
                return false;
            }
        }

        // Método para Listar las Cuentas de un Cliente específico
        public List<Cuenta> obtenerCuentasPorCliente(int codigoCliente) {
            List<Cuenta> lista = new ArrayList<>();
            String sql = "SELECT * FROM cuenta WHERE codigo_cliente = ?";
            
            try (Connection conn = obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, codigoCliente);
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        Cuenta c = new Cuenta(
                            rs.getInt("numero_cuenta"),
                            rs.getInt("codigo_cliente"),
                            rs.getString("tipo_cuenta"),
                            rs.getBigDecimal("saldo"),
                            rs.getString("estado")
                        );
                        lista.add(c);
                    }
                }
            } catch (SQLException e) {
                System.err.println("Error al consultar cuentas del cliente: " + e.getMessage());
            }
            
            return lista;
        }

        // Método para Actualizar el Saldo (Útil para transacciones futuras)
        public boolean actualizarSaldo(int numeroCuenta, BigDecimal nuevoSaldo) {
            String sql = "UPDATE cuenta SET saldo = ? WHERE numero_cuenta = ?";
            
            try (Connection conn = obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setBigDecimal(1, nuevoSaldo);
                pstmt.setInt(2, numeroCuenta);
                
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error al actualizar saldo: " + e.getMessage());
                return false;
            }
        }

        public boolean actualizar(Cuenta cuenta) {
            String sql = "UPDATE cuenta SET codigo_cliente = ?, tipo_cuenta = ?, saldo = ?, estado = ? WHERE numero_cuenta = ?";

            try (Connection conn = obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, cuenta.getCodigoCliente());
                pstmt.setString(2, cuenta.getTipoCuenta());
                pstmt.setBigDecimal(3, cuenta.getSaldo());
                pstmt.setString(4, cuenta.getEstado());
                pstmt.setInt(5, cuenta.getNumeroCuenta());
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error al actualizar cuenta: " + e.getMessage());
                return false;
            }
        }

        // Método para Eliminar Cuenta
        public boolean eliminar(int numeroCuenta) {
            String sql = "DELETE FROM cuenta WHERE numero_cuenta = ?";
            
            try (Connection conn = obtenerConexion();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, numeroCuenta);
                
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error al eliminar cuenta: " + e.getMessage());
                return false;
            }
        }
    }    
}
