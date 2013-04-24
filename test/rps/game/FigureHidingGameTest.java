package rps.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
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

public class FigureHidingGameTest {
	private Game game;

	private Player thePlayer;
	private Player theEnemy;
	
	private FigureHidingGame sut;
	private Figure[] fields;
	
	@Before
	public void setup() throws RemoteException {
		thePlayer = mock(Player.class);
		theEnemy = mock(Player.class);

		fields = new Figure[42];
		for (int i = 0; i < 42; i++) {
			fields[i] = new Figure((i<21) ? FigureKind.PAPER : FigureKind.SCISSORS, (i<21) ? thePlayer : theEnemy);
		}

		game = mock(Game.class);

		sut = new FigureHidingGame(game, thePlayer);
	}
	

	@Test
	public void enemyFieldsAreHidden() throws RemoteException {
		// Testet ob alle Figuren des Gegners HIDDEN sind und alle eigenen nicht
		when(game.getField()).thenReturn(fields);
		
		Figure[] hiddenField = sut.getField();
		
		for (int i = 0; i <= 41; i++) {
			if (i<21) {
				assertEquals(hiddenField[i].getKind(), FigureKind.PAPER);
			}
			else {
				assertEquals(hiddenField[i].getKind(), FigureKind.HIDDEN);
			}
		}
	}
	

	@Test
	public void figuresOnOldFieldOfMoveGetHidden() throws RemoteException {
		when(game.getLastMove()).thenReturn(new Move(14, 21, fields));
		
		Figure[] hiddenField = sut.getLastMove().getOldField();
		
		for (int i = 0; i <= 41; i++) {
			if (i<21) {
				assertEquals(hiddenField[i].getKind(), FigureKind.PAPER);
			}
			else {
				assertEquals(hiddenField[i].getKind(), FigureKind.HIDDEN);
			}
		}
	}

	@Test
	public void figureGetsDiscoveredAfterAttack() throws RemoteException {
		when(game.getField()).thenReturn(fields);
		
		// Simulate attack
		fields[14] = fields[21];
		fields[14].setDiscovered();
		fields[21] = null;
		
		Figure[] hiddenField = sut.getField();
		assertEquals(hiddenField[14].getKind(), FigureKind.SCISSORS);
	}
	
	@Test
	public void figureGetsDiscoveredAfterDraw() throws RemoteException {
		when(game.getField()).thenReturn(fields);
		
		//Simulate attack
		fields[14].setDiscovered();
		fields[21].setDiscovered();
		
		Figure[] hiddenField = sut.getField();
		assertEquals(hiddenField[14].getKind(), FigureKind.PAPER);
		assertEquals(hiddenField[21].getKind(), FigureKind.SCISSORS);
	}
	
	@Test
	public void otherMethodsPassThroughUntouched() throws RemoteException {
		sut.sendMessage(thePlayer, "test");
		verify(game).sendMessage(thePlayer, "test");
		
		sut.getOpponent(theEnemy);
		verify(game).getOpponent(theEnemy);

		sut.move(thePlayer, 1, 2);
		verify(game).move(thePlayer, 1, 2);
		
		FigureKind[] assign = new FigureKind[42];
		sut.setInitialAssignment(thePlayer, assign);
		verify(game).setInitialAssignment(thePlayer, assign);
		
		sut.setInitialChoice(thePlayer, FigureKind.FLAG);
		verify(game).setInitialChoice(thePlayer, FigureKind.FLAG);

		sut.setUpdatedKindAfterDraw(thePlayer, FigureKind.FLAG);
		verify(game).setUpdatedKindAfterDraw(thePlayer, FigureKind.FLAG);
		
		sut.surrender(thePlayer);
		verify(game).surrender(thePlayer);
	}

	@Test
	public void originalFieldStaysUntouched() throws RemoteException {
		Figure[] origField = fields.clone();
		
		when(game.getField()).thenReturn(fields);
		
		sut.getField();
		
		assertArrayEquals(origField, game.getField());
	}
	
	@Test
	public void originalMoveStaysUntouched() throws RemoteException {
		Figure[] origField = fields.clone();
		
		when(game.getField()).thenReturn(fields);
		when(game.getLastMove()).thenReturn(new Move(14, 21, fields));
		
		sut.getLastMove().getOldField();
		
		assertArrayEquals(origField, game.getField());
	}
}