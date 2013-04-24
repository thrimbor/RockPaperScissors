package rps.game;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

public class RMIGame extends UnicastRemoteObject implements Game {

	private static final long serialVersionUID = -1433680624081182944L;

	protected Game game;

	public RMIGame(Game game) throws RemoteException {
		super();
		this.game = game;
	}

	@Override
	public void setInitialAssignment(Player p, FigureKind[] assignment) throws RemoteException {
		game.setInitialAssignment(p, assignment);
	}

	@Override
	public Figure[] getField() throws RemoteException {
		return game.getField();
	}

	@Override
	public void move(Player p, int from, int to) throws RemoteException {
		game.move(p, from, to);
	}

	@Override
	public void sendMessage(Player p, String message) throws RemoteException {
		game.sendMessage(p, message);
	}

	@Override
	public Player getOpponent(Player p) throws RemoteException {
		return game.getOpponent(p);
	}

	@Override
	public void surrender(Player p) throws RemoteException {
		game.surrender(p);
	}

	@Override
	public void setUpdatedKindAfterDraw(Player p, FigureKind kind) throws RemoteException {
		game.setUpdatedKindAfterDraw(p, kind);
	}

	@Override
	public void setInitialChoice(Player p, FigureKind kind) throws RemoteException {
		game.setInitialChoice(p, kind);
	}

	@Override
	public Move getLastMove() throws RemoteException {
		return game.getLastMove();
	}
}