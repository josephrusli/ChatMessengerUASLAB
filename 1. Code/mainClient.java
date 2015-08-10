import javax.swing.*;



public class mainClient
{
	public static void main(String[] args)
	{
		JFrame window = new JFrame("Client");
		Client client1 = new Client();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(client1);
		window.setSize(700,550);
		window.setVisible(true);
	}
	
}