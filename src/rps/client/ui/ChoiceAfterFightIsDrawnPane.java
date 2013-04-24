package rps.client.ui;

import rps.icons.Icons;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import rps.game.Game;
import rps.game.data.FigureKind;
import rps.game.data.Player;

public class ChoiceAfterFightIsDrawnPane extends JDialog {
	private static final long serialVersionUID = 7345805496191360184L;
	
	private Container choicePane = getContentPane();
	private JPanel choicePanel = new JPanel();
	
	private JLabel instruction = new JLabel();
	
	//Buttons Stein Schere Papier
	private JButton rock = new JButton();
	private JButton scissors = new JButton();
	private JButton paper = new JButton();
	
	private Game game;
	private Player player;
	
	private GamePane gamePane;
	
	public ChoiceAfterFightIsDrawnPane(Game game, GamePane gamePane, Player player, JFrame parent){
		
		super(parent, "DRAWN!!", ModalityType.DOCUMENT_MODAL);
		this.gamePane = gamePane;
		
		this.setSize(500, 200);
		this.setLocationRelativeTo(parent);
		this.game = game;
		this.player = player;
		

		// Die Größe des Fensters ist unveränderbar
		this.setResizable(false);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		choicePane.setLayout(new BoxLayout(choicePane, BoxLayout.Y_AXIS));
		
		instruction.setText("DRAWN!! Please set a new figure kind for this fight:");
		instruction.setAlignmentX(CENTER_ALIGNMENT);
		instruction.setFont(new Font("Dialog", 0, 20));
		
		//fügt dem choicePane das choicePanel hinzu
		choicePane.add(instruction);
		choicePane.add(choicePanel);
		
		//setzt das choicePanel Layout
		choicePanel.setLayout(new GridLayout(1,3));
		
		//fügt dem choicePanel die Buttons Stein, Papier,Schere hinzu
		choicePanel.add(rock); 
		choicePanel.add(paper); 
		choicePanel.add(scissors);

		rock.setIcon(Icons.rock_white);
		scissors.setIcon(Icons.scissors_white);
		paper.setIcon(Icons.paper_white);
		
		bindButtons();
		
		this.setVisible(true);
	}
	
	public void closeDialog() {
		this.gamePane.showAsStatus("Waiting for player to set new figuretype");
		this.dispose();
	}
	
	private void bindButtons() {
		rock.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent action) {
				try {
					game.setUpdatedKindAfterDraw(player, FigureKind.ROCK);
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
				closeDialog();
				
			}
		});
		scissors.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent action) {
				try {
					game.setUpdatedKindAfterDraw(player, FigureKind.SCISSORS);
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
				closeDialog();
				
			}
		});
		paper.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent action) {
				try {
					game.setUpdatedKindAfterDraw(player, FigureKind.PAPER);
				}
				catch (RemoteException e) {
					e.printStackTrace();
				}
				closeDialog();
				
			}
		});
	}
}
