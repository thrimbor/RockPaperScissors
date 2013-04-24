package rps.game;

import static rps.game.data.FigureKind.FLAG;
import static rps.game.data.FigureKind.PAPER;
import static rps.game.data.FigureKind.ROCK;
import static rps.game.data.FigureKind.SCISSORS;
import static rps.game.data.FigureKind.TRAP;
import rps.game.data.FigureKind;

public class ValidatingGameFixture {
	public static FigureKind[] getInitialAssignmentWithInvalidKinds() {
		return new FigureKind[] { 
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				FigureKind.PAPER, FigureKind.PAPER, FigureKind.ROCK, FigureKind.PAPER, FigureKind.ROCK, FigureKind.ROCK, FigureKind.SCISSORS, 
				FigureKind.PAPER, FigureKind.PAPER, FigureKind.PAPER, FigureKind.PAPER, FigureKind.PAPER, FigureKind.PAPER, FigureKind.FLAG, 
		};
	}

	public static FigureKind[] getInitialAssignmentWithInvalidNumber() {
		return new FigureKind[] { 
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null, FigureKind.ROCK, 
				FigureKind.ROCK, FigureKind.SCISSORS, FigureKind.SCISSORS, FigureKind.SCISSORS, FigureKind.SCISSORS, FigureKind.FLAG, FigureKind.TRAP,
		};
	}
	
	public static FigureKind[] getInitialAssignmentWithFiguresPlacedOnLowAddresses() {
		return new FigureKind[] { 
				PAPER, PAPER, ROCK, ROCK, ROCK, ROCK, SCISSORS, 
				SCISSORS, SCISSORS, SCISSORS, FLAG, TRAP, PAPER, PAPER,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
				null, null, null, null, null, null, null,
		};
	}
}