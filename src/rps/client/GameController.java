package rps.client;

import static rps.network.NetworkUtil.hostNetworkGame;

import java.rmi.RemoteException;

import rps.client.ui.GamePane;
import rps.game.Game;
import rps.game.data.Move;
import rps.game.data.Player;
import rps.network.GameRegistry;
import rps.network.NetworkUtil;

/**
 * this class is responsible for controlling all game related events.
 */
public class GameController implements GameListener {

	private UIController uiController;
	private GamePane gamePane;

	private GameRegistry registry;
	private Player player;
	private Game game;
	private boolean didAlreadySurrender = false;

	public void setComponents(UIController uiController, GamePane gamePane) {
		this.uiController = uiController;
		this.gamePane = gamePane;
	}

	public void startHostedGame(Player player, String host) {
		this.player = player;
		registry = hostNetworkGame(host);
		register(player, this);
	}

	public void startJoinedGame(Player player, String host) {
		this.player = player;
		registry = NetworkUtil.requestRegistry(host);
		register(player, this);
	}

	public void startAIGame(Player player, GameListener ai) {
		this.player = player;
		registry = NetworkUtil.hostLocalGame();
		register(new Player(ai.toString()), ai);
		register(player, this);
	}

	private void register(Player player, GameListener listener) {
		try {
			GameListener multiThreadedListener = decorateListener(listener);
			registry.register(multiThreadedListener);
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	private static GameListener decorateListener(GameListener listener) {
		try {
			listener = new MultiThreadedGameListener(listener);
			listener = new RMIGameListener(listener);
			return listener;
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	public void unregister() {
		try {
			if (registry != null) {
				registry.unregister(player);
			}
		} catch (RemoteException e) {
			throw new RuntimeException(e);
		}
	}

	public void surrender() {
		if (!didAlreadySurrender ) {
			try {
				game.surrender(player);
			} catch (RemoteException e) {
				throw new RuntimeException(e);
			}
			didAlreadySurrender = true;
		}
	}

	public void resetForNewGame() {
		surrender();
	}

	public void exit() {
		if (registry != null) {
			unregister();
		}
		if (game != null) {
			surrender();
		}
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void chatMessage(Player sender, String message) throws RemoteException {
		gamePane.receivedMessage(sender, message);
	}

	@Override
	public void provideInitialAssignment(Game game) throws RemoteException {
		// Der Spieler soll nun seine Startaufstellung bereitstellen
		this.game = game;
		
		// game-Pane anzeigen
		uiController.switchToGamePane();
		
		// Das Spiel geht los
		gamePane.startGame(player, game);
		
		// Statusbar updaten
		gamePane.showAsStatus("Please choose your initial assignment");
		
		// Für das Setzen der Startaufstellung bereitmachen
		gamePane.prepareBoardButtonsForInitialAssignment();
	}

	@Override
	public void provideInitialChoice() throws RemoteException {
		// Der Spieler muss nun eine Runde Schere, Stein, Papier spielen
		gamePane.showAsStatus("Please choose your figure to decide who starts");
		gamePane.showInitialChoicePane();
	}

	@Override
	public void startGame() throws RemoteException {
		// Das Spielfeld vorbereiten
		this.gamePane.prepareBoardButtonsForGame();
		
		// Den aktuellen Stand des Spielfeldes (beide Startaufstellungen) holen
		this.gamePane.updateBoard(game.getField());
		
		// Wir warten auf den gegnerischen Zug (wenn wir als erster dran sind wird die Statusbar sowieso von provideNextMove() aktualisiert)
		gamePane.showAsStatus("Waiting for opponent move");
	}

	@Override
	public void provideNextMove() throws RemoteException {
		// Statusbar updaten
		gamePane.showAsStatus("Please make your move");
		
		// Dem Spielfeld mitteilen, dass wir einen Zug erwarten
		this.gamePane.askForNextMove();
	}

	@Override
	public void figureMoved() throws RemoteException {
		gamePane.move();
		gamePane.updateBoard(game.getField());
		
		// Den Zug holen
		Move lastMove = game.getLastMove();
		
		// Wenn es ein gegnerischer Zug war, zeigen wir ihn an
		if (!lastMove.getOldField()[lastMove.getFrom()].belongsTo(player)) {
			gamePane.visualizeLastMove(lastMove);
		}
	}

	@Override
	public void figureAttacked() throws RemoteException {
		gamePane.updateBoard(game.getField());
		
		// Den Zug holen
		Move lastMove = game.getLastMove();
		
		// Wenn es ein gegnerischer Zug war, zeigen wir ihn an
		if (!lastMove.getOldField()[lastMove.getFrom()].belongsTo(player)) {
			gamePane.visualizeLastMove(lastMove);
		}
		
		// Wenn das Zielfeld des letzten Zuges nicht leer war, war es ein Angriff
		if (lastMove.getOldField()[lastMove.getTo()] != null) {
			gamePane.showAttackInChat(lastMove);
		}
	}

	@Override
	public void provideChoiceAfterFightIsDrawn() throws RemoteException {
		gamePane.showAsStatus("The fight was drawn, please choose a new figure");
		gamePane.showChoiceAfterFightIsDrawnPane();
	}

	@Override
	public void gameIsLost() throws RemoteException {
		gamePane.showAsStatus("You lost");
		gamePane.updateBoard(game.getField());
		gamePane.showLostMessage();
	}

	@Override
	public void gameIsWon() throws RemoteException {
		gamePane.showAsStatus("You won");
		gamePane.updateBoard(game.getField());
		gamePane.showWinMessage();
	}

	@Override
	public void gameIsDrawn() throws RemoteException {
		gamePane.showAsStatus("Game is drawn");
		gamePane.updateBoard(game.getField());
		gamePane.showDrawnMessage();
	}
}