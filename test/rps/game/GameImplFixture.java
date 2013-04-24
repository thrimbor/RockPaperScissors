package rps.game;

import static rps.game.data.FigureKind.FLAG;
import static rps.game.data.FigureKind.PAPER;
import static rps.game.data.FigureKind.ROCK;
import static rps.game.data.FigureKind.SCISSORS;
import static rps.game.data.FigureKind.TRAP;
import rps.game.data.FigureKind;

public class GameImplFixture {
	public static FigureKind[] getValidInitialAssignmentForFirstPlayer() {
		return new FigureKind[] {
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				PAPER, PAPER, ROCK, ROCK, ROCK, ROCK, SCISSORS,
				SCISSORS, SCISSORS, SCISSORS, FLAG, TRAP, PAPER, PAPER,
		};
	}

	public static FigureKind[] getAnotherValidInitialAssignmentForFirstPlayer() {
		return new FigureKind[] {
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				PAPER, PAPER, PAPER, PAPER, ROCK, ROCK, ROCK,
				ROCK, SCISSORS, SCISSORS, SCISSORS, SCISSORS, FLAG, TRAP,
		};
	}

	public static FigureKind[] getInvalidInitialAssignmentForFirstPlayer() {
		return new FigureKind[] {
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				ROCK, PAPER, ROCK, ROCK, ROCK, ROCK, SCISSORS,
				SCISSORS, SCISSORS, SCISSORS, FLAG, TRAP, PAPER, PAPER,
		};
	}

	public static FigureKind[] getStupidInitialAssignmentForFirstPlayer() {
		return new FigureKind[] {
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				FLAG, ROCK, ROCK, SCISSORS, SCISSORS, PAPER, PAPER,
				ROCK, TRAP, ROCK, SCISSORS, SCISSORS, PAPER, PAPER,
		};
	}

	public static FigureKind[] getNotSoStupidInitialAssignmentForFirstPlayer() {
		return new FigureKind[] {
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				TRAP, ROCK, ROCK, SCISSORS, SCISSORS, PAPER, PAPER,
				FLAG, ROCK, ROCK, SCISSORS, SCISSORS, PAPER, PAPER,
		};
	}

	public static FigureKind[] getInitialAssignmentForSecondPlayerToEliminateStupidAssignment() {
		return new FigureKind[] {
				FLAG, PAPER, PAPER, ROCK, ROCK, SCISSORS, SCISSORS,
				TRAP, PAPER, PAPER, ROCK, ROCK, SCISSORS, SCISSORS,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
		};
	}

	public static FigureKind[] getEliminationAssignmentForFirstPlayer() {
		return new FigureKind[] {
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				TRAP, ROCK,ROCK, PAPER,PAPER, SCISSORS, SCISSORS,
				FLAG, SCISSORS, SCISSORS, ROCK, ROCK, PAPER, PAPER,
		};
	}

	public static FigureKind[] getEliminationAssignmentForSecondPlayer() {
		return new FigureKind[] {
				TRAP, ROCK, PAPER, PAPER, SCISSORS, SCISSORS, ROCK,
				FLAG, PAPER, PAPER, SCISSORS, SCISSORS, ROCK, ROCK,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
		};
	}

	public static FigureKind[] getValidInitialAssignmentForSecondPlayer() {
		return new FigureKind[] {
				PAPER, PAPER, PAPER, PAPER, ROCK, ROCK, ROCK,
				FLAG, TRAP, ROCK, SCISSORS, SCISSORS, SCISSORS, SCISSORS,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
		};
	}

	public static FigureKind[] getStartingFieldAfterValidAssignmentsForFirstAndSecondPlayer() {
		return new FigureKind[] {
				PAPER, PAPER, PAPER, PAPER, ROCK, ROCK, ROCK,
				FLAG, TRAP, ROCK, SCISSORS, SCISSORS, SCISSORS, SCISSORS,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				PAPER, PAPER, ROCK, ROCK, ROCK, ROCK, SCISSORS,
				SCISSORS, SCISSORS, SCISSORS, FLAG, TRAP, PAPER, PAPER,
		};
	}
}