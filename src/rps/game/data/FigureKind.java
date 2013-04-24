package rps.game.data;

public enum FigureKind {
	ROCK, PAPER, SCISSORS, FLAG, TRAP, HIDDEN;

	private FigureKind weakness;

	static {
		ROCK.setWeakness(PAPER);
		PAPER.setWeakness(SCISSORS);
		SCISSORS.setWeakness(ROCK);
	}

	private void setWeakness(FigureKind weakness) {
		this.weakness = weakness;
	}

	public AttackResult attack(FigureKind kind) {
		if (equals(kind)) {
			return AttackResult.DRAW;
		} else if (kind == weakness) {
			return AttackResult.LOOSE;
		} else if (kind == FigureKind.FLAG) {
			return AttackResult.WIN_AGAINST_FLAG;
		} else if (kind == FigureKind.TRAP) {
			return AttackResult.LOOSE_AGAINST_TRAP;
		} else {
			return AttackResult.WIN;
		}
	}

	public boolean isMovable() {
		return ordinal() <= 2;
	}
}