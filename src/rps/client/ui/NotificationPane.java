package rps.client.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class NotificationPane extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1841545937619650733L;
	
	
	private Container content = getContentPane();
	private JLabel message = new JLabel();
	private boolean wait = false;
	
	
	JPanel panel = new JPanel();
	
	
	public NotificationPane(JFrame parent) {
		//this.setVisible(false);
		
		
		this.message.setFont(new Font("Dialog", Font.BOLD, 40));
		this.message.setForeground(Color.WHITE);
		this.message.setAlignmentX(CENTER_ALIGNMENT);
		this.message.setAlignmentY(CENTER_ALIGNMENT);
		this.message.revalidate();
		
		
		this.setSize(400, 150);
		this.setResizable(false);
		this.setUndecorated(true);
		this.setLocationRelativeTo(parent);
		
		panel.add(this.message);
		
		content.setLayout(new GridBagLayout());
		content.add(panel, new GridBagConstraints());
		
		setActionListener();
	}

	public void show(String text, Color color) {
		this.message.setText(text);
		this.message.setAlignmentX(CENTER_ALIGNMENT);
		this.content.setBackground(color);
		panel.setBackground(color);
		this.setVisible(true);
		this.wait = true;
		for (int i = 0; this.wait && i <= 300; i++) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		this.wait = false;
		dispose();
	}
	
	private void setActionListener() {
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent arg0) {
				hide();
				
			}
		});
	}
	
	public void hide() {
		this.wait = false;
	}
}
