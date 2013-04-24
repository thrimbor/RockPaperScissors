package rps.game.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class PlayerTest {

	@Test
	public void playersWithEqualNameAreConsideredEqual() {
		Player a = new Player("A");
		Player b = new Player("A");

		assertEquals(a, b);
		assertTrue(a.hashCode() == b.hashCode());
	}

	@Test
	public void playersWithDifferentNameAreConsideredDifferent() {
		Player a = new Player("A");
		Player b = new Player("B");

		assertFalse(a.equals(b));
		assertFalse(a.hashCode() == b.hashCode());
	}

	@Test
	public void nickIsSet() {
		Player p = new Player("asd");
		assertEquals("asd", p.getNick());
	}
}