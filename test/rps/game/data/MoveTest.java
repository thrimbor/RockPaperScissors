package rps.game.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

public class MoveTest {
	@Test
	public void allValuesAreStored() {
		Figure[] oldField = new Figure[] {};
		Move sut = new Move(123, 456, oldField);

		assertEquals(123, sut.getFrom());
		assertEquals(456, sut.getTo());
		assertSame(oldField, sut.getOldField());
	}
}