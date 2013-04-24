package rps.game.data;

import java.io.Serializable;
import static rps.game.data.FigureKind.HIDDEN;

public class Figure implements Serializable {

	private static final long serialVersionUID = 1L;

	private FigureKind kind;
	private Player owner;
	private boolean isDiscovered = false;

	public Figure(FigureKind kind, Player owner) {
		this.kind = kind;
		this.owner = owner;
	}

	public FigureKind getKind() {
		return kind;
	}

	public void setDiscovered() {
		isDiscovered = true;
	}

	public boolean isDiscovered() {
		return isDiscovered;
	}

	/**
	 * shortcut for comparing two figure kinds
	 */
	public AttackResult attack(Figure f) {
		return getKind().attack(f.getKind());
	}

	public boolean belongsTo(Player p) {
		return owner != null && owner.equals(p);
	}

	public Figure cloneWithHiddenKind() {
		Figure clone = new Figure(HIDDEN, owner);
		clone.isDiscovered = isDiscovered;
		return clone;
	}

	@Override
	public Figure clone() {
		Figure clone = new Figure(kind, owner);
		clone.isDiscovered = isDiscovered;
		return clone;
	}

	@Override
	public String toString() {
		return "Figure[" + owner + "," + kind + "," + (isDiscovered ? "!" : "?") + "]";
	}
}