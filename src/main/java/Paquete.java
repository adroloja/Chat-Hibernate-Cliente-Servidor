import java.io.Serializable;

public class Paquete implements Serializable {
	
	private String ipDestino;
	private String puerto;
	private String ipOrigen;
	private String identificador;
	private String mensaje;
	private boolean finConexion;
	
	public Paquete() {
		
	}
	public Paquete(String ipDestino, String puerto, String ipOrigen, String identificador, String mensaje,
			boolean finConexion) {
		this.ipDestino = ipDestino;
		this.puerto = puerto;
		this.ipOrigen = ipOrigen;
		this.identificador = identificador;
		this.mensaje = mensaje;
		this.finConexion = finConexion;
	}
	public String getIpDestino() {
		return ipDestino;
	}
	public void setIpDestino(String ipDestino) {
		this.ipDestino = ipDestino;
	}
	public String getPuerto() {
		return puerto;
	}
	public void setPuerto(String puerto) {
		this.puerto = puerto;
	}
	public String getIpOrigen() {
		return ipOrigen;
	}
	public void setIpOrigen(String ipOrigen) {
		this.ipOrigen = ipOrigen;
	}
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	public String getMensaje() {
		return mensaje;
	}
	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}
	public boolean isFinConexion() {
		return finConexion;
	}
	public void setFinConexion(boolean finConexion) {
		this.finConexion = finConexion;
	}
	
	@Override
	public String toString() {
		return "Paquete [ipDestino=" + ipDestino + ", puerto=" + puerto + ", ipOrigen=" + ipOrigen + ", identificador="
				+ identificador + ", mensaje=" + mensaje + ", finConexion=" + finConexion + "]";
	}
	
		

}
