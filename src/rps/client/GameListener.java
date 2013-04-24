package rps.client;

import java.rmi.Remote;
import java.rmi.RemoteException;

import rps.game.Game;
import rps.game.data.Player;

/**
 * {@code GameListener} is the interface of a client that is accessed from a
 * {@code Game}. All methods are called to indicate an event during the game or
 * to request an action from the player.
 */
public interface GameListener extends Remote {

	/**
	 * returns the {@code Player} that is boudn to this {@code GameListener}.
	 * This must not change during the game.
	 */
	public Player getPlayer() throws RemoteException;

	/**
	 * Indicates that a message was sent to the chat.
	 */
	public void chatMessage(Player sender, String message) throws RemoteException;

	/**
	 * Called to indicate that an initial assignment must be provided to start a
	 * game.
	 */
	public void provideInitialAssignment(Game game) throws RemoteException;

	/**
	 * Called to request an initial choice to be able to select the starting
	 * player. This method is called multiple times if the initial choice leads
	 * to a draw and players need to re-choose.
	 */
	public void provideInitialChoice() throws RemoteException;

	/**
	 * called to indicate that the assignment of the initial field is done and
	 * that the game is now started
	 */
	public void startGame() throws RemoteException;

	/**
	 * called to indicate that the next move must be provided.
	 */
	public void provideNextMove() throws RemoteException;

	/**
	 * called to indicate that a move happened on the field.
	 */
	public void figureMoved() throws RemoteException;

	/**
	 * called to indicate that there was a fight.
	 */
	public void figureAttacked() throws RemoteException;

	/**
	 * called to request a new figure kind, after a drawn fight
	 */
	public void provideChoiceAfterFightIsDrawn() throws RemoteException;

	/**
	 * called to indicate that the game is finished and the client lost.
	 */
	public void gameIsLost() throws RemoteException;

	/**
	 * called to indicate that the game is finished and the client won.
	 */
	public void gameIsWon() throws RemoteException;

	/**
	 * called to indicate that the game is drawn.
	 */
	public void gameIsDrawn() throws RemoteException;
}