package rps.client.ai;

import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import rps.client.GameListener;
import rps.game.Game;
import rps.game.GameImpl;
import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

import static rps.game.data.FigureKind.FLAG;
import static rps.game.data.FigureKind.TRAP;
import static rps.game.data.FigureKind.PAPER;
import static rps.game.data.FigureKind.ROCK;
import static rps.game.data.FigureKind.SCISSORS;

/**
 * This class contains a very basic AI, that allows to play a game against it.
 * The main benefit is to be able to test the UI.
 */
public class BasicAi implements GameListener {

	private Player player = new Player("Basic AI");
	private Game game;
	
	Random generator = new Random();

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void chatMessage(Player sender, String message) throws RemoteException {
		if (!player.equals(sender)) {
			game.sendMessage(player, "you said: " + message);
		}
	}

	@Override
	public void provideInitialAssignment(Game game) throws RemoteException {
		this.game = game;
		
		FigureKind[] assignment = new FigureKind[42];
		Vector<FigureKind> placement = new Vector<FigureKind>();
		
		// Einmal Falle hinzufügen
		placement.add(TRAP);
		// Einmal Flagge hinzufügen
		placement.add(FLAG);
		
		// 4 Stein, 4 Schere und 4 Papier hinzufügen
		for (int counter = 0; counter < 4; counter++) {
			placement.add(ROCK);
			placement.add(SCISSORS);
			placement.add(PAPER);
		}
		
		// Gut durchmischen
		Collections.shuffle(placement);
		
		// Die Felder, die nicht uns gehören, auf null setzen
		for (int counter = 0; counter < 28; counter++) {
			assignment[counter] = null;
		}
		
		// Die letzten zwei Reihen mit unserer Startaufstellung füllen
		for (int counter = 28; counter < 42; counter++) {
			assignment[counter] = placement.elementAt(counter - 28);
		}
		
		// Die Aufstellung übergeben
		this.game.setInitialAssignment(player, assignment);
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
		// Eine vernichtende Drohung dem Spieler gegenüber ausstoßen ;)
		this.game.sendMessage(this.player, "I'm going to make you cry!");
	}

	@Override
	public void provideNextMove() throws RemoteException {
		// Ein Vektor, der alle möglichen Züge enthält
		Vector<Move> possibleMoves = new Vector<Move>();
		// Unser zwischengespeichertes Spielfeld
		Figure[] board = this.game.getField();
		// Der ausgewählte Zug
		Move selectedMove;
		
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
		
		// Wir wählen zufällig einen der Züge aus
		selectedMove = possibleMoves.elementAt(this.generator.nextInt(possibleMoves.size()-1));
		
		// Wir übermitteln den gewählten Zug an das Spiel
		// Theoretisch könnte eine NullPointerException auftreten, wenn keine gültigen Züge gefunden
		// wurden, allerdings wird Bewegungsunfähigkeit bereits von GameImpl abgefangen, daher ist alles
		// in Ordnung
		this.game.move(this.player, selectedMove.getFrom(), selectedMove.getTo());
	}

	@Override
	public void figureMoved() throws RemoteException {
		// Hm, hier muss ja eigentlich nix passieren
	}

	@Override
	public void figureAttacked() throws RemoteException {
		// hier eigentlich auch nicht
	}

	@Override
	public void provideChoiceAfterFightIsDrawn() throws RemoteException {
		// Zwischenspeicher für unseren Figurentyp
		FigureKind figureKind;
		
		// Zufällig eine Möglichkeit wählen
		switch (this.generator.nextInt(3)) {
			case 0:
				figureKind = FigureKind.PAPER;
				break;
			case 1:
				figureKind = FigureKind.ROCK;
				break;
			default:
				figureKind = FigureKind.SCISSORS;
				break;
		}
		
		// Die zufällig ausgewählte Figur übergeben
		this.game.setUpdatedKindAfterDraw(this.player, figureKind);
	}

	@Override
	public void gameIsLost() throws RemoteException {
		this.game.sendMessage(this.player, "I lost. How could this happen?");
	}

	@Override
	public void gameIsWon() throws RemoteException {
		this.game.sendMessage(this.player, "Sweet Victory! Maybe you want to try again?");
	}

	@Override
	public void gameIsDrawn() throws RemoteException {
		this.game.sendMessage(this.player, "Hm, the game ended drawn. Maybe we will play again?");
	}

	@Override
	public String toString() {
		return player.getNick();
	}
}