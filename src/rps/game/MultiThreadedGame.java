package rps.game;

import static rps.client.Application.callAsync;

import java.rmi.RemoteException;
import java.util.concurrent.Callable;

import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

/**
 * this class is used to convert (blocking) method calls to the {@code Game} to
 * asynchronous (non-blocking) calls. Nevertheless, the decorated {@code Game}
 * does not need to be synchronized, it is ensured here that the calls are not
 * executed in parallel.
 */
public class MultiThreadedGame implements Game {

	private final Game game;

	public MultiThreadedGame(Game game) {
		this.game = new SynchronizedGame(game);
	}

	@Override
	public void sendMessage(final Player p, final String message) throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				game.sendMessage(p, message);
				return null;
			}
		});
	}

	@Override
	public void setInitialAssignment(final Player p, final FigureKind[] assignment) throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				game.setInitialAssignment(p, assignment);
				return null;
			}
		});
	}

	@Override
	public void setInitialChoice(final Player p, final FigureKind kind) throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				game.setInitialChoice(p, kind);
				return null;
			}
		});
	}

	@Override
	public void move(final Player movingPlayer, final int fromIndex, final int toIndex) throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				game.move(movingPlayer, fromIndex, toIndex);
				return null;
			}
		});
	}

	@Override
	public void setUpdatedKindAfterDraw(final Player p, final FigureKind kind) throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				game.setUpdatedKindAfterDraw(p, kind);
				return null;
			}
		});
	}

	@Override
	public void surrender(final Player p) throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				game.surrender(p);
				return null;
			}
		});
	}

	@Override
	public Figure[] getField() throws RemoteException {
		return game.getField();
	}

	@Override
	public Move getLastMove() throws RemoteException {
		return game.getLastMove();
	}

	@Override
	public Player getOpponent(Player p) throws RemoteException {
		return game.getOpponent(p);
	}

	/**
	 * The single purpose of this class is to synchronize all method calls to
	 * the decorated {@code Game}.
	 */
	private class SynchronizedGame implements Game {

		private final Game game;

		public SynchronizedGame(Game game) {
			this.game = game;
		}

		@Override
		public synchronized void sendMessage(Player p, String message) throws RemoteException {
			game.sendMessage(p, message);
		}

		@Override
		public synchronized void setInitialAssignment(Player p, FigureKind[] assignment) throws RemoteException {
			game.setInitialAssignment(p, assignment);
		}

		@Override
		public synchronized void setInitialChoice(Player p, FigureKind kind) throws RemoteException {
			game.setInitialChoice(p, kind);
		}

		@Override
		public synchronized void move(Player movingPlayer, int fromIndex, int toIndex) throws RemoteException {
			game.move(movingPlayer, fromIndex, toIndex);
		}

		@Override
		public synchronized void setUpdatedKindAfterDraw(Player p, FigureKind kind) throws RemoteException {
			game.setUpdatedKindAfterDraw(p, kind);
		}

		@Override
		public synchronized void surrender(Player p) throws RemoteException {
			game.surrender(p);
		}

		@Override
		public synchronized Figure[] getField() throws RemoteException {
			return game.getField();
		}

		@Override
		public synchronized Move getLastMove() throws RemoteException {
			return game.getLastMove();
		}

		@Override
		public synchronized Player getOpponent(Player p) throws RemoteException {
			return game.getOpponent(p);
		}
	}
}