package BBDD;

import jakarta.persistence.*;

@Entity
@Table(name = "mensajes")
public class Mensajes {
	
	@Id
	@Column(name="ip")
	String ip;
	
	@Column(name="id")
	String id;
	
	@Column(name="mensaje")
	String mensaje;
	
	@Column(name="destino")
	String destino;

	public Mensajes() {
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getDestino() {
		return destino;
	}

	public void setDestino(String destino) {
		this.destino = destino;
	}

	public Mensajes(String ip, String id, String mensaje, String destino) {
		this.ip = ip;
		this.id = id;
		this.mensaje = mensaje;
		this.destino = destino;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getMensaje() {
		return mensaje;
	}

	public void setMensaje(String mensaje) {
		this.mensaje = mensaje;
	}

	@Override
	public String toString() {
		return "Mensajes [ip=" + ip + ", mensaje=" + mensaje + "]";
	}
	
}
