package rps.client;

import static rps.client.Application.callAsync;

import java.rmi.RemoteException;
import java.util.concurrent.Callable;

import rps.game.Game;
import rps.game.data.Player;

/**
 * this class is used to convert (blocking) method calls to the
 * {@code GameListener} to asynchronous (non-blocking) calls. Nevertheless, the
 * decorated {@code GameListener} does not need to be synchronized, it is
 * ensured here that the calls are not executed in parallel.
 */
public class MultiThreadedGameListener implements GameListener {

	private final GameListener listener;

	public MultiThreadedGameListener(GameListener listener) {
		this.listener = new SynchronizedGameListener(listener);
	}

	@Override
	public Player getPlayer() throws RemoteException {
		return listener.getPlayer();
	}

	@Override
	public void chatMessage(final Player sender, final String message) throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				listener.chatMessage(sender, message);
				return null;
			}
		});
	}

	@Override
	public void provideInitialAssignment(final Game game) throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				listener.provideInitialAssignment(game);
				return null;
			}
		});
	}

	@Override
	public void provideInitialChoice() throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				listener.provideInitialChoice();
				return null;
			}
		});
	}

	@Override
	public void startGame() throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				listener.startGame();
				return null;
			}
		});
	}

	@Override
	public void provideNextMove() throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				listener.provideNextMove();
				return null;
			}
		});
	}

	@Override
	public void figureMoved() throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				listener.figureMoved();
				return null;
			}
		});
	}

	@Override
	public void figureAttacked() throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				listener.figureAttacked();
				return null;
			}
		});
	}

	@Override
	public void provideChoiceAfterFightIsDrawn() throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				listener.provideChoiceAfterFightIsDrawn();
				return null;
			}
		});
	}

	@Override
	public void gameIsLost() throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				listener.gameIsLost();
				return null;
			}
		});
	}

	@Override
	public void gameIsWon() throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				listener.gameIsWon();
				return null;
			}
		});
	}

	@Override
	public void gameIsDrawn() throws RemoteException {
		callAsync(new Callable<Void>() {
			@Override
			public Void call() throws RemoteException {
				listener.gameIsDrawn();
				return null;
			}
		});
	}

	/**
	 * The single purpose of this class is to synchronize all method calls to
	 * the decorated {@code GameListener}.
	 */
	private class SynchronizedGameListener implements GameListener {

		private final GameListener listener;

		public SynchronizedGameListener(GameListener listener) {
			this.listener = listener;
		}

		@Override
		public synchronized Player getPlayer() throws RemoteException {
			return listener.getPlayer();
		}

		@Override
		public synchronized void chatMessage(Player sender, String message) throws RemoteException {
			listener.chatMessage(sender, message);
		}

		@Override
		public synchronized void provideInitialAssignment(Game game) throws RemoteException {
			listener.provideInitialAssignment(game);
		}

		@Override
		public synchronized void provideInitialChoice() throws RemoteException {
			listener.provideInitialChoice();
		}

		@Override
		public synchronized void startGame() throws RemoteException {
			listener.startGame();
		}

		@Override
		public synchronized void provideNextMove() throws RemoteException {
			listener.provideNextMove();
		}

		@Override
		public synchronized void figureMoved() throws RemoteException {
			listener.figureMoved();
		}

		@Override
		public synchronized void figureAttacked() throws RemoteException {
			listener.figureAttacked();
		}

		@Override
		public synchronized void provideChoiceAfterFightIsDrawn() throws RemoteException {
			listener.provideChoiceAfterFightIsDrawn();
		}

		@Override
		public synchronized void gameIsLost() throws RemoteException {
			listener.gameIsLost();
		}

		@Override
		public synchronized void gameIsWon() throws RemoteException {
			listener.gameIsWon();
		}

		@Override
		public synchronized void gameIsDrawn() throws RemoteException {
			listener.gameIsDrawn();
		}
	}
}