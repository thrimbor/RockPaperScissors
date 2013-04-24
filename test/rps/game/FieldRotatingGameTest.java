package rps.game;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

public class FieldRotatingGameTest {

	private Game game;
	private Player thePlayer;

	private Figure[] origFields;
	private FieldRotatingGame sut;

	@Before
	public void setup() throws RemoteException {
		thePlayer = mock(Player.class);

		origFields = new Figure[42];
		for (int i = 0; i < 42; i++) {
			origFields[i] = mock(Figure.class);
		}

		game = mock(Game.class);
		when(game.getField()).thenReturn(origFields);

		sut = new FieldRotatingGame(game);
	}

	@Test
	public void moveIsPropagatedToGame() throws RemoteException {
		sut.move(thePlayer, 1, 2);
		verify(game).move(thePlayer, 40, 39);
	}

	@Test
	public void getFieldIsPropagatedToGame() throws RemoteException {
		sut.getField();
		verify(game).getField();
	}

	@Test
	public void initialAssignmentsArePropagatedToGame() throws RemoteException {
		sut.setInitialAssignment(thePlayer, new FigureKind[14]);
		verify(game).setInitialAssignment(eq(thePlayer), any(FigureKind[].class));
	}

	@Test
	public void movePositionsAreInvertedCorrect() throws RemoteException {
		sut.move(thePlayer, 1, 2);
		verify(game).move(thePlayer, 40, 39);
	}

	@Test
	public void fieldIsInvertedCorrectly() throws RemoteException {
		Figure[] actualField = sut.getField();

		for (int i = 0; i <= 41; i++) {
			int rotated = 41 - i;
			assertEquals(actualField[i], origFields[rotated]);
		}
	}

	@Test
	public void lastMoveIsInvertedCorrectly() throws RemoteException {
		Move move = new Move(1, 2, origFields);
		when(game.getLastMove()).thenReturn(move);
		Move lastMove = sut.getLastMove();
		assertEquals(lastMove.getFrom(), 40);
		assertEquals(lastMove.getTo(), 39);
	}
}