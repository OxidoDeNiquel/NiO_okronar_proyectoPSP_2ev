
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.*;

public class Cliente1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MarcoCliente mimarco = new MarcoCliente();
		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

class MarcoCliente extends JFrame {

	public MarcoCliente() {
		setBounds(600, 300, 280, 350);
		
		LaminaMarcoCliente milamina = new LaminaMarcoCliente();
		add(milamina);
		setVisible(true);
	}
}

class LaminaMarcoCliente extends JPanel implements Runnable {
	
	private JTextField campo1,nick,ip;
	private JTextArea campochat;
	private JButton miboton;

	public LaminaMarcoCliente() {
		nick = new JTextField(5);
		add(nick); 
		
		JLabel texto = new JLabel("-CHAT-");
		add(texto);
		ip = new JTextField(8);
		add (ip);
		campochat = new JTextArea(12,20);
		add(campochat);
		campo1 = new JTextField(20);
		add(campo1);
		
		miboton = new JButton("Enviar");
		
		EnviarTexto mievento = new EnviarTexto();
		miboton.addActionListener(mievento);
		
		add(miboton);
		
		Thread miHilo = new Thread(this);
		miHilo.start();
	}
	
	private class EnviarTexto implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) { 
			
			campochat.append("\n" + campo1.getText());
			try {
				//Puente para comunicar con el servidor
				Socket misocket = new Socket("10.5.4.23", 1234);
				//aqui tenemos la informacion completa del mensaje y el que lo manda en un objeto
				PaqueteEnvio datos = new PaqueteEnvio();
				
				datos.setNick(nick.getText());
				datos.setIp(ip.getText());
				datos.setMensaje(campo1.getText());
				
				//stream del objeto
				ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());
				paquete_datos.writeObject(datos);
				
				misocket.close();
				
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				System.out.println(e1.getMessage());
			}
			campo1.setText(null);
		}
	}
	
	//Run LaminaMarcoCliente
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			//Recepcion del servidor por el puerto 9090
			ServerSocket servidorCliente = new ServerSocket(9090);
			Socket cliente;
			PaqueteEnvio paqueteRecibido;
			
			while(true) {
				cliente = servidorCliente.accept();
				ObjectInputStream flujoEntrada = new ObjectInputStream(cliente.getInputStream());
				paqueteRecibido = (PaqueteEnvio) flujoEntrada.readObject();
				//Montamos el objeto recibido
				campochat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
				
			}
			
		}catch(Exception e) {
			
		}
	}

}

//lo serializamos para que todas las instancias sean "desmenuzables"
class PaqueteEnvio implements Serializable{
	
	private String nick,ip,mensaje;

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
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
	
	
}