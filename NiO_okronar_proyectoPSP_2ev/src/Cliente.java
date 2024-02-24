
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;

public class Cliente {

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
		
		addWindowListener(new EnvioOnline());
	}
}
//------ ENVÍO DE SEÑAL ONLINE ------//
class EnvioOnline extends WindowAdapter{
	public void windowOpened(WindowEvent e) {
		try {
			Socket socket = new Socket("10.5.4.23", 9999);
			
			PaqueteEnvio datos = new PaqueteEnvio();
			datos.setMensaje("online");
			
			ObjectOutputStream paqueteDatos = new ObjectOutputStream(socket.getOutputStream());
			
			paqueteDatos.writeObject(datos);
			
			socket.close();
			
		}catch(Exception ex) {
			System.out.println("ERROR: " + ex.getMessage());
		}
	}
}

class LaminaMarcoCliente extends JPanel implements Runnable {
	
	private JTextField campo1;
	private JComboBox ip;
	private JLabel nick;
	private JTextArea campochat;
	private JButton miboton;

	public LaminaMarcoCliente() {
		String nickUsuario = JOptionPane.showInputDialog("Nick: ");
		
		JLabel nameNick = new JLabel("Nick: ");
		add(nameNick);
		
		nick = new JLabel();
		nick.setText(nickUsuario);
		add(nick); 
		
		JLabel texto = new JLabel("| Online: ");
		add(texto);
		
		ip = new JComboBox();
		/*
		 * comento esto de momento
		
		ip.addItem("Usuario 1");
		ip.addItem("Usuario 2");
		ip.addItem("Usuario 3");*/
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
				datos.setIp(ip.getSelectedItem().toString());
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
			//Recepcion del servidor por  el puerto 9090
			ServerSocket servidorCliente = new ServerSocket(9090);
			Socket cliente;
			PaqueteEnvio paqueteRecibido;
			
			while(true) {
				cliente = servidorCliente.accept();
				
				ObjectInputStream flujoEntrada = new ObjectInputStream(cliente.getInputStream());
				
				paqueteRecibido = (PaqueteEnvio) flujoEntrada.readObject();
				
				if(!paqueteRecibido.getMensaje().equals("online")){
					
					//Montamos el objeto recibido
				campochat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
					
				}else {
					
					// campochat.append("\n "+paqueteRecibido.getIps());
					
					//metemos las ips de los clientes en el combo box
					ArrayList<String> IpsMenu = new ArrayList<String>();
					IpsMenu = paqueteRecibido.getIps();
					
					
					//borramos el combobox antes de rellenarlo
					ip.removeAllItems();
					
					for (String z : IpsMenu) {
						
						ip.addItem(z);
						
					}
				}
				
				
				
				
				
			}
			
		}catch(Exception e) {
			
		}
	}

}

//lo serializamos para que todas las instancias sean "desmenuzables"
class PaqueteEnvio implements Serializable{
	
	private String nick,ip,mensaje;
	private ArrayList<String> Ips;
	
	

	public ArrayList<String> getIps() {
		return Ips;
	}

	public void setIps(ArrayList<String> ips) {
		Ips = ips;
	}

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