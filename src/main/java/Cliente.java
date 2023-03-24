import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

import javax.swing.JOptionPane;


public class Cliente {

	public static String ID = null;
	
	public static void main(String[] args) {
		
		Paquete p = new Paquete();
		p.setIdentificador(Cliente.ID);
		
		p.setIpOrigen("192.168.100.20");
		
		// Esto cargar la id generada por el servidor si es que la tiene asignada
		CrearArchivo c = new CrearArchivo();
		c.cargarId();
		
		// Si no ha cargado la id seria siendo null y entonces arrancamos el hilo para obtener el id
		
		if(Cliente.ID == null) {
			
			Thread t1 = new Thread(new RespuestaId());
			t1.start();
			comprobarId(p);
		}
		
		// Aqui encendemos el servicio de recibir respuestas
		
		Thread recibeRespuesta = new Thread(new RecibeRespuesta());
		recibeRespuesta.start();
		
		
		// Dos ventanas que recogen el mensaje y la ip de destino

		String mensaje = JOptionPane.showInputDialog("Escribe un mensaje:");
		String ipDestino = JOptionPane.showInputDialog("Dirección de destino:");
		
		mandarPaquete(p, ipDestino, mensaje);
		
	}
	
	public static void comprobarId(Paquete p) {
		
		try(Socket s = new Socket("localhost", 8090)){
		
			// Mando el paquete vacio solo con la ip para que le asigne una id si lo na tiene
			
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
	
	public static void mandarPaquete(Paquete p, String ipDestino, String mensaje) {
		
/*		// Antes de mandar el paquete comprueba la id si se quiere hacer en cada mensaje en vez de cuando inicializa el cliente
		
		if(Cliente.id == null) {
			
			comprobarId(p);
			
		}				*/
				
		// Crea el socket que conecta con el localhost por el puerto especificado

		try(Socket s = new Socket("localhost",8080)){
			
			// Si no tiene id todavia asignada que mande el paquete sin id pero si tiene ya asignada q lo mande con la id
			
			if(Cliente.ID != null) {
				
				p.setIdentificador(Cliente.ID);
				
			}else { p.setIdentificador(null);	}
			
			// Completamos el paquete
			// LA IP LA HE CONFIGURADO EN EL MAIN pero con esta opcion seria la ip de cada cliente diferente
			//p.setIpOrigen(s.getInetAddress().toString());
			p.setMensaje(mensaje);
			p.setIpDestino(ipDestino);
			// Creo el stream de salida para un objeto
			
			ObjectOutputStream salida = new ObjectOutputStream(s.getOutputStream());
			
			// Escribo el objeto
			
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
}
class RespuestaId implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		System.out.println("Servicio recibe Id activado");
		
		try(ServerSocket ss = new ServerSocket(8095)){
			
			while(true) {
				
				Socket s = ss.accept();
				
				ObjectInputStream entrada = new ObjectInputStream(s.getInputStream());
				Paquete p = (Paquete) entrada.readObject();
				
				System.out.println("IDENTIFICADOR:         " + p.getIdentificador());
				Cliente.ID = p.getIdentificador();
				CrearArchivo c = new CrearArchivo();
				c.guardarId(p.getIdentificador());
				System.out.println("Finalizando proceso de asignación de ID");
				
				break;
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

class RecibeRespuesta implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		System.out.println("Servicio de recibir mensajes encendido");
		
		try(ServerSocket ss = new ServerSocket(8096)){
			
			while(true) {
				
				Socket s = ss.accept();
				
				ObjectInputStream entrada = new ObjectInputStream(s.getInputStream());
				Paquete p = (Paquete) entrada.readObject();
				System.out.println(p.getMensaje());
				
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
//Esta clase es la que se encarga de la persistencia de la ID almacenandola en un fichero

class CrearArchivo{
	
	String nombreFichero = "Id.txt";
	File f;
	public CrearArchivo() {		
		
		}
	
	public void cargarId() {
		
		try {		
			
			FileReader leer = new FileReader(nombreFichero);
			BufferedReader lb = new BufferedReader(leer);
			
			String id = lb.readLine();
			System.out.println("Id del usuario es:               " + id);
			Cliente.ID = id;
			
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Archivo con ID todavia no creado  // Error al cargarlo");
		}
	}
	
	public void guardarId(String id) {
		
		try {
			
			f = new File(nombreFichero);
			if(f.exists()) {
				FileWriter escribir = new FileWriter(f);
				BufferedWriter writeBuffer = new BufferedWriter(escribir);
				writeBuffer.write(id);
				writeBuffer.close();
				
			}else {
				f.createNewFile();
				FileWriter escribir = new FileWriter(f);
				BufferedWriter writeBuffer = new BufferedWriter(escribir);
				writeBuffer.write(id);
				writeBuffer.close();
			}
		}catch(Exception e) {
			e.printStackTrace();
			System.out.println("Error al guardar la ID");
		
	}
	}
}