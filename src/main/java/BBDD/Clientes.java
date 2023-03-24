package BBDD;

import jakarta.persistence.*;

@Entity
@Table(name= "clientes")
public class Clientes {

	@Id
	@Column(name= "ip")
	String ip;
	
	@Column(name= "id")
	String id;
	
	public Clientes() {
		
	}

	public Clientes(String ip, String id) {
		this.ip = ip;
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Clientes [ip=" + ip + ", id=" + id + "]";
	}


}
