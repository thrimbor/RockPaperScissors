package rps.game;

import java.rmi.Remote;
import java.rmi.RemoteException;

import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

/**
 * {@code Game} is an interface for the client that defines all methods that are
 * needed to play the game according to the game protocol.
 * <p>
 * The game assumes that all provided parameters are valid. Nevertheless it has
 * to assert that players do not move multiple times.
 */
public interface Game extends Remote {

	/**
	 * Sends a chat message to all game participants.
	 */
	public void sendMessage(Player p, String message) throws RemoteException;

	/**
	 * Provides an initial assignment for the game. The initial assignment must
	 * only be passed once by each player. Each player passes the assignment
	 * according to its representation (i.e. one representation is inverted
	 * w.r.t. the other). This class checks the initial assignment for validity.
	 * Called as a reaction to {@code GameListener.provideInitialAssignment}
	 */
	public void setInitialAssignment(Player p, FigureKind[] assignment) throws RemoteException;

	/**
	 * Provides the server the initial choice that allows to identify the
	 * starting player. Called as a reaction to
	 * {@code GameListener.provideInitialChoice}
	 */
	public void setInitialChoice(Player p, FigureKind kind) throws RemoteException;

	/**
	 * Moves a figure of the {@code movingPlayer} from the position
	 * {@code fromIndex} to the position {@code toIndex}. Called as a reaction
	 * to {@code GameListener.nextMove}
	 */
	public void move(Player movingPlayer, int fromIndex, int toIndex) throws RemoteException;

	/**
	 * Called from the client to provide a new kind after a fight between two
	 * figures with the same kind. Called as a reaction to
	 * {@code GameListener.provideChoiceAfterFightIsDrawn}.
	 */
	public void setUpdatedKindAfterDraw(Player p, FigureKind kind) throws RemoteException;

	/**
	 * Called by a client that decides to surrender and quit the game.
	 */
	public void surrender(Player p) throws RemoteException;

	/**
	 * Returns the current field of the game.
	 *
	 * @return The game field as an array of size 42 (=6*7). A null value in the
	 *         array means that the cell is empty.
	 */
	public Figure[] getField() throws RemoteException;

	/**
	 * Returns the last move that was performed in the game, either by the
	 * calling player or by the other one.
	 *
	 * @return The last move that was performed.
	 */
	public Move getLastMove() throws RemoteException;

	/**
	 * Returns the opponent player object.
	 */
	public Player getOpponent(Player p) throws RemoteException;
}