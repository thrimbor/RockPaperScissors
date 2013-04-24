package rps.game.data;

import java.io.Serializable;

public class Player implements Serializable {

	private static final long serialVersionUID = 1118386104373691076L;

	private final String nick;

	public Player(String nick) {
		this.nick = nick;
	}

	public String getNick() {
		return nick;
	}

	@Override
	public int hashCode() {
		return nick.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Player other = (Player) obj;
		return nick.equals(other.nick);
	}

	@Override
	public String toString() {
		return "Player[" + nick + "]";
	}
}