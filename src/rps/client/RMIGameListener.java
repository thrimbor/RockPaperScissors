package rps.client;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import rps.game.Game;
import rps.game.data.Player;

public class RMIGameListener extends UnicastRemoteObject implements GameListener {

	private static final long serialVersionUID = -5895577378614418245L;

	private final GameListener listener;

	public RMIGameListener(GameListener listener) throws RemoteException {
		super();
		this.listener = listener;
	}

	@Override
	public Player getPlayer() throws RemoteException {
		return listener.getPlayer();
	}

	@Override
	public void figureAttacked() throws RemoteException {
		listener.figureAttacked();
	}

	@Override
	public void chatMessage(Player sender, String message) throws RemoteException {
		listener.chatMessage(sender, message);
	}

	@Override
	public void figureMoved() throws RemoteException {
		listener.figureMoved();
	}

	@Override
	public void gameIsDrawn() throws RemoteException {
		listener.gameIsDrawn();
	}

	@Override
	public void gameIsLost() throws RemoteException {
		listener.gameIsLost();
	}

	@Override
	public void gameIsWon() throws RemoteException {
		listener.gameIsWon();
	}

	@Override
	public void provideInitialAssignment(Game game) throws RemoteException {
		listener.provideInitialAssignment(game);
	}

	@Override
	public void provideNextMove() throws RemoteException {
		listener.provideNextMove();
	}

	@Override
	public void provideInitialChoice() throws RemoteException {
		listener.provideInitialChoice();
	}

	@Override
	public void provideChoiceAfterFightIsDrawn() throws RemoteException {
		listener.provideChoiceAfterFightIsDrawn();
	}

	@Override
	public void startGame() throws RemoteException {
		listener.startGame();
	}
}