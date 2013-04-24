package rps.network;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import rps.client.GameListener;
import rps.game.FieldRotatingGame;
import rps.game.FigureHidingGame;
import rps.game.Game;
import rps.game.GameImpl;
import rps.game.MultiThreadedGame;
import rps.game.RMIGame;
import rps.game.ValidatingGame;
import rps.game.data.Player;

public class GameRegistryImpl extends UnicastRemoteObject implements GameRegistry {

	private static final long serialVersionUID = 1L;

	private Player waitingPlayer;
	private GameListener waitingGameListener;

	protected GameRegistryImpl() throws RemoteException {
		// needed for RMI, should not be used by clients
		super();
	}

	@Override
	public synchronized void register(GameListener listener) throws RemoteException {

		Player player = listener.getPlayer();

		boolean isPlayerRegistered = waitingPlayer != null;

		if (!isPlayerRegistered) {
			waitingPlayer = player;
			waitingGameListener = listener;
		} else {
			assertUniquePlayers(player);
			startGame(listener, waitingGameListener);
			clearFields();
		}
	}

	private void assertUniquePlayers(Player player) {
		boolean hasNewPlayerTheSameName = waitingPlayer.equals(player);
		if (hasNewPlayerTheSameName) {
			throw new IllegalArgumentException("nick already registered");
		}
	}

	private void startGame(GameListener listener2, GameListener listener1) throws RemoteException {

		Game game = new GameImpl(listener1, listener2);

		Game firstDecoration = decorateFirst(listener1.getPlayer(), game);
		listener1.provideInitialAssignment(firstDecoration);

		Game secondDecoration = decorateSecond(listener2.getPlayer(), game);
		listener2.provideInitialAssignment(secondDecoration);
	}

	private Game decorateFirst(Player player, Game game) throws RemoteException {
		game = new FigureHidingGame(game, player);
		game = new MultiThreadedGame(game);
		game = new ValidatingGame(game, player);
		game = new RMIGame(game);
		return game;
	}

	private Game decorateSecond(Player player, Game game) throws RemoteException {
		game = new FieldRotatingGame(game);
		game = new FigureHidingGame(game, player);
		game = new MultiThreadedGame(game);
		game = new ValidatingGame(game, player);
		game = new RMIGame(game);
		return game;
	}

	private void clearFields() {
		waitingGameListener = null;
		waitingPlayer = null;
	}

	@Override
	public synchronized void unregister(Player player) throws RemoteException {
		if (waitingPlayer != null && waitingPlayer.equals(player)) {
			clearFields();
		}
	}
}