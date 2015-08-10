
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;
import java.security.*;
import javax.crypto.*;
import java.util.*;
import javax.crypto.spec.*;
public class Client extends JPanel implements Runnable, ActionListener, KeyListener {


	private ServerSocket serversocket;
	private BufferedReader br1, br2;
	private PrintWriter pr1;
	private Socket socket;
	Thread t1;
	private String sIn="",decryptText="";
	private JScrollPane jsp,jsp2;
	private JTextField text;
	private String sText="", sChat="", sStatus="";
	private JLabel status;
	private JTextArea chat, decryptField;
	private JButton send;
	private boolean running;
	boolean connect;
	private KeyGenerator keygenerator;
	private SecretKey myDesKey;
	private Cipher desCipher;
	private String encodedKey;
	private String textEn = new String();
	private String textDe = new String();
	private String clientKey = new String();

	
public Client() {
  
	String IPAddress;
	int Port;
	Scanner input = new Scanner(System.in);
	System.out.print("Enter IP Address: ");
	IPAddress = input.next();
	System.out.print("Enter Port: ");
	Port = input.nextInt();
	
  
	try {
		t1 = new Thread(this);

		t1.start();
        socket = new Socket(IPAddress, Port);
		connect = socket.isConnected() && !socket.isClosed();
	
		} catch (Exception e) {
    }
	
 }


public void run() {
	init();
	
	while(running) {
		
		update();
		try {
				update();
				pr1 = new PrintWriter(socket.getOutputStream(), true);
				br2 = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				//sIn = br2.readLine();
				// membaca secretKey dari Client
				clientKey = br2.readLine();
				// membaca pesan encrypted dari Client
				sIn = br2.readLine();
				sChat = sChat + "\n" + "Server : " + sIn;
				
				// mendekripsi pesan dari Client
				decrypt(sIn);
				decryptText = decryptText + "\n" + "Server : " + textDe;
			}
		catch(Exception e) {	
		}
		}
}	

public void update() {
	
	chat.setText(sChat);
	decryptField.setText(decryptText);
	status.setText(sStatus);
}

public void init() {
	running = true;
	setLayout(null);
	
	Font f = new Font("Arial", Font.BOLD,14);
	
	chat = new JTextArea(sChat);
	chat.setBackground(Color.WHITE);
	chat.setFont(f);
    chat.setEditable(false);
	if(!connect) {
		sStatus = "Connected to Server";
	} else
	{
		sStatus = "Connected with " +  socket.getInetAddress().getHostAddress();
	}
	
	decryptField = new JTextArea(decryptText);
	decryptField.setBackground(Color.WHITE);
	decryptField.setFont(f);
    decryptField.setEditable(false);
	
	status = new JLabel(sStatus);
	status.setBackground(Color.WHITE);
	status.setFont(f);
	status.setBounds(20,10,600,50);
	add(status);
	
	jsp = new JScrollPane(chat);
	jsp.setBounds(20,60,300,290);
	add(jsp);
	jsp2 = new JScrollPane(decryptField);
	jsp2.setBounds(320,60,300,290);
	add(jsp2);
	
	text = new JTextField();
	text.setFont(f);
	text.setBounds(20,370,500,40);
	text.addKeyListener(this);
	add(text);
	
	text.requestFocus();
	
	send = new JButton("SEND");
	send.setBounds(535,370,85,40);
	send.addActionListener(this);
	add(send);
}


public void actionPerformed(ActionEvent e)
{
		Object source = e.getSource();
		if(connect) {
			if(source == send)
			{
				sChat = sChat + "\n" + "Client : " + text.getText();
				decryptText = decryptText + "\n";
				encrypt(text.getText());
				pr1.println(encodedKey);
				pr1.println(textEn);
				
				chat.setText(sChat);
				decryptField.setText(decryptText);
				text.setText(null);
				text.requestFocus();

			}
		}
}

public void keyTyped(KeyEvent key) {

}

public void keyPressed(KeyEvent key) {
	int code = key.getKeyCode();
	if(code == KeyEvent.VK_ENTER) {
		send.doClick();
	}
}

public void keyReleased(KeyEvent key) {}

public void encrypt(String text1) {
		
		try{
			keygenerator = KeyGenerator.getInstance("DES");
		    myDesKey = keygenerator.generateKey();
			desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			// mengencode secretkey ke dalam string agar dapat dikirimkan
			encodedKey = Base64.getEncoder().encodeToString(myDesKey.getEncoded());
			// Initialize the cipher for encryption
		    desCipher.init(Cipher.ENCRYPT_MODE, myDesKey);
		    //sensitive information
		    byte[] text = text1.getBytes();
 
		    // Encrypt the text
		    byte[] textEncrypted = desCipher.doFinal(text);
			
			textEn = new String(textEncrypted);
			
		}
		catch (Exception e) {}
	}
	
public void decrypt(String text1) {
		
		try{
			// decode key
			byte[] decodedKey = Base64.getDecoder().decode(clientKey);
			// rebuild key using SecretKeySpec
			SecretKey originalKey = new SecretKeySpec(decodedKey, 0, decodedKey.length, "DES"); 
			
			desCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
		    // Initialize the cipher for decryption
		    desCipher.init(Cipher.DECRYPT_MODE, originalKey);
			
		    byte[] text = text1.getBytes();
 

		    // Decrypt the text
		    byte[] textDecrypted = desCipher.doFinal(text);
			
			textDe = new String(textDecrypted);
		}
		catch (Exception e) {}
	}
}

