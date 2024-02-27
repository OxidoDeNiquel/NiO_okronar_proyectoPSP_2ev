import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

class EncriptarTextoServidor implements ActionListener{
	private JTextArea areatexto;

    // Constructor que recibe la referencia al JTextArea
    public EncriptarTextoServidor(JTextArea areatexto) {
        this.areatexto = areatexto;
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		System.out.println("Encriptando...");
		String mensajeOriginal = areatexto.getText();
        String mensajeEncriptado = AESCrypt.encrypt(mensajeOriginal);
        

        // Guardar el mensaje encriptado en un archivo
        AESCrypt.saveToFile(mensajeEncriptado, "archivo_encriptado.txt");
		
	}
	
}

class DesencriptarTextoServidor implements ActionListener{
	private JTextArea areatexto;

    // Constructor que recibe la referencia al JTextArea
    public DesencriptarTextoServidor(JTextArea areatexto) {
        this.areatexto = areatexto;
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		// Cargar el contenido desencriptado desde el archivo
		System.out.println("Desencriptando...");
        String mensajeDesencriptado = AESCrypt.loadFromFile("archivo_encriptado.txt");

        // Mostrar el mensaje desencriptado en el JTextArea
        if (mensajeDesencriptado != null) {
            areatexto.setText(mensajeDesencriptado);
        }
		
	}
	
}

class MarcoServidor extends JFrame implements Runnable {
    private JButton botonEncriptar;
    private JButton botonDesencriptar;
    private JTextArea areatexto;

    public MarcoServidor() {
        setBounds(1200, 300, 280, 350);

        JPanel milamina = new JPanel();
        milamina.setLayout(new BorderLayout());

        areatexto = new JTextArea();
        milamina.add(new JScrollPane(areatexto), BorderLayout.CENTER);
        add(milamina);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));

        botonEncriptar = new JButton("Guardar");
        EncriptarTextoServidor actionEncriptar = new EncriptarTextoServidor(areatexto);
        botonEncriptar.addActionListener(actionEncriptar);
        panelBotones.add(botonEncriptar);

        botonDesencriptar = new JButton("Importar");
        DesencriptarTextoServidor actionDesencriptar = new DesencriptarTextoServidor(areatexto);
        botonDesencriptar.addActionListener(actionDesencriptar);
        panelBotones.add(botonDesencriptar);

        add(panelBotones, BorderLayout.SOUTH);

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

  }
