package rps.game;

import java.rmi.RemoteException;

import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

/**
 * Game decorator that can be used to transparently rotate the game field. This
 * is typically used by one player, so that the game UI looks the same for both
 * players.
 */
public class FieldRotatingGame implements Game {

	private static final int positionOffset = 41;
	private final Game game;

	public FieldRotatingGame(Game game) throws RemoteException {
		this.game = game;
	}

	@Override
	public void setInitialAssignment(Player p, FigureKind[] assignment) throws RemoteException {
		FigureKind[] invertedAssignment = new FigureKind[42];
		invert(assignment, invertedAssignment);
		game.setInitialAssignment(p, invertedAssignment);
	}

	@Override
	public Figure[] getField() throws RemoteException {
		Figure[] field = game.getField();
		Figure[] invertedField = new Figure[field.length];
		invert(field, invertedField);
		return invertedField;
	}

	@Override
	public void move(Player p, int from, int to) throws RemoteException {
		game.move(p, invertIndex(from), invertIndex(to));
	}

	@Override
	public Move getLastMove() throws RemoteException {
		Move lastMove = game.getLastMove();

		if (lastMove == null) {
			return null;
		}

		int invertedFrom = invertIndex(lastMove.getFrom());
		int invertedTo = invertIndex(lastMove.getTo());
		Figure[] invertedField = new Figure[42];
		invert(lastMove.getOldField(), invertedField);
		Move invertedMove = new Move(invertedFrom, invertedTo, invertedField);

		return invertedMove;
	}

	private static int invertIndex(int position) {
		return positionOffset - position;
	}

	private static <T> void invert(T[] orig, T[] inverted) {
		int offset = orig.length - 1;
		for (int index = 0; index <= offset; index++) {
			int invertedIndex = offset - index;
			inverted[invertedIndex] = orig[index];
		}
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