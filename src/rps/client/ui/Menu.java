package rps.client.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import rps.client.UIController;

public class Menu {

	private final JFrame frame;
	private final UIController controller;

	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu menuGame = new JMenu("Game");
	private final JMenuItem menuGameNew = new JMenuItem("New");
	private final JMenuItem menuGameSurrender = new JMenuItem("Surrender");
	private final JMenuItem menuGameAbout = new JMenuItem("About");
	private final JMenuItem menuGameExit = new JMenuItem("Exit");

	public Menu(JFrame frame, UIController controller) {

		this.frame = frame;
		this.controller = controller;

		buildMenuStructure();
		bindMenuActions();
	}

	private void buildMenuStructure() {
		menuGame.setMnemonic(KeyEvent.VK_G);

		menuBar.add(menuGame);
		menuGame.add(menuGameNew);
		menuGame.add(menuGameSurrender);
		menuGame.addSeparator();
		menuGame.add(menuGameAbout);
		menuGame.addSeparator();
		menuGame.add(menuGameExit);
		frame.setJMenuBar(menuBar);

		menuGameSurrender.setEnabled(false);
		menuGameNew.setEnabled(false);
	}

	private void bindMenuActions() {
		menuGameSurrender.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.handleSurrender();

			}
		});
		menuGameNew.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.handleNewGame();

			}
		});
		menuGameAbout.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.handleAbout();
			}
		});
		menuGameExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.handleExit();
			}
		});
	}

	public void gameStarted() {
		menuGameNew.setEnabled(true);
		menuGameSurrender.setEnabled(true);
	}

	public void gameEnded() {
		menuGameNew.setEnabled(true);
		menuGameSurrender.setEnabled(false);
	}

	public void reset() {
		menuGameNew.setEnabled(false);
		menuGameSurrender.setEnabled(false);
	}
}