package rps.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rps.game.GameImplFixture.getAnotherValidInitialAssignmentForFirstPlayer;
import static rps.game.GameImplFixture.getEliminationAssignmentForFirstPlayer;
import static rps.game.GameImplFixture.getEliminationAssignmentForSecondPlayer;
import static rps.game.GameImplFixture.getInitialAssignmentForSecondPlayerToEliminateStupidAssignment;
import static rps.game.GameImplFixture.getNotSoStupidInitialAssignmentForFirstPlayer;
import static rps.game.GameImplFixture.getStartingFieldAfterValidAssignmentsForFirstAndSecondPlayer;
import static rps.game.GameImplFixture.getStupidInitialAssignmentForFirstPlayer;
import static rps.game.GameImplFixture.getValidInitialAssignmentForFirstPlayer;
import static rps.game.GameImplFixture.getValidInitialAssignmentForSecondPlayer;
import static rps.game.data.FigureKind.FLAG;
import static rps.game.data.FigureKind.PAPER;
import static rps.game.data.FigureKind.ROCK;
import static rps.game.data.FigureKind.SCISSORS;

import java.rmi.RemoteException;

import org.junit.Before;
import org.junit.Test;

import rps.client.GameListener;
import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

public class GameImplTest {

	private Player p1;
	private Player p2;

	private GameListener listener1;
	private GameListener listener2;

	private GameImpl sut;

	@Before
	public void setup() throws RemoteException {
		p1 = new Player("player1");
		p2 = new Player("player2");
		listener1 = mock(GameListener.class);
		listener2 = mock(GameListener.class);
		resetListeners();
		sut = new GameImpl(listener1, listener2);
	}

	private void resetListeners() throws RemoteException {
		reset(listener1, listener2);
		when(listener1.getPlayer()).thenReturn(p1);
		when(listener2.getPlayer()).thenReturn(p2);
	}

	@Test
	public void resettingDoesNotBreakPlayerReturns() throws RemoteException {
		assertNotNull(listener1.getPlayer());
		resetListeners();
		assertNotNull(listener1.getPlayer());
	}

	@Test
	public void gameIsStartedOnceBothPlayersSubmittedAnInitialAssignment() throws RemoteException {
		sut.setInitialAssignment(p1, getValidInitialAssignmentForFirstPlayer());
		sut.setInitialAssignment(p2, getValidInitialAssignmentForSecondPlayer());

		verify(listener1).provideInitialChoice();
		verify(listener2).provideInitialChoice();
	}

	@Test(expected = IllegalStateException.class)
	public void itIsNotPossibleToSubmitMultipleInitialAssignments() throws RemoteException {
		sut.setInitialAssignment(p1, getValidInitialAssignmentForFirstPlayer());
		sut.setInitialAssignment(p1, getAnotherValidInitialAssignmentForFirstPlayer());
	}

	@Test
	public void winningPlayerStartsGame() throws RemoteException {
		sut.setInitialAssignment(p1, getValidInitialAssignmentForFirstPlayer());
		sut.setInitialAssignment(p2, getValidInitialAssignmentForSecondPlayer());

		sut.setInitialChoice(p1, PAPER);
		sut.setInitialChoice(p2, ROCK);

		verify(listener1).startGame();
		verify(listener2).startGame();
		verify(listener1).provideNextMove();
	}

	@Test
	public void winningPlayer2StartsGame() throws RemoteException {
		sut.setInitialAssignment(p1, getValidInitialAssignmentForFirstPlayer());
		sut.setInitialAssignment(p2, getValidInitialAssignmentForSecondPlayer());

		sut.setInitialChoice(p1, ROCK);
		sut.setInitialChoice(p2, PAPER);

		verify(listener1).startGame();
		verify(listener2).startGame();
		verify(listener2).provideNextMove();

		assertGameField(getStartingFieldAfterValidAssignmentsForFirstAndSecondPlayer());
	}

	@Test
	public void playersNeedToChooseAgainAfterInitialDraw() throws RemoteException {
		sut.setInitialAssignment(p1, getValidInitialAssignmentForFirstPlayer());
		sut.setInitialAssignment(p2, getValidInitialAssignmentForSecondPlayer());

		resetListeners();

		sut.setInitialChoice(p1, ROCK);
		sut.setInitialChoice(p2, ROCK);

		verify(listener1).provideInitialChoice();
		verify(listener2).provideInitialChoice();
	}

	@Test
	public void defaultMoveCausesNecessaryEvents() throws RemoteException {
		startGameWherePlayer1Begins();
		resetListeners();

		sut.move(p1, 34, 27);

		verify(listener1).figureMoved();
		verify(listener2).figureMoved();
		verify(listener2).provideNextMove();

		assertEmptyField(34);
		assertField(27, SCISSORS, p1);
	}

	@Test(expected = IllegalStateException.class)
	public void playerCannotMoveTwoTimes() throws RemoteException {
		startGameWherePlayer1Begins();
		sut.move(p1, 33, 26);
		sut.move(p1, 32, 25);
	}

	@Test
	public void drawBattleCausesRequestForNewKind() throws RemoteException {

		startGameWherePlayer1Begins();
		sut.move(p1, 34, 27);
		sut.move(p2, 13, 20);
		resetListeners();

		sut.move(p1, 27, 20);

		verify(listener1).provideChoiceAfterFightIsDrawn();
		verify(listener2).provideChoiceAfterFightIsDrawn();

		assertField(27, SCISSORS, p1);
	}

	@Test
	public void rechoosingSameTypesAfterDrawCausesDraw() throws RemoteException {
		startGameWherePlayer1Begins();
		sut.move(p1, 34, 27);
		sut.move(p2, 13, 20);
		sut.move(p1, 27, 20);
		resetListeners();

		sut.setUpdatedKindAfterDraw(p1, ROCK);
		sut.setUpdatedKindAfterDraw(p2, ROCK);

		verify(listener1).provideChoiceAfterFightIsDrawn();
		verify(listener2).provideChoiceAfterFightIsDrawn();

		assertEmptyFields(13, 34);
		assertField(20, ROCK, p2);
		assertField(27, ROCK, p1);
	}

	@Test
	public void choosingDifferentTypesAfterDrawCausesSuccessfullDefence() throws RemoteException {
		startGameWherePlayer1Begins();
		sut.move(p1, 34, 27);
		sut.move(p2, 13, 20);
		sut.move(p1, 27, 20);
		resetListeners();

		sut.setUpdatedKindAfterDraw(p1, FigureKind.SCISSORS);
		sut.setUpdatedKindAfterDraw(p2, FigureKind.ROCK);

		verify(listener1).figureAttacked();
		verify(listener2).figureAttacked();
		verify(listener2).provideNextMove();

		assertEmptyFields(13, 27, 34);
		assertField(20, ROCK, p2);
	}

	@Test
	public void choosingDifferentTypesAfterDrawCausesSuccessfullAttack() throws RemoteException {
		startGameWherePlayer1Begins();
		sut.move(p1, 34, 27);
		sut.move(p2, 13, 20);
		sut.move(p1, 27, 20);
		resetListeners();

		sut.setUpdatedKindAfterDraw(p1, FigureKind.ROCK);
		sut.setUpdatedKindAfterDraw(p2, FigureKind.SCISSORS);

		verify(listener1).figureAttacked();
		verify(listener2).figureAttacked();
		verify(listener2).provideNextMove();

		assertEmptyFields(13, 27, 34);
		assertField(20, ROCK, p1);
	}

	@Test
	public void defaultAttackCausesNecessaryEvents() throws RemoteException {
		startGameWherePlayer1Begins();
		sut.move(p1, 31, 24);
		sut.move(p2, 10, 17);
		resetListeners();

		sut.move(p1, 24, 17);

		verify(listener1).figureAttacked();
		verify(listener2).figureAttacked();

		verify(listener2).provideNextMove();

		assertEmptyField(24);
		assertField(17, ROCK, p1);
	}

	@Test
	public void attackingFlagEndsTheGame() throws RemoteException {
		startGameWherePlayer1Begins();
		sut.move(p1, 28, 21);
		sut.move(p2, 13, 20);
		sut.move(p1, 21, 14);
		sut.move(p2, 12, 19);
		resetListeners();
		sut.move(p1, 14, 7);
		verify(listener1).gameIsWon();
		verify(listener2).gameIsLost();
	}

	@Test
	public void allFiguresAreUncoveredAfterTheGameHasEnded() throws RemoteException {
		playGameInWhichPlayer1Wins();
		assertEverythingIsUncovered();
	}

	private void assertEverythingIsUncovered() throws RemoteException {
		Figure[] field = sut.getField();
		for (Figure f : field) {
			if (f == null) {
				continue;
			} else {
				assertTrue(f.isDiscovered());
			}
		}
	}

	@Test
	public void attackingTrapDeletesTheAttackingFigure() throws RemoteException {
		startGameWherePlayer1Begins();
		sut.move(p1, 29, 22);
		sut.move(p2, 13, 20);
		sut.move(p1, 22, 15);
		sut.move(p2, 12, 19);
		resetListeners();

		sut.move(p1, 15, 8);

		assertEmptyFields(15, 8);
	}

	@Test
	public void messagesArePropagatedToTheListeners() throws RemoteException {
		sut.sendMessage(p1, "test");

		verify(listener1).chatMessage(eq(p1), eq("test"));
		verify(listener2).chatMessage(eq(p1), eq("test"));
	}

	@Test
	public void surrenderingLeadsToGameEnd() throws RemoteException {
		sut.surrender(p1);

		verify(listener1).gameIsLost();
		verify(listener2).gameIsWon();
	}

	@Test(expected = IllegalStateException.class)
	public void itDoesNotMakeSenseThatBothPlayersSurrenderAtOnce() throws RemoteException {
		sut.surrender(p1);
		sut.surrender(p2);
	}

	@Test
	public void playersCanMoveMultipleTimesIfTheOpponentHasNoMovableFiguresLeft() throws RemoteException {
		startGameAndPlayUntilPlayer1HasNoMovableFiguresLeft();

		sut.move(p2, 29, 22);
		sut.move(p2, 22, 15);
	}

	@Test
	public void flagIsNotRemovedAtTheEnd() throws RemoteException {
		playGameInWhichPlayer1Wins();
		assertField(7, FLAG, p2);
	}

	private void playGameInWhichPlayer1Wins() throws RemoteException {
		startGameWherePlayer1Begins();
		sut.move(p1, 28, 21);
		sut.move(p2, 13, 20);
		sut.move(p1, 21, 14);
		sut.move(p2, 12, 19);
		sut.move(p1, 14, 7);
	}

	@Test
	public void oldFieldIsCorrectlyRememberedInLastMoveForBothPlayers() throws RemoteException {
		startGameWherePlayer1Begins();

		Figure[] oldField = sut.getField();
		String expected = compact(oldField);

		sut.move(p1, 28, 21);
		Move move1 = sut.getLastMove();
		Move move2 = sut.getLastMove();

		String actual1 = compact(move1.getOldField());
		String actual2 = compact(move2.getOldField());

		assertEquals(expected, actual1);
		assertEquals(expected, actual2);

		oldField = sut.getField();
		expected = compact(oldField);

		sut.move(p2, 9, 16);
		move1 = sut.getLastMove();
		move2 = sut.getLastMove();

		actual1 = compact(move1.getOldField());
		actual2 = compact(move2.getOldField());

		assertEquals(expected, actual1);
		assertEquals(expected, actual2);
	}

	@Test
	public void lastMoveIsUpdatedInCaseOfATie() throws RemoteException {
		startGameWherePlayer1Begins();

		sut.move(p1, 34, 27);
		sut.move(p2, 13, 20);
		sut.move(p1, 27, 20);

		Move lastMove = sut.getLastMove();

		assertEquals(27, lastMove.getFrom());
		assertEquals(20, lastMove.getTo());

		Figure[] field = sut.getField();
		Figure[] oldField = lastMove.getOldField();
		assertEquals(compact(field), compact(oldField));
	}

	@Test
	public void lastMoveIsUpdatedAfterKindWasRechosen() throws RemoteException {
		startGameWherePlayer1Begins();

		sut.move(p1, 34, 27);
		sut.move(p2, 13, 20);
		sut.move(p1, 27, 20);

		sut.setUpdatedKindAfterDraw(p1, PAPER);
		sut.setUpdatedKindAfterDraw(p2, ROCK);

		Figure[] field = sut.getLastMove().getOldField();

		assertEquals(PAPER, field[27].getKind());
		assertEquals(ROCK, field[20].getKind());
	}

	@Test
	public void attackedFigureIsUncoveredForLastMove() throws RemoteException {
		startGameWherePlayer1Begins();
		sut.move(p1, 31, 24);
		sut.move(p2, 10, 17);

		Figure[] oldField = sut.getField();
		assertFalse(oldField[17].isDiscovered());
		assertFalse(oldField[24].isDiscovered());

		sut.move(p1, 24, 17);

		Move move = sut.getLastMove();
		assertTrue(move.getOldField()[17].isDiscovered());
		assertTrue(move.getOldField()[24].isDiscovered());
	}

	@Test
	public void gameEndsInADrawIfNoMovableFiguresAreLeft() throws RemoteException {
		sut.setInitialAssignment(p1, getEliminationAssignmentForFirstPlayer());
		sut.setInitialAssignment(p2, getEliminationAssignmentForSecondPlayer());

		sut.setInitialChoice(p1, PAPER); // p1 starts
		sut.setInitialChoice(p2, ROCK);

		eliminateColumnOfEliminationAssignment(0);
		eliminateColumnOfEliminationAssignment(1);
		eliminateColumnOfEliminationAssignment(2);
		eliminateColumnOfEliminationAssignment(3);
		eliminateColumnOfEliminationAssignment(4);
		eliminateColumnOfEliminationAssignment(5);

		// kill the existing ones
		sut.move(p1, 20, 19);
		sut.move(p2, 19, 18);
		sut.move(p1, 18, 17);
		sut.move(p2, 17, 16);
		sut.move(p1, 16, 15);
		sut.move(p2, 15, 14);

		// / move the last survivor onto the trap
		sut.move(p2, 14, 21);
		sut.move(p2, 21, 28);

		verify(listener1).gameIsDrawn();
		verify(listener2).gameIsDrawn();
	}

	private void eliminateColumnOfEliminationAssignment(int i) throws RemoteException {
		sut.move(p1, 29 + i, 22 + i);
		sut.move(p2, 8 + i, 15 + i);
		sut.move(p1, 22 + i, 15 + i);
		sut.move(p2, 15 + i, 22 + i);
		sut.move(p1, 36 + i, 29 + i);
		sut.move(p2, 22 + i, 29 + i);
		sut.move(p1, 29 + i, 22 + i);
		sut.move(p2, 1 + i, 8 + i);
		sut.move(p1, 22 + i, 15 + i);
		sut.move(p2, 8 + i, 15 + i);
	}

	@Test
	public void placingFiguresInAStupidWayMayLeadToImmovableFigures() throws RemoteException {
		playGameWithStupidAssignmentUntilFirstPlayerHasNoMovableFiguresLeft();

		// second player should be able to move multiple times now
		sut.move(p2, 15, 22);
		sut.move(p2, 22, 23);
		sut.move(p2, 23, 24);
	}

	private void playGameWithStupidAssignmentUntilFirstPlayerHasNoMovableFiguresLeft() throws RemoteException {
		sut.setInitialAssignment(p1, getStupidInitialAssignmentForFirstPlayer());
		sut.setInitialAssignment(p2, getInitialAssignmentForSecondPlayerToEliminateStupidAssignment());

		sut.setInitialChoice(p1, PAPER); // p1 starts
		sut.setInitialChoice(p2, ROCK);

		clearColumnInWhichPlayerTwoAlwaysWins(0);
		clearColumnInWhichPlayerTwoAlwaysWins(-1);
		clearColumnInWhichPlayerTwoAlwaysWins(-2);
		clearColumnInWhichPlayerTwoAlwaysWins(-3);
		clearColumnInWhichPlayerTwoAlwaysWins(-4);

		sut.move(p1, 29, 22);
		sut.move(p2, 8, 15);
		sut.move(p1, 22, 15);
	}

	private void clearColumnInWhichPlayerTwoAlwaysWins(int offset) throws RemoteException {
		sut.move(p1, 34 + offset, 27 + offset);
		sut.move(p2, 13 + offset, 20 + offset);
		sut.move(p1, 27 + offset, 20 + offset);
		sut.move(p2, 20 + offset, 27 + offset);
		sut.move(p1, 41 + offset, 34 + offset);
		sut.move(p2, 27 + offset, 34 + offset);
	}

	private void startGameWherePlayer1Begins() throws RemoteException {
		sut.setInitialAssignment(p1, getValidInitialAssignmentForFirstPlayer());
		sut.setInitialAssignment(p2, getValidInitialAssignmentForSecondPlayer());

		sut.setInitialChoice(p1, PAPER);
		sut.setInitialChoice(p2, ROCK);
	}

	private void startGameAndPlayUntilPlayer1HasNoMovableFiguresLeft() throws RemoteException {
		sut.setInitialAssignment(p1, getNotSoStupidInitialAssignmentForFirstPlayer());
		sut.setInitialAssignment(p2, getInitialAssignmentForSecondPlayerToEliminateStupidAssignment());

		sut.setInitialChoice(p1, PAPER); // p1 starts
		sut.setInitialChoice(p2, ROCK);

		clearColumnInWhichPlayerTwoAlwaysWins(0);
		clearColumnInWhichPlayerTwoAlwaysWins(-1);
		clearColumnInWhichPlayerTwoAlwaysWins(-2);
		clearColumnInWhichPlayerTwoAlwaysWins(-3);
		clearColumnInWhichPlayerTwoAlwaysWins(-4);
		clearColumnInWhichPlayerTwoAlwaysWins(-5);
	}

	private void assertField(int i, FigureKind expectedKind, Player expectedPlayer) throws RemoteException {
		Figure[] field = sut.getField();
		assertTrue(field[i].belongsTo(expectedPlayer));

		FigureKind actualKind = field[i].getKind();
		assertEquals(expectedKind, actualKind);
	}

	private void assertEmptyField(int i) throws RemoteException {
		Figure[] field = sut.getField();
		assertNull(field[i]);
	}

	private void assertEmptyFields(int... fields) throws RemoteException {
		for (int i : fields) {
			assertEmptyField(i);
		}
	}

	private void assertGameField(FigureKind[] expecteds) throws RemoteException {
		Figure[] actuals = sut.getField();

		for (int i = 0; i < expecteds.length; i++) {

			Figure actual = actuals[i];

			if (actual == null) {
				assertNull(expecteds[i]);
			} else {
				assertEquals(expecteds[i], actual.getKind());
			}
		}
	}

	public String compact(Figure[] field) {
		StringBuffer sb = new StringBuffer();

		int i = 0;

		for (Figure fig : field) {
			if (fig == null) {
				sb.append('.');
			} else {
				String kind = fig.getKind().toString().substring(0, 1);
				if (fig.belongsTo(p1)) {
					sb.append(kind.toLowerCase());
				} else {
					sb.append(kind.toUpperCase());
				}
			}

			i++;
			if (i % 7 == 0) {
				sb.append("\n");
			}
		}

		return sb.toString();
	}
}