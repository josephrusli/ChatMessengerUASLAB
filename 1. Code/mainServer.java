import javax.swing.*;



public class mainServer 
{
	public static void main(String[] args)
	{
		JFrame window = new JFrame("Server");
		Server server1 = new Server();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(server1);
		window.setSize(700,550);
		window.setVisible(true);
	}
	
}