package rps.client;

import static java.lang.String.format;
import static javax.swing.BoxLayout.Y_AXIS;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Vector;
import java.util.concurrent.Callable;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import rps.client.ui.GamePane;
import rps.client.ui.Menu;
import rps.client.ui.StartupPane;
import rps.client.ui.WaitingPane;

/**
 * Klasse als Wurzel der ganzen Anwendung
 * @author Manuel Kress
 *
 */
public class Application {
	public final JFrame frame = new JFrame("Rock Paper Scissors");
	private final Container rootPane;
	
	private final UIController uiController = new UIController(this.frame);
	private final GameController gameController = new GameController();

	private final Menu menu;
	private final StartupPane startupPane;
	private final WaitingPane waitingPane;
	private final GamePane gamePane;

	/**
	 * Konstruktor
	 * @param ais
	 */
	public Application(Vector<GameListener> ais) {

		//Menü erstellen
		menu = new Menu(frame, uiController);

		//Funktion des Schließen-Buttons belegen
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				uiController.handleExit();
			}
		});

		//rootPane ist der ContentPane des Frames, also des Fensters
		//Hier wird sich alles abspielen
		rootPane = frame.getContentPane();
		rootPane.setLayout(new BoxLayout(rootPane, Y_AXIS));
		rootPane.setSize(10,10);

		// Wir haben prinzipiell für die verschiedenen Status verschiedene Panes:
		// startupPane: Auswahl ob Host, Client oder gegen Bot
		// waitingPane: Warten auf zweiten Spieler
		// gamePane: Das Spiel an sich mit Brett und chat usw...
		startupPane = new StartupPane(rootPane, uiController, gameController, ais);
		waitingPane = new WaitingPane(rootPane, uiController);
		gamePane = new GamePane(rootPane, frame);
		
		// Die Größe des Fensters ist unveränderbar
		frame.setResizable(false);
		
		// Die Größe unseres Startfensters setzen
		frame.setSize(268, 182);
		// Ab in die Bildschirmmitte damit
		frame.setLocationRelativeTo(null);
		// Und sichtbar machen
		frame.setVisible(true);

		// Komponenten miteinander bekanntmachen
		wireComponents();
		
		
		//Unser Spielkern ist nun gestartet, es geht weiter per Klick auf Starten
	}
	

	/**
	 * Mit "Verkabeln" meinen die, dass man den verschiedenen Objekten Zugriff auf die
	 * benötigten Objekte gibt. Also im Endeffekt dem UIController die Panes und das Menü übergibt
	 * und dem GameController den UIController und den gamePane
	 */
	private void wireComponents() {
		uiController.setComponents(menu, startupPane, waitingPane, gamePane, gameController);
		gameController.setComponents(uiController, gamePane);
	}

	public static void callAsync(final Callable<?> callable) {
		new SwingWorker<Void, Void>() {

			@Override
			protected Void doInBackground() throws Exception {
				callable.call();
				return null;
			}

			@Override
			protected void done() {
				try {
					get();
				} catch (Exception e) {
					showErrorDialog(e);
				}
			}
		}.execute();
	}

	private static void showErrorDialog(Throwable e) {
		showMessage(format("Unexpected problem: %s", e.getMessage()));
		e.printStackTrace();
	}

	public static void showMessage(String message) {
		showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
}