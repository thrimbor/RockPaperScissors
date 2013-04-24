package rps.game.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static rps.game.data.AttackResult.DRAW;
import static rps.game.data.AttackResult.LOOSE;
import static rps.game.data.AttackResult.LOOSE_AGAINST_TRAP;
import static rps.game.data.AttackResult.WIN;
import static rps.game.data.AttackResult.WIN_AGAINST_FLAG;
import static rps.game.data.FigureKind.FLAG;
import static rps.game.data.FigureKind.HIDDEN;
import static rps.game.data.FigureKind.PAPER;
import static rps.game.data.FigureKind.ROCK;
import static rps.game.data.FigureKind.SCISSORS;
import static rps.game.data.FigureKind.TRAP;

import org.junit.Before;
import org.junit.Test;

public class FigureTest {

	private Player thePlayer;
	private Player otherPlayer;

	@Before
	public void setup() {
		thePlayer = mock(Player.class);
		otherPlayer = mock(Player.class);
	}

	@Test
	public void belongsToReturnsExpectedValues() {
		Figure sut = new Figure(ROCK, thePlayer);
		assertTrue(sut.belongsTo(thePlayer));
		assertFalse(sut.belongsTo(otherPlayer));
	}

	@Test
	public void figureHasCorrectKind() {
		Figure sut = new Figure(ROCK, thePlayer);
		assertEquals(ROCK, sut.getKind());
		sut = new Figure(PAPER, thePlayer);
		assertEquals(PAPER, sut.getKind());
	}

	@Test
	public void figuresAreInitiallyUndiscovered() {
		Figure sut = new Figure(ROCK, thePlayer);
		assertFalse(sut.isDiscovered());
	}

	@Test
	public void figuresCanBeDiscovered() {
		Figure sut = new Figure(ROCK, thePlayer);
		sut.setDiscovered();
		assertTrue(sut.isDiscovered());
	}

	@Test
	public void cloneWorks() {
		Figure sut = new Figure(ROCK, thePlayer);
		sut.setDiscovered();
		Figure clone = sut.clone();

		assertEquals(sut.getKind(), clone.getKind());
		assertTrue(clone.belongsTo(thePlayer));
		assertEquals(sut.isDiscovered(), clone.isDiscovered());
		assertNotSame(sut, clone);
	}

	@Test
	public void cloneHiddenWorks() {
		Figure sut = new Figure(ROCK, thePlayer);
		sut.setDiscovered();
		Figure clone = sut.cloneWithHiddenKind();

		assertEquals(HIDDEN, clone.getKind());
		assertTrue(clone.belongsTo(thePlayer));
		assertEquals(sut.isDiscovered(), clone.isDiscovered());
		assertNotSame(sut, clone);
	}

	@Test
	public void compareReturnsTheExpectedValue() {
		assertEquals(attack(ROCK, SCISSORS), WIN);
		assertEquals(attack(PAPER, SCISSORS), LOOSE);
		assertEquals(attack(PAPER, PAPER), DRAW);
		assertEquals(attack(PAPER, FLAG), WIN_AGAINST_FLAG);
		assertEquals(attack(PAPER, TRAP), LOOSE_AGAINST_TRAP);
	}

	private Object attack(FigureKind k1, FigureKind k2) {
		Player p1 = new Player("p1");
		Player p2 = new Player("p2");
		Figure f1 = new Figure(k1, p1);
		Figure f2 = new Figure(k2, p2);
		return f1.attack(f2);
	}
}