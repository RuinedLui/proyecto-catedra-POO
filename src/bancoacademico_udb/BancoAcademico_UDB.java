/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package bancoacademico_udb;

import Conexion.ConexionDB;
import Vista.FrmPrincipal;
import java.sql.Connection;

/**
 *
 * @author Lenovo
 */
public class BancoAcademico_UDB {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Connection conexion = ConexionDB.conectar();

        if (conexion != null) {
            System.out.println("La aplicacion esta conectada a la base de datos del Banco Academico");
        } else {
            System.out.println("No se pudo realizar la conexion a la base de datos");
        }
        
        FrmPrincipal Principal = new FrmPrincipal();
        Principal.setVisible(true);
    }
    
}
