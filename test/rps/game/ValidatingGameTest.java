package rps.game;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rps.game.GameImplFixture.getValidInitialAssignmentForFirstPlayer;
import static rps.game.ValidatingGameFixture.getInitialAssignmentWithFiguresPlacedOnLowAddresses;
import static rps.game.ValidatingGameFixture.getInitialAssignmentWithInvalidKinds;
import static rps.game.ValidatingGameFixture.getInitialAssignmentWithInvalidNumber;
import static rps.game.data.FigureKind.FLAG;
import static rps.game.data.FigureKind.HIDDEN;
import static rps.game.data.FigureKind.TRAP;

import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Player;

public class ValidatingGameTest {

	private Game game;
	private Player thePlayer;
	private Player otherPlayer;

	private Figure[] fields;

	private ValidatingGame sut;

	@Before
	public void setup() throws RemoteException {

		game = mock(Game.class);

		fields = new Figure[42];
		when(game.getField()).thenReturn(fields);

		thePlayer = new Player("A");
		otherPlayer = new Player("B");

		sut = new ValidatingGame(game, thePlayer);
	}

	@Test
	public void moveIsPropagatedToGame() throws RemoteException {
		fields[1] = createFigureForPlayer(thePlayer);
		sut.move(thePlayer, 1, 2);
		verify(game).move(thePlayer, 1, 2);
	}

	@Test
	public void getFieldIsPropagatedToGame() throws RemoteException {
		sut.getField();
		verify(game).getField();
	}

	@Test
	public void initialAssignmentsArePropagatedToGame() throws RemoteException {
		sut.setInitialAssignment(thePlayer, getValidInitialAssignmentForFirstPlayer());
		verify(game).setInitialAssignment(thePlayer, getValidInitialAssignmentForFirstPlayer());
	}

	@Test(expected = IllegalArgumentException.class)
	public void assignmentsWithInvalidKindsCausesAnException() throws RemoteException {
		sut.setInitialAssignment(thePlayer, getInitialAssignmentWithInvalidKinds());
	}

	@Test(expected = IllegalArgumentException.class)
	public void assignmentsWithInvalidNumbersCausesAnException() throws RemoteException {
		sut.setInitialAssignment(thePlayer, getInitialAssignmentWithInvalidNumber());
	}

	@Test(expected = IllegalArgumentException.class)
	public void assignmentsWithInvalidPlacedFiguresCausesAnException() throws RemoteException {
		sut.setInitialAssignment(thePlayer, getInitialAssignmentWithFiguresPlacedOnLowAddresses());
	}

	@Test(expected = IllegalArgumentException.class)
	public void movesFromDifferentPlayerCauseAnException() throws RemoteException {
		sut.move(otherPlayer, 1, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void initialAssignmentsFromDifferentPlayerCauseAnException() throws RemoteException {
		sut.setInitialAssignment(otherPlayer, getValidInitialAssignmentForFirstPlayer());
	}

	@Test(expected = IllegalArgumentException.class)
	public void flagCannotBeMoved() throws RemoteException {
		fields[1] = new Figure(FLAG, thePlayer);
		sut.move(thePlayer, 1, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void trapCannotBeMoved() throws RemoteException {
		fields[1] = new Figure(TRAP, thePlayer);
		sut.move(thePlayer, 1, 2);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noMoveToNegativePositions() throws RemoteException {
		sut.move(thePlayer, 1, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noMoveAcrossFieldBorders() throws RemoteException {
		fields[6] = createFigureForPlayer(thePlayer);
		sut.move(thePlayer, 6, 7);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noMoveAcrossFieldBorders2() throws RemoteException {
		fields[7] = createFigureForPlayer(thePlayer);
		sut.move(thePlayer, 7, 20);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noMoveOfTheOpponentFigures() throws RemoteException {
		fields[3] = createFigureForPlayer(otherPlayer);
		sut.move(thePlayer, 3, 4);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noTrapIsAllowedForTheInitialChoice() throws RemoteException {
		sut.setInitialChoice(thePlayer, TRAP);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noHiddenTypeIsAllowedForTheInitialChoice() throws RemoteException {
		sut.setInitialChoice(thePlayer, HIDDEN);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noFlagIsAllowedForTheInitialChoice() throws RemoteException {
		sut.setInitialChoice(thePlayer, FLAG);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noTrapsAreAllowedForRechoosingTypeOnDraw() throws RemoteException {
		sut.setUpdatedKindAfterDraw(thePlayer, TRAP);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noHiddenTypesAreAllowedForRechoosingTypeOnDraw() throws RemoteException {
		sut.setUpdatedKindAfterDraw(thePlayer, HIDDEN);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noFlagsAreAllowedForRechoosingTypeOnDraw() throws RemoteException {
		sut.setUpdatedKindAfterDraw(thePlayer, FLAG);
	}

	@Test(expected = IllegalArgumentException.class)
	public void movingToTheSameCoordinateIsAnInvalidMove() throws RemoteException {
		sut.move(thePlayer, 1, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void playersCannotAttackTheirOwnFigures() throws RemoteException {
		fields[1] = createFigureForPlayer(thePlayer);
		fields[2] = createFigureForPlayer(thePlayer);
		sut.move(thePlayer, 1, 2);
	}

	private static Figure createFigureForPlayer(Player p) {
		return new Figure(FigureKind.PAPER, p);
	}
}