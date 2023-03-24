package BBDD;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Conexion {
	
	public static void main(String[] args) {
		
		JDBC j = new JDBC();
		
		// Crea la BBDD
		
		j.ejecutaSentencias("CREATE DATABASE ServidorMensajes");
		
		// Crea las tablas
		
		j.ejecutaSentenciasConBBDD("ServidorMensajes", "CREATE TABLE clientes(id int primary key AUTO_INCREMENT, ip varchar(20) not null)");
		j.ejecutaSentenciasConBBDD("ServidorMensajes", "create TABLE mensajes (ip varchar(20) not null, id varchar(20) not null, mensaje varchar(256), destino varchar(20))");
		
	}
}
class JDBC{
	
	// Esta función crea la base de datos
	
	public void ejecutaSentencias(String sentencia) {
		
		try {
			
			// Creo la conexión 
			
			Connection miConexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "");
		
			// Creo el statement 
			
			Statement miStatement = miConexion.createStatement();
			miStatement.executeUpdate(sentencia);
			System.out.println("Query realizada correctamente");
			
			
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error");
		}
	}
	
	// Esta funcion crea Querys con nombre de la BBDD que reciba 
	
	
	public void ejecutaSentenciasConBBDD(String nombreBBDD, String sentencia) {
		
		
		String BBDD = "jdbc:mysql://localhost:3306/" + nombreBBDD;
		
		try {
			
			// Creo la conexión 
			
			Connection miConexion = DriverManager.getConnection(BBDD, "root", "");
		
			// Creo el statement con update porque no devuelve un resultset

			Statement miStatement = miConexion.createStatement();
			miStatement.executeUpdate(sentencia);
			System.out.println("Query realizada correctamente");
			
			
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error");
		}
	}
}
