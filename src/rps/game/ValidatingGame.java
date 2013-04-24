package rps.game;

import java.rmi.RemoteException;

import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

public class ValidatingGame implements Game {

	private final Game game;
	private Player player;

	public ValidatingGame(Game game, Player player) throws RemoteException {
		this.game = game;
		this.player = player;
	}

	@Override
	public void setInitialAssignment(Player p, FigureKind[] assignment) throws RemoteException {
		// Erst einmal nachsehen, ob überhaupt die richtige Anzahl an Figuren vorliegt
		if ((ValidatingGame.getAmountOfFigureKindOnAssignment(assignment, FigureKind.FLAG) != 1) ||
			(ValidatingGame.getAmountOfFigureKindOnAssignment(assignment, FigureKind.TRAP) != 1) ||
			(ValidatingGame.getAmountOfFigureKindOnAssignment(assignment, FigureKind.PAPER) != 4) ||
			(ValidatingGame.getAmountOfFigureKindOnAssignment(assignment, FigureKind.ROCK) != 4) ||
			(ValidatingGame.getAmountOfFigureKindOnAssignment(assignment, FigureKind.SCISSORS) != 4)) {
				throw new IllegalArgumentException();
		}
		
		// Versucht da jemand, den Decorator vom falschen Spieler aus aufzurufen?
		if (!p.equals(this.player))
			throw new IllegalArgumentException();
		
		// Figuren dürfen nur in den untersten zwei Reihen gesetzt sein
		for (int counter = 0; counter < 28; counter++)
			if (assignment[counter] != null)
				throw new IllegalArgumentException();
		
		game.setInitialAssignment(p, assignment);
	}

	@Override
	public Figure[] getField() throws RemoteException {
		return game.getField();
	}
	
	public static int getAmountOfFigureKindOnAssignment(FigureKind[] board, FigureKind kind) {
		int amount = 0;
		
		// Wir zählen hier alle Figuren des übergebenen Typs auf dem Spielfeld
		for (FigureKind figureKind: board)
			if ((figureKind != null) && figureKind.equals(kind))
				amount++;
		
		return amount;
	}

	@Override
	public void move(Player p, int from, int to) throws RemoteException {
		// Das Spielfeld zwischenspeichern, da wir mehrmals darauf zugreifen
		Figure[] board = this.game.getField();
		
		// Ursprungs- und Zielfeld dürfen nicht identisch sein
		if (from == to)
			throw new IllegalArgumentException();
		
		// Weder Ursprung noch Ziel dürfen außerhalb des Feldes liegen
		if ((from < 0) || (from > 41) || (to < 0) || (to > 41))
			throw new IllegalArgumentException();
		
		// Nur Züge um ein Feld horizontal und vertikal sind erlaubt
		if ((Math.abs(to-from) != 1) && (Math.abs(to-from) != 7))
			throw new IllegalArgumentException();
		
		// Wenn sich die y-Koordinate bei einem horizontalen Zug ändert, übertreten wir eine Spielfeldgrenze
		if ((Math.abs(to-from) == 1) && (Math.floor(from/7.0d) != Math.floor(to/7.0d)))
			throw new IllegalArgumentException();
		
		// Mit einem leeren Feld kann man nicht ziehen
		if (board[from] == null)
			throw new IllegalArgumentException();

		// Man darf nicht mit Flaggen oder Fallen ziehen
		if (!board[from].getKind().isMovable()) {
			throw new IllegalArgumentException();
		}
		
		// Die zu ziehende Spielfigur gehört gar nicht dem Spieler
		if (!board[from].belongsTo(p))
			throw new IllegalArgumentException();
		
		if (board[to] != null) {
			// Man darf keine eigenen Figuren schlagen
			if (board[to].belongsTo(p))
				throw new IllegalArgumentException();
		}
		
		// Wenn es bis jetzt keine Exception gab, ist der Zug wohl in Ordnung
		game.move(p, from, to);
	}

	@Override
	public Move getLastMove() throws RemoteException {
		return game.getLastMove();
	}

	@Override
	public void sendMessage(Player p, String message) throws RemoteException {
		game.sendMessage(p, message);
	}

	@Override
	public void setInitialChoice(Player p, FigureKind kind) throws RemoteException {
		// Nur Schere, Stein und Papier werden akzeptiert
		if (!kind.isMovable())
			throw new IllegalArgumentException();
		
		game.setInitialChoice(p, kind);
	}

	@Override
	public void setUpdatedKindAfterDraw(Player p, FigureKind kind) throws RemoteException {
		// Nur Schere, Stein und Papier werden akzeptiert
		if (!kind.isMovable())
			throw new IllegalArgumentException();
		
		game.setUpdatedKindAfterDraw(p, kind);
	}

	@Override
	public void surrender(Player p) throws RemoteException {
		game.surrender(p);
	}

	@Override
	public Player getOpponent(Player p) throws RemoteException {
		return game.getOpponent(p);
	}
}