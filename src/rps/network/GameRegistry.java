package rps.network;

import java.rmi.Remote;
import java.rmi.RemoteException;

import rps.client.GameListener;
import rps.game.data.Player;

/**
 * A {@code GameRegistry} is used as a service where to clients can register for
 * a game. A game is started the moment the second clients registers.
 */
public interface GameRegistry extends Remote {

	/**
	 * Used by a client to register to this game. When the second player
	 * registers, a game is automatically started and the registrations are
	 * removed.
	 */
	public void register(GameListener controller) throws RemoteException;

	/**
	 * Used by a client to unregister from this game
	 */
	public void unregister(Player player) throws RemoteException;
}