package rps.client;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import rps.client.ui.GamePane;
import rps.client.ui.Menu;
import rps.client.ui.StartupPane;
import rps.client.ui.WaitingPane;

public class UIController {

	private StartupPane startupPane;
	private WaitingPane waitingPane;
	private GamePane gamePane;
	private GameController gameController;
	private Menu menu;
	
	private JFrame frame;
	
	public UIController (JFrame frame) {
		this.frame = frame;
	}

	public void setComponents(Menu menu, StartupPane startupPane, WaitingPane waitingPane, GamePane gamePane, GameController gameController) {
		this.menu = menu;
		this.startupPane = startupPane;
		this.waitingPane = waitingPane;
		this.gamePane = gamePane;
		this.gameController = gameController;
	}

	public void handleSurrender() {
		gameController.surrender();
		menu.gameEnded();
	}
	
	// Zeigt einen kleinen About-Dialog
	public void handleAbout() {
		JOptionPane.showMessageDialog(null, "This project was created by Manuel Kress and Stefan Schmidt", "About", 1);
	}

	public void handleExit() {
		gameController.exit();
		System.exit(0);
	}

	public void handleNewGame() {
		gameController.resetForNewGame();
		menu.reset();
		switchBackToStartup();
	}

	public void switchToWaitingForOpponentPane() {
		startupPane.hide();
		waitingPane.show();
	}

	public void stopWaitingAndSwitchBackToStartup() {
		gameController.unregister();
		switchBackToStartup();
	}

	public void switchToGamePane() {
		menu.gameStarted();
		waitingPane.hide();
		frame.setVisible(false);
		// Das Fenster auf die passende Größe vergrößern
		frame.setSize(1000, 796);
		// Neu zentrieren
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void switchBackToStartup() {
		gamePane.hide();
		waitingPane.hide();
		startupPane.show();
		frame.setVisible(false);
		// Das Fenster wieder auf die kleine Größe bringen
		frame.setSize(268, 182);
		// Neu zentrieren
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}