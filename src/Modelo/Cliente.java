/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Modelo;

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
public class Cliente {

    public Cliente(int aInt, String string, String string1, String string2, String string3, String string4, String string5) {
    }
    
    private int codigoCliente;
    private String dui;
    private String nombreCompleto;
    private String telefono;
    private String correoElectronico;
    private String direccion;
    private String estado;

    // Constructor vacío
    public void Cliente() {
    }

    // Constructor con todos los atributos
    public void Cliente(int codigoCliente, String dui, String nombreCompleto, String telefono, String correoElectronico, String direccion, String estado) {
        this.codigoCliente = codigoCliente;
        this.dui = dui;
        this.nombreCompleto = nombreCompleto;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
        this.direccion = direccion;
        this.estado = estado;
    }

    // Getters y Setters
    public int getCodigoCliente() { return codigoCliente; }
    public void setCodigoCliente(int codigoCliente) { this.codigoCliente = codigoCliente; }

    public String getDui() { return dui; }
    public void setDui(String dui) { this.dui = dui; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCorreoElectronico() { return correoElectronico; }
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    
    
    @Override
    public String toString() {
        return nombreCompleto + " (" + dui + ")";
    }
    // =====================================================
    // MÉTODOS DE BASE DE DATOS (CRUD)
    // =====================================================

    public static class ClienteDAO{
        
        private Connection conn;

        // Constructor DAO
        public ClienteDAO() {
            conn = ConexionDB.conectar();
        }

        // Método para Insertar Cliente
        public boolean insertar(Cliente cliente) {
            String sql = "INSERT INTO cliente (dui, nombre_completo, telefono, correo_electronico, direccion, estado) VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, cliente.getDui());
                pstmt.setString(2, cliente.getNombreCompleto());
                pstmt.setString(3, cliente.getTelefono());
                pstmt.setString(4, cliente.getCorreoElectronico());
                pstmt.setString(5, cliente.getDireccion());
                pstmt.setString(6, cliente.getEstado());
                
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error al insertar cliente: " + e.getMessage());
                return false;
            }
        }

        // Método para Listar Clientes
        public List<Cliente> obtenerClientes() {
            List<Cliente> lista = new ArrayList<>();
            String sql = "SELECT * FROM cliente";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {
                
                while (rs.next()) {
                    Cliente c = new Cliente(
                        rs.getInt("codigo_cliente"),
                        rs.getString("dui"),
                        rs.getString("nombre_completo"),
                        rs.getString("telefono"),
                        rs.getString("correo_electronico"),
                        rs.getString("direccion"),
                        rs.getString("estado")
                    );
                    lista.add(c);
                }
            } catch (SQLException e) {
                System.err.println("Error al consultar clientes: " + e.getMessage());
            }
            
            return lista;
        }

        // Método para Actualizar Estado
        public boolean actualizarEstado(int codigoCliente, String nuevoEstado) {
            String sql = "UPDATE cliente SET estado = ? WHERE codigo_cliente = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, nuevoEstado);
                pstmt.setInt(2, codigoCliente);
                
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error al actualizar estado: " + e.getMessage());
                return false;
            }
        }

        // Método para Eliminar Cliente
        public boolean eliminar(int codigoCliente) {
            String sql = "DELETE FROM cliente WHERE codigo_cliente = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, codigoCliente);
                
                return pstmt.executeUpdate() > 0;
            } catch (SQLException e) {
                System.err.println("Error al eliminar cliente: " + e.getMessage());
                return false;
            }
        }  
    }
}
