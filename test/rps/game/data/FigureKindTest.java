package rps.game.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FigureKindTest {

	@Test
	public void isMovableMarksCorrectFiguresAsMovable() {
		assertTrue(FigureKind.ROCK.isMovable());
		assertTrue(FigureKind.SCISSORS.isMovable());
		assertTrue(FigureKind.PAPER.isMovable());
		assertFalse(FigureKind.HIDDEN.isMovable());
		assertFalse(FigureKind.FLAG.isMovable());
		assertFalse(FigureKind.TRAP.isMovable());
	}
}