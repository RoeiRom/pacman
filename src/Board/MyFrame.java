package Board;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import Players.Direction;
@SuppressWarnings("all")

public class MyFrame extends JFrame implements KeyListener, ActionListener {

	private Board board;
	private JPanel start;
	private JButton startGame;
	private JButton endGame;
	private boolean chosenOption;
	
	public MyFrame() 
	{
		super("Pac Man");
		chosenOption = false;
		startGame = new JButton();
		endGame = new JButton();
		this.setSize(665, 650);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Image logo = Toolkit.getDefaultToolkit().getImage("Logo/logo.png");
		Image startImg = Toolkit.getDefaultToolkit().getImage("Images/OpeningScreen.jpg");
		this.setIconImage(logo);
		this.setVisible(true);
		this.setResizable(false);
		// TODO Auto-generated constructor stub
		start = new JPanel();
		start.setLayout(null);
		start.setSize(665, 643);
		startGame.setVisible(true);
		startGame.setLocation(304, 349);
		startGame.setSize(290, 62);
		endGame.setVisible(true);
		endGame.setLocation(400, 438);
		endGame.setSize(104, 50);
		startGame.addActionListener(this);
		endGame.addActionListener(this);
		start.add(startGame);
		start.add(endGame);
		this.add(start);
		while (chosenOption == false)
		{
			start.getGraphics().drawImage(startImg, 0, 0, start.getWidth(), start.getHeight(), null);
		}
		this.setSize(Board.squareWidth * 28 + 6, Board.squareWidth * 35 + 6);
		this.remove(start);
		this.setFocusable(true);
		this.requestFocusInWindow();
		this.addKeyListener(this);
		this.board = new Board(this);
	}

	@Override
	public void keyTyped(KeyEvent e) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) 
	{
		switch (e.getKeyCode())
		{
		case KeyEvent.VK_RIGHT:
			this.board.getPacman().setPacmanDirection(Direction.RIGHT);
			break;
		case KeyEvent.VK_LEFT:
			this.board.getPacman().setPacmanDirection(Direction.LEFT);
			break;
		case KeyEvent.VK_UP:
			this.board.getPacman().setPacmanDirection(Direction.UP);
			break;
		case KeyEvent.VK_DOWN:
			this.board.getPacman().setPacmanDirection(Direction.DOWN);
			break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		// TODO Auto-generated method stub
		JButton pressed = (JButton)e.getSource();
		if (pressed.equals(this.startGame))
		{
			chosenOption = true;
		}
		else
		{
			JOptionPane.showMessageDialog(this, "Bye Bye!", "You Pressed Exit", JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}
	}
}
