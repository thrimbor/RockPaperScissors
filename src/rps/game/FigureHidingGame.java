package rps.game;

import java.rmi.RemoteException;

import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

/**
 * This decorator is used to remove all information from get* methods that are
 * not visible for the corresponding player. Most importantly this is the
 * FigureKind of all Figure on the field that are undiscovered yet and do belong
 * to the other player.
 */
public class FigureHidingGame implements Game {

	private final Game game;
	private Player player;

	public FigureHidingGame(Game game, Player player) throws RemoteException {
		this.game = game;
		// Den Spieler merken, da jeder Spieler nur seine eigenen und die aufgedeckten Figuren sehen darf
		this.player = player;
	}

	@Override
	public void setInitialAssignment(Player p, FigureKind[] assignment) throws RemoteException {
		game.setInitialAssignment(p, assignment);
	}

	@Override
	public Figure[] getField() throws RemoteException {
		// Eine Kopie des Spielfeldes holen, wir wollen das Original nicht mit Hidden-Figure zerstören
		Figure[] board = this.game.getField().clone();
		
		// Alle verdeckten und nicht dem Spieler gehörenden Figuren werden mit verstecktem Typ geklont und ersetzt
		for (int counter = 0; counter < 42; counter++) {
			if ((board[counter] != null) && !board[counter].belongsTo(this.player) && !board[counter].isDiscovered()) {
				board[counter] = board[counter].cloneWithHiddenKind();
			}
		}
		
		// Das veränderte Spielfeld zurückgeben
		return board;
	}

	@Override
	public void move(Player p, int from, int to) throws RemoteException {
		game.move(p, from, to);
	}

	@Override
	public Move getLastMove() throws RemoteException {
		// Den Move holen
		Move move = this.game.getLastMove();
		
		if (move == null) return null;
		
		move = new Move(move.getFrom(), move.getTo(), move.getOldField().clone());
		
		// Das Spielfeld vom Move holen, wir müssen nicht klonen, da im Move bereits eine Kopie vorliegt
		Figure[] board = move.getOldField();
		
		// Alle verdeckten und nicht dem Spieler gehörenden Figuren werden mit verstecktem Typ geklont und ersetzt
		for (int counter = 0; counter < 42; counter++) {
			if ((board[counter] != null) && !board[counter].belongsTo(this.player) && !board[counter].isDiscovered()) {
				board[counter] = board[counter].cloneWithHiddenKind();
			}
		}
		
		// Den Move mit dem veränderten Spielfeld zurückgeben
		return move;
	}

	@Override
	public void sendMessage(Player p, String message) throws RemoteException {
		game.sendMessage(p, message);
	}

	@Override
	public void setInitialChoice(Player p, FigureKind kind) throws RemoteException {
		game.setInitialChoice(p, kind);
	}

	@Override
	public void setUpdatedKindAfterDraw(Player p, FigureKind kind) throws RemoteException {
		game.setUpdatedKindAfterDraw(p, kind);
	}

	@Override
	public void surrender(Player p) throws RemoteException {
		game.surrender(p);
	}

	@Override
	public Player getOpponent(Player p) throws RemoteException {
		return game.getOpponent(p);
	}
}