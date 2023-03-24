import java.io.BufferedWriter;


import BBDD.Conexion;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import BBDD.Clientes;
import BBDD.Mensajes;


public class Servidor {
	
	// Lista de clientes con id asociadas creada por el servidor
	
	static List<Clientes> listaClientes = new ArrayList();
	
	// Lista de paquetes
	
	static List<Paquete> listaPaquetes = new ArrayList();
	
	public static void main(String[] args) {
		
		// Si queremos crear la base de datos y las tablas SQL, con inicializar el objeto Conexion se hace automaticamente
		// Conexion con = new Conexion();
		
		// Cargar clientes
		
		CargaCliente c = new CargaCliente();
		listaClientes = c.cargarUsandoHibernate();
		
		listaClientes.forEach(n -> System.out.println(n.toString()));
		
		// Hilo que se encargar de asignar la ID
		//Thread asignaId = new Thread(new AsignaId());
		Thread estableceId = new Thread(new EstableceId());
		
		// Hilo que se encarga de recibir los mensajes 
		Thread recibeMensaje = new Thread(new RecibePaquetes());
		
		// Inicio los hilos
		//asignaId.start();
		estableceId.start();
		recibeMensaje.start();
		
		// Creo un pequeño menu en consola con dos opciones una leer todos los mensajes almacenados en el servidor y otra para apagar el servidor
		
		Scanner teclado = new Scanner(System.in);
		
		System.out.println("<----- CONSOLA SERVIDOR -----> \nElija una opcion: \n1) Mostrar mensajes almacenados \n2) Salir");
		
		while(true) {
			
			int opcion = teclado.nextInt();
			
			switch(opcion) {
			
			case 1:
				listaPaquetes.forEach(n -> System.out.println(n.getIpOrigen() + " - " + n.getIdentificador() + ":   " + n.getMensaje().toString()));
				break;
				
			case 2:
				
				System.exit(0);
				
			default: 
				
				System.out.println("Opción incorrecta. \nElija una opcion: \n1) Mostrar mensajes almacenados \n2) Salir");
					
			}
		}
	}

}

// Esta clase es la que se encarga de recibir los paquetes

class RecibePaquetes implements Runnable{

	CrearArchivo c = new CrearArchivo();
	
	String mensaje = "";
	String ip = "";
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("Hilo recibe mensajes encendido");
		EnviaRespuesta r = new EnviaRespuesta();
		
		try(ServerSocket ss = new ServerSocket(8080)){
			
			while(true) {
				
				Socket s = ss.accept();
				
				ObjectInputStream entrada = new ObjectInputStream(s.getInputStream());
				Paquete p = (Paquete) entrada.readObject();			
				
				Servidor.listaPaquetes.add(p);
				
				Insertar i = new Insertar();
				i.insertarMensajes(p.getMensaje(), p.getIdentificador(), p.getIpOrigen(), p.getIpDestino());
				
				r.enviarMensajeDestinatario(p);

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

// Esta clase se encargar de recibir la ip y asignarle una id si no la tiene

class EstableceId implements Runnable{
	
	boolean clienteRegistrado = false;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	
		
	System.out.println("Hilo asigna id encendido");
		
		try(ServerSocket ss = new ServerSocket(8090)){
			
			while(true) {
				
				Socket s = ss.accept();
				
				ObjectInputStream entrada = new ObjectInputStream(s.getInputStream());
				Paquete p = (Paquete) entrada.readObject();
				
				synchronized(Servidor.listaClientes) {
					
					if(!Servidor.listaClientes.isEmpty()) {
						
						for(Clientes e : Servidor.listaClientes) {
							
							if(e.getIp().equals(p.getIpOrigen())) {
								
								System.out.println("Ip repetida");
								clienteRegistrado = true;
								
								EnviaRespuesta envia = new EnviaRespuesta();
								envia.enviarRespuesta(e.getId(), e.getIp());
							}
						}
						
						if(clienteRegistrado == false) {
							
							Clientes c = new Clientes();
							c.setIp(p.getIpOrigen());
							
							Insertar i = new Insertar();
							i.insertarCliente(c);
							
							CargaCliente carga = new CargaCliente();
							Servidor.listaClientes = carga.cargarUsandoHibernate();
							
							for(Clientes e : Servidor.listaClientes) {
								
								if(e.getIp().equals(p.getIpOrigen())) {
									
									EnviaRespuesta envia = new EnviaRespuesta();
									envia.enviarRespuesta(e.getId(), e.getIp());
								}
							}
						}
					}else {
						
						Clientes c = new Clientes();
						c.setIp(p.getIpOrigen());
						
						Insertar i = new Insertar();
						i.insertarCliente(c);
						
						CargaCliente carga = new CargaCliente();
						Servidor.listaClientes = carga.cargarUsandoHibernate();
						
						for(Clientes e : Servidor.listaClientes) {
							
							if(e.getIp().equals(p.getIpOrigen())) {
								
								EnviaRespuesta envia = new EnviaRespuesta();
								envia.enviarRespuesta(e.getId(), e.getIp());
							}
						}
					}
				}
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}	
}


class Insertar{
	
	
	public Insertar() {
		
	}
	
	public void insertarCliente(Clientes c) {
		
		SessionFactory sf = new Configuration()
							.configure("hibernate.cfg.xml")
							.addAnnotatedClass(Clientes.class)
							.buildSessionFactory();
		
		Session s = sf.openSession();
		
		
		try {
			s.beginTransaction();
			
			s.save(c);
			
			s.getTransaction().commit();
			
		}catch(Exception e) {
			
			e.printStackTrace();
			System.out.println("Error");
		}
		s.close();
		sf.close();
		
	}
public void insertarMensajes(String mensaje, String ip, String id, String destino) {
		
		SessionFactory sf = new Configuration()
							.configure()
							.addAnnotatedClass(Mensajes.class)
							.buildSessionFactory();
		
		Session s = sf.openSession();
		
		
		try {			
			
			s.beginTransaction();
			
			s.save(new Mensajes(id, ip, mensaje, destino));
			
			s.getTransaction().commit();
			
		}catch(Exception e) {
			
			e.printStackTrace();
			System.out.println("Error");
		}
		s.close();
		sf.close();
		
	}
}

//Esta funcion cargar los clientes al encender el servidor

class CargaCliente{
	
	
	
	public CargaCliente() {
		
		
	}
	
	public Clientes cargarCliente(String ipOrigen) {
		
		SessionFactory sf = new Configuration()
							.configure()
							.addAnnotatedClass(Clientes.class)
							.addAnnotatedClass(Mensajes.class)
							.buildSessionFactory();
		
		Session s = null;
		Clientes c = null;
		
		try {
			
			s = sf.openSession();
			
			c = s.get(Clientes.class, ipOrigen);
		
		
		}catch(Exception e) {
			
			e.printStackTrace();
			System.out.println("Error al recuperar el cliente");
		}
		
		s.close();
		sf.close();
		
		return c;
	}
	
	public List<Clientes> cargarUsandoHibernate(){
		
		List<Clientes> listaC = new ArrayList();
		
		SessionFactory sf = new Configuration()
							.configure("hibernate.cfg.xml")
							.addAnnotatedClass(Clientes.class)
							.addAnnotatedClass(Mensajes.class)
							.buildSessionFactory();
		
		Session s = null;
		
		try {
			
			s = sf.openSession();
			
			listaC = s.createQuery("from Clientes", Clientes.class).getResultList();
			
		}catch(Exception e) {
			
			e.printStackTrace();
			System.out.println("Error al cargar los clientes");
		}
		
		s.close();
		sf.close();
		
		return listaC;
		
	}
	
	public List<Clientes> cargarUsandoJDBC() {
		
	String BBDD = "jdbc:mysql://localhost:3306/servidormensajes";
	String sentencia = "SELECT * FROM CLIENTES";
	List<Clientes> listaClientes = new ArrayList();
		
		try {
			
			// Creo la conexión 
			
			Connection miConexion = DriverManager.getConnection(BBDD, "root", "");
		
			// Creo el statement con update porque no devuelve un resultset

			Statement miStatement = miConexion.createStatement();
			ResultSet rs = miStatement.executeQuery(sentencia);
			
			while(rs.next()) {
				
				Clientes c = new Clientes(rs.getString(2), rs.getString(1));
				listaClientes.add(c);
			}
			
			System.out.println("Usuarios cargados correctamente");
			
			
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error");
		}
		return listaClientes;
	}
		
		
	
}
class EnviaRespuesta{
	
	
	
	public EnviaRespuesta() {
		
		
	}
	
	public void enviarMensajeDestinatario(Paquete p) {
		
		try(Socket s = new Socket("localhost", 8096)){    // p.getIpDestino()
			
			ObjectOutputStream salida = new ObjectOutputStream(s.getOutputStream());
			salida.writeObject(p);
			
			s.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void enviarRespuesta(String identificador, String ip) {
		
	// Aqui debería de poner ip del campo que recibe el metodo pero como es locahost lo dejo asi
		
	try(Socket s = new Socket("localhost", 8095)){
			
			ObjectOutputStream salida = new ObjectOutputStream(s.getOutputStream());
			Paquete p2 = new Paquete();
			
			
			p2.setIdentificador(identificador);
			salida.writeObject(p2);
			s.close();
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}