package rps.client.ai;

import static java.util.regex.Pattern.matches;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TournamentAiNamingTest {
	@Test
	public void nameOfTournamentAiContainsGroupNumber() {
		String nick = new TournamentAi(1, 2).getPlayer().getNick();
		assertTrue(hasGroup(nick));
	}

	@Test
	public void smallGroupNumber() {
		assertTrue(hasGroup("Name (#1)"));
	}

	@Test
	public void biggerGroupNumber() {
		assertTrue(hasGroup("Name (#12)"));
	}

	@Test
	public void nameWithSpaces() {
		assertTrue(hasGroup("A Name (#12)"));
	}

	@Test
	public void wrongFormat() {
		assertFalse(hasGroup("Name - #12"));
	}

	@Test
	public void numberMustBeAtTheEnd() {
		assertFalse(hasGroup("Some (#123) Name"));
	}

	@Test
	public void noNumberGiven() {
		assertFalse(hasGroup("Just a Name"));
	}

	private static boolean hasGroup(String nick) {
		return matches(".*\\(#\\d+\\)$", nick);
	}
}