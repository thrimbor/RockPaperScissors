package rps.client.ai;

import static rps.game.data.FigureKind.FLAG;
import static rps.game.data.FigureKind.PAPER;
import static rps.game.data.FigureKind.ROCK;
import static rps.game.data.FigureKind.SCISSORS;
import static rps.game.data.FigureKind.TRAP;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import rps.client.GameListener;
import rps.game.Game;
import rps.game.GameImpl;
import rps.game.data.AttackResult;
import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

/**
 * This class contains an advanced AI, that should participate in the
 * tournament.
 */
public class TournamentAi implements GameListener {

	// player nick must end with group number, e.g. "Tournament AI (#123)"
	private final Player player = new Player("Tournament AI (#53)");
	private Game game;
	
	Random generator = new Random();

	//private final int maxDurationForMoveInMilliSeconds;
	//private final int maxDurationForAllMovesInMilliSeconds;
	
	private static int depth = 4;
	
	private ArrayList<Integer> possibleFlagOrTrapOfEnemy = new ArrayList<Integer>();
	private ArrayList<Integer> possibleFlagOrTrapForEnemy = new ArrayList<Integer>();
	private int flagPosition = -1;

	public TournamentAi(int maxDurationForMoveInMilliSeconds, int maxDurationForAllMovesInMilliSeconds) {
		//this.maxDurationForMoveInMilliSeconds = maxDurationForMoveInMilliSeconds;
		//this.maxDurationForAllMovesInMilliSeconds = maxDurationForAllMovesInMilliSeconds;
		
		if (maxDurationForMoveInMilliSeconds <= 100) {
			depth = 3;
		}

		resetMemory();
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void chatMessage(Player sender, String message) throws RemoteException {
		
	}

	@Override
	public void provideInitialAssignment(Game game) throws RemoteException {
		this.game = game;
		
		Vector<FigureKind> assignment = new Vector<FigureKind>();
		FigureKind[] ret = new FigureKind[42];

		assignment.add(TRAP);
		assignment.add(FLAG);

		assignment.add(ROCK);
		assignment.add(ROCK);
		assignment.add(ROCK);
		assignment.add(ROCK);
		
		assignment.add(SCISSORS);
		assignment.add(SCISSORS);
		assignment.add(SCISSORS);
		assignment.add(SCISSORS);
		
		assignment.add(PAPER);
		assignment.add(PAPER);
		assignment.add(PAPER);
		assignment.add(PAPER);
		
		Collections.shuffle(assignment);

		for (int i = 0; i < 28; i++) {
			ret[i] = null;
		}
		for (int i = 28; i < 42; i++) {
			ret[i] = assignment.elementAt(i - 28);
			if (ret[i] == FLAG) {
				this.flagPosition = i;
			}
		}
		
		this.game.setInitialAssignment(this.player, ret);

	}

	@Override
	public void provideInitialChoice() throws RemoteException {
		// Zwischenspeicher für unseren Figurentyp
		FigureKind figureKind;
		
		// Zufällig eine Möglichkeit wählen
		switch (this.generator.nextInt(3)) {
			case 0:
				figureKind = PAPER;
				break;
			case 1:
				figureKind = ROCK;
				break;
			default:
				figureKind = SCISSORS;
				break;
		}
		
		// Die zufällig ausgewählte Figur übergeben
		this.game.setInitialChoice(this.player, figureKind);

	}

	@Override
	public void startGame() throws RemoteException {
		game.sendMessage(player, "Okay! Good luck!");
	}

	@Override
	public void provideNextMove() throws RemoteException {
		miniMax(game.getField(), this.possibleFlagOrTrapOfEnemy, this.possibleFlagOrTrapForEnemy, 1);
	}
	
	private int miniMax (Figure[] board, ArrayList<Integer>possibleFlagOrTrapOfEnemy, ArrayList<Integer> possibleFlagOrTrapForEnemy, int currentdeepth) throws RemoteException {
		// Minimax:
		boolean isOwnMove = (currentdeepth % 2 == 1);
		
		int finalScore = (isOwnMove ? -1 : 1) * 10000000;
		int rating;
		Figure[] newField;
		ArrayList<Integer> flagsOfEnemy;
		ArrayList<Integer> flagsForEnemy;
		Vector<Move> possible;
		if (isOwnMove) {
			possible = getPossibleOwnMoves(board);
		}
		else {
			possible = getPossibleEnemyMoves(board, possibleFlagOrTrapOfEnemy);
		}
		if (possible.size() == 0) {
			return finalScore;
		}
		Move result = possible.firstElement();
		
		
		for (Move move: possible) {
			newField = board.clone();
			move(newField, move);
			flagsOfEnemy = new ArrayList<Integer>(possibleFlagOrTrapOfEnemy);
			flagsForEnemy = new ArrayList<Integer>(possibleFlagOrTrapForEnemy);
			
			if (isOwnMove) {
				flagsForEnemy.remove((Integer) move.getFrom());
				flagsOfEnemy.remove((Integer) move.getTo());
			}
			else {
				flagsOfEnemy.remove((Integer) move.getFrom());
				flagsForEnemy.remove((Integer) move.getTo());
			}
			
			if (board[move.getTo()] != null && board[move.getTo()].getKind() == FLAG) {
				rating = -1000000;
			}
			else if (currentdeepth == depth) {
				rating = rateBoard(board, flagsOfEnemy, flagsForEnemy);
			}
			else {
				rating = miniMax(newField, flagsOfEnemy, flagsForEnemy, currentdeepth + 1);
			}
			
			// Eigene Züge maximieren
			if (isOwnMove && rating >= finalScore) {
				result = move;
				finalScore = rating;
			}
			// Gegnerzüge minimieren
			else if (!isOwnMove && rating <= finalScore)  {
				result = move;
				finalScore = rating;
			}
		}
		
		if (currentdeepth == 1) {
			game.move(player, result.getFrom(), result.getTo());
		}
		
		return finalScore;
		
	}
	
	private void move(Figure[] board, Move move) {
		// Kein Angriff
		if (board[move.getTo()] == null) {
			board[move.getTo()] = board[move.getFrom()];
			board[move.getFrom()] = null;
			return;
		}
		
		if (board[move.getFrom()].belongsTo(player)) {
			// Wir greifen an
			

			//Angriff auf Hidden, wir machen den besten fall draus, dass wir gewinnen
			if (!board[move.getTo()].isDiscovered()) {
				board[move.getTo()] = board[move.getFrom()];
				board[move.getFrom()] = null;
				return;
			}
			
			AttackResult res = board[move.getFrom()].attack(board[move.getTo()]);
			if (res == AttackResult.WIN || res == AttackResult.DRAW) {
				board[move.getTo()] = board[move.getFrom()];
				board[move.getFrom()] = null;
				return;
			}
			if (res == AttackResult.LOOSE) {
				board[move.getFrom()] = null;
				return;
			}
				
		}
		
		// Der Gegner greift uns an
		
		// Auf unsere Flagge
		if (board[move.getTo()].getKind() == FigureKind.FLAG) {
			board[move.getTo()] = board[move.getFrom()];
			board[move.getFrom()] = null;
			return;
		}
		
		// Auf unsere Falle
		if (board[move.getTo()].getKind() == FigureKind.TRAP) {
			board[move.getTo()] = null;
			board[move.getFrom()] = null;
			return;
		}
		
		// Unbekannter Angreifer
		// Lass uns verlieren
		if (!board[move.getFrom()].isDiscovered()) {
			board[move.getTo()] = board[move.getFrom()];
			board[move.getFrom()] = null;
			return;
		}
		
		
		AttackResult res = board[move.getFrom()].attack(board[move.getTo()]);
		if (res == AttackResult.WIN || res == AttackResult.DRAW) {
			board[move.getTo()] = board[move.getFrom()];
			board[move.getFrom()] = null;
			return;
		}
		if (res == AttackResult.LOOSE) {
			board[move.getFrom()] = null;
			return;
		}
		
		
		return;
	}
		

	@Override
	public void figureMoved() throws RemoteException {
		// Erinnerung, welche Figuren keine Fallen und keine Flaggen waren
		int source = game.getLastMove().getFrom();
		possibleFlagOrTrapForEnemy.remove((Integer) source);
		possibleFlagOrTrapOfEnemy.remove((Integer) source);
		
		int dest = game.getLastMove().getTo();
		possibleFlagOrTrapForEnemy.remove((Integer) dest);
		possibleFlagOrTrapOfEnemy.remove((Integer) dest);
	}

	@Override
	public void figureAttacked() throws RemoteException {
		
	}

	@Override
	public void provideChoiceAfterFightIsDrawn() throws RemoteException {
		// Zwischenspeicher für unseren Figurentyp
		FigureKind figureKind;
		
		// Zufällig eine Möglichkeit wählen
		switch (this.generator.nextInt(3)) {
			case 0:
				figureKind = PAPER;
				break;
			case 1:
				figureKind = ROCK;
				break;
			default:
				figureKind = SCISSORS;
				break;
		}
		
		// Die zufällig ausgewählte Figur übergeben
		this.game.setUpdatedKindAfterDraw(this.player, figureKind);
	}

	@Override
	public void gameIsLost() throws RemoteException {
		resetMemory();
	}

	@Override
	public void gameIsWon() throws RemoteException {
		resetMemory();

	}

	@Override
	public void gameIsDrawn() throws RemoteException {
		resetMemory();
	}
	
	private void resetMemory() {
		this.possibleFlagOrTrapOfEnemy = new ArrayList<Integer>();
		this.possibleFlagOrTrapForEnemy = new ArrayList<Integer>();
		
		for (int i = 0; i < 14; i++) {
			this.possibleFlagOrTrapOfEnemy.add(i);
			this.possibleFlagOrTrapForEnemy.add(28 + i);
		}
	}

	@Override
	public String toString() {
		return player.getNick();
	}
	
	private Vector<Move> getPossibleOwnMoves(Figure[] board) {
		// Ein Vektor, der alle möglichen Züge enthält
		Vector<Move> possibleMoves = new Vector<Move>();
		
		// Wir untersuchen jedes Feld des Spielfeldes
		for (int counter = 0; counter < 42; counter++) {
			// Befindet sich dort eine unserer Figuren?
			if ((board[counter] != null) && board[counter].belongsTo(this.player)) {
				// Ist ein Zug nach links möglich?
				if (GameImpl.isMoveAllowed(board, counter, -1, this.player)) {
					// Den Zug in den Vektor aufnehmen
					possibleMoves.addElement(new Move(counter, counter-1, null));
				}
				// Ist ein Zug nach rechts möglich?
				if (GameImpl.isMoveAllowed(board, counter, +1, this.player)) {
					// Den Zug in den Vektor aufnehmen
					possibleMoves.addElement(new Move(counter, counter+1, null));
				}
				// Ist ein Zug nach oben möglich?
				if (GameImpl.isMoveAllowed(board, counter, -7, this.player)) {
					// Den Zug in den Vektor aufnehmen
					possibleMoves.addElement(new Move(counter, counter-7, null));
				}
				// Ist ein Zug nach unten möglich?
				if (GameImpl.isMoveAllowed(board, counter, +7, this.player)) {
					// Den Zug in den Vektor aufnehmen
					possibleMoves.addElement(new Move(counter, counter +7, null));
				}
			}
		}
		
		return possibleMoves;
	}
	
	private Vector<Move> getPossibleEnemyMoves(Figure[] board, ArrayList<Integer> possibleFlagOrTrapOfEnemy) {
		// Ein Vektor, der alle möglichen Züge enthält
		Vector<Move> possibleMoves = new Vector<Move>();
		
		// Wir untersuchen jedes Feld des Spielfeldes
		for (int counter = 0; counter < 42; counter++) {
			// Befindet sich dort eine der gegnerischen Figuren?
			if ((board[counter] != null) && !board[counter].belongsTo(this.player)) {
				// Ist ein Zug nach links möglich?
				if (isEnemyMoveAllowed(board, counter, -1, possibleFlagOrTrapOfEnemy)) {
					// Den Zug in den Vektor aufnehmen
					possibleMoves.addElement(new Move(counter, counter-1, null));
				}
				// Ist ein Zug nach rechts möglich?
				if (isEnemyMoveAllowed(board, counter, +1, possibleFlagOrTrapOfEnemy)) {
					// Den Zug in den Vektor aufnehmen
					possibleMoves.addElement(new Move(counter, counter+1, null));
				}
				// Ist ein Zug nach oben möglich?
				if (isEnemyMoveAllowed(board, counter, -7, possibleFlagOrTrapOfEnemy)) {
					// Den Zug in den Vektor aufnehmen
					possibleMoves.addElement(new Move(counter, counter-7, null));
				}
				// Ist ein Zug nach unten möglich?
				if (isEnemyMoveAllowed(board, counter, +7, possibleFlagOrTrapOfEnemy)) {
					// Den Zug in den Vektor aufnehmen
					possibleMoves.addElement(new Move(counter, counter +7, null));
				}
			}
		}
		
		return possibleMoves;
	}
	
	// Überprüft, ein Zug des Spielers mit der Figur auf fromIndex um offset erlaubt ist
	public boolean isEnemyMoveAllowed (Figure[] board, int fromIndex, int offset, ArrayList<Integer> possibleFlagOrTrapOfEnemy) {
		
		// Flaggen und Fallen dürfen sich nicht bewegen
		// Prinzipiell darf sich alles bewegen, da wir nicht wissen, was die flagge bzw falle ist,
		// außer wir haben <=2 unmovable eintrage dann ist klar was falle und flagge ist
		if (possibleFlagOrTrapOfEnemy.size() <= 2 && possibleFlagOrTrapOfEnemy.contains(fromIndex)) {
			return false;
		}
		
		// Es sind nur Bewegungen in 1er oder 7er Schritten erlaubt (1 = horizontal, 7 = vertikal)
		if (Math.abs(offset) == 1 || Math.abs(offset) == 7) {
			// Berechne den Index des Zielfeldes
			int toIndex = fromIndex + offset;
			
			// Das Zielfeld darf nicht außerhalb des Spielfeldes liegen
			if ((toIndex < 0) || (toIndex > 41))
				return false;
			
			// Wenn wir uns horizontal bewegen, darf sich unsere y-Koordinate nicht ändern, sonst haben wir den Spielfeldrand übertreten
			if ((Math.abs(offset) == 1) && (fromIndex - (fromIndex % 7)) != (toIndex - (toIndex % 7)))
				return false;
			
			// Ist unser Zielfeld leer? Dann können wir auf jeden Fall dorthin
			if (board[toIndex] == null) return true;
			
			// Wenn das Zielfeld nicht leer ist, darf dort keine eigene Figur stehen (wir können uns nicht selbst schlagen)
			if (board[toIndex].belongsTo(player)) {
				// Dort steht eine für den Gegner gegnerische Figur (also unsere), er kann also ziehen
				return true;
			}
		}
		
		// Kein Zug möglich
		return false;
	}
	
	// Heuristikfunktion
	private int rateBoard(Figure[] board, ArrayList<Integer>possibleFlagOrTrapOfEnemy, ArrayList<Integer> possibleFlagOrTrapForEnemy) {
		int rating = 0;
		int dist;
		
		
		for (int i=0; i < 42; i++) {
			// Leere Felder überspringen
			if (board[i] == null) continue;
			
			if (board[i].belongsTo(player)) {
				// Je mehr eigene Figuren desto besser
				// noch besser aber: mehr unaufgedeckte eigene
				// Am besten aber: keine Flaggenpositionen preisgeben und Figuren, die für den Gegner als Flagge in Frage kommen, möglichst wenig bewegen
				if (possibleFlagOrTrapForEnemy.contains(i)) {
					rating += 100;
				}
				else if (board[i].isDiscovered()) {
					rating += 10;
				}
				else {
					rating += 30;
				}
				
				// Abstand zu Gegnerfiguren bestimmen und minimieren
				for (int enemy: possibleFlagOrTrapOfEnemy)  {
					dist = calcDistance(i, enemy);
					if (dist == 0) {
						// Für den Fall dass Funktionsaufrufe zu spät kommen und evtl. noch Sachen gespeichert sind
						// die es nicht sein sollen eine division by zero exception vermeiden
						continue;
					}
					
					rating += 400/(dist*dist);
				}
			}
			
			// Das gleiche für den Gegner
			else {
				// Je mehr Figuren unaufgedeckt, desto besser für den Gegner
				if (possibleFlagOrTrapOfEnemy.contains(i)) {
					rating -= 100;
				}
				else if (board[i].isDiscovered()) {
					rating -= 10;
				}
				else {
					rating -= 30;
				}
				
				// Abstand zu der Gegnerfiguren zur eigenen Flagge möglichst groß halten
				dist = calcDistance(i, flagPosition);
				if (dist == 0) {
					// Für den Fall dass Funktionsaufrufe zu spät kommen und evtl. noch Sachen gespeichert sind
					// die es nicht sein sollen eine division by zero exception vermeiden
					continue;
				}
				rating -= 200/(dist*dist);
			}
		}
		
		// Bisschen Zufälligkeit
		return rating + generator.nextInt(10) - 5;
	}
	
	private int calcDistance(int p1, int p2) {
		int dist = 0;
		int from;
		int to;
		
		if (p1 == p2) return 0;
		
		if (p1 < p2) {
			from = p1;
			to = p2;
		}
		else {
			to = p1;
			from = p2;
		}
		
		// Anzahl der Reihen bestimmen
		dist = to % 7 - from % 7;
		
		// Anzahl der Spalten addieren
		dist +=  Math.abs((from + dist * 7) - to);
		
		return dist;
	}
}