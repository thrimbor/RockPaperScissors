package rps.network;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rps.game.GameImplFixture.getValidInitialAssignmentForFirstPlayer;

import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import rps.client.GameListener;
import rps.game.Game;
import rps.game.data.Player;

public class GameRegistryImplTest {

	private Player p1;
	private Player p2;

	private GameListener listener1;
	private GameListener listener2;

	private Game game1;
	private Game game2;

	private GameRegistryImpl sut;

	@Before
	public void setup() throws RemoteException {
		p1 = mock(Player.class);
		p2 = mock(Player.class);

		listener1 = mock(GameListener.class);
		when(listener1.getPlayer()).thenReturn(p1);


		listener2 = mock(GameListener.class);
		when(listener2.getPlayer()).thenReturn(p2);

		sut = new GameRegistryImpl();
	}

	@Test(expected = IllegalArgumentException.class)
	public void impossibleToRegisterTwice() throws RemoteException {
		sut.register(listener1);
		when(listener2.getPlayer()).thenReturn(p1);
		sut.register(listener2);
	}

	@Test
	public void initialAssignmentsAreRequestedFirst() throws RemoteException {
		sut.register(listener1);
		sut.register(listener2);

		verify(listener1).provideInitialAssignment(any(Game.class));
		verify(listener2).provideInitialAssignment(any(Game.class));
	}

	@Test(expected = IllegalArgumentException.class)
	public void playerCheckingIsInstalledForFirstPlayer() throws RemoteException {
		startTheGameAndCaptureGames();
		game1.setInitialAssignment(p2, getValidInitialAssignmentForFirstPlayer());
	}

	@Test(expected = IllegalArgumentException.class)
	public void playerCheckingIsInstalledForSecondPlayer() throws RemoteException {
		startTheGameAndCaptureGames();
		game2.setInitialAssignment(p1, getValidInitialAssignmentForFirstPlayer());
	}

	private void startTheGameAndCaptureGames() throws RemoteException {
		ArgumentCaptor<Game> game1Captor = ArgumentCaptor.forClass(Game.class);
		ArgumentCaptor<Game> game2Captor = ArgumentCaptor.forClass(Game.class);

		sut.register(listener1);
		sut.register(listener2);

		verify(listener1).provideInitialAssignment(game1Captor.capture());
		game1 = game1Captor.getValue();

		verify(listener2).provideInitialAssignment(game2Captor.capture());
		game2 = game2Captor.getValue();
	}
}