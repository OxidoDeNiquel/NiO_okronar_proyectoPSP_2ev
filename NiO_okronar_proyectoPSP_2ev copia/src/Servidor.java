import javax.swing.*;

import java.awt.*;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Servidor {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		MarcoServidor mimarco = new MarcoServidor();

		mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}
}

class MarcoServidor extends JFrame implements Runnable {

	public MarcoServidor() {

		setBounds(1200, 300, 280, 350);

		JPanel milamina = new JPanel();
		milamina.setLayout(new BorderLayout());

		areatexto = new JTextArea();
		milamina.add(areatexto, BorderLayout.CENTER);
		add(milamina);

		setVisible(true);

		Thread miHilo = new Thread(this);
		miHilo.start();

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ServerSocket servidor = new ServerSocket(1234);

			String nick, ip, mensaje;
			
			//creamos un array list para almacenar las ips de los usuarios conectados
			
			ArrayList <String> listaIp = new ArrayList<String>();

			//lo metemos en el paquete(despues de modificar la clase)
			PaqueteEnvio paquete_recibido;

			while (true) {
				// Recepcion del cliente1
				Socket misocket = servidor.accept();

				ObjectInputStream paquete_datos = new ObjectInputStream(misocket.getInputStream());

				// Montamos el objeto recibido
				paquete_recibido = (PaqueteEnvio) paquete_datos.readObject();

				nick = paquete_recibido.getNick();
				ip = paquete_recibido.getIp();
				mensaje = paquete_recibido.getMensaje();

				if (!mensaje.equals(" online")) {

					areatexto.append("\n" + nick + ": " + mensaje + " para " + ip);

					// Envio al cliente2
					Socket envioDestinatario = new Socket(ip, 9090);
					ObjectOutputStream paqueteEnvioDestinatario = new ObjectOutputStream(
							envioDestinatario.getOutputStream());

					paqueteEnvioDestinatario.writeObject(paquete_recibido);

					paqueteEnvioDestinatario.close();
					envioDestinatario.close();
					misocket.close();

				} else {

					// --- DETECTA USUARIOS ONLINE ---//
					InetAddress localizador = misocket.getInetAddress();
					String ipRemota = localizador.getHostAddress();
 
					System.out.println("Online " + ipRemota);
					
					//cuando se conecta un usuario se almacena en el array
										
					listaIp.add(ipRemota);
					
					paquete_recibido.setIps(listaIp); 
					
					
					for (String s : listaIp) {
						
						
						System.out.println("Array: " + s);
						Socket envioDestinatario = new Socket(s, 9090);
						ObjectOutputStream paqueteEnvioDestinatario = new ObjectOutputStream(
								envioDestinatario.getOutputStream());

						paqueteEnvioDestinatario.writeObject(paquete_recibido);

						paqueteEnvioDestinatario.close();
						envioDestinatario.close();
						misocket.close();
						
					}
					
					
					// ------------------------------//
				}
			}

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}

	private JTextArea areatexto;

  }