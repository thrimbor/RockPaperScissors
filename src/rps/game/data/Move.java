package rps.game.data;

import java.io.Serializable;

public class Move implements Serializable {

	private static final long serialVersionUID = -4989264796563153034L;

	private int fromIndex;
	private int toIndex;

	private Figure[] oldField;

	public Move(int fromIndex, int toIndex, Figure[] oldField) {
		this.fromIndex = fromIndex;
		this.toIndex = toIndex;
		this.oldField = oldField;
	}

	public int getFrom() {
		return fromIndex;
	}

	public int getTo() {
		return toIndex;
	}

	public Figure[] getOldField() {
		return oldField;
	}

	@Override
	public String toString() {
		return String.format("Move (%d -> %d)", fromIndex, toIndex);
	}
}