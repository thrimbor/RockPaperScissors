package rps.game;

import java.rmi.RemoteException;

import rps.client.GameListener;
import rps.game.data.AttackResult;
import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

/**
 * The {@code GameImpl} is an implementation for the {@code Game} interface. It
 * contains the necessary logic to play a game.
 */
public class GameImpl implements Game {

	private GameListener listener1;
	private GameListener listener2;
	
	//Das Spielbrett
	private Figure[] board = new Figure[42];
	
	// Statusvariablen, die Anzeigen, ob die Startaufstellung vom jeweiligen Spieler empfangen wurde
	private boolean receivedAssignmentFromPlayerOne = false;
	private boolean receivedAssignmentFromPlayerTwo = false;
	
	// Variablen, die die momentane Figurauswahl der beiden Spieler enthalten
	private FigureKind initialChoiceOfPlayerOne;
	private FigureKind initialChoiceOfPlayerTwo;
	private FigureKind choiceOfPlayerOne;
	private FigureKind choiceOfPlayerTwo;
	
	private Player player1;
	private Player player2;
	// Der Spieler, dem momentan am Zug ist
	private Player activePlayer;
	
	// Eine Statusvariable, die anzeigt, ob einer der beiden Spieler aufgegeben hat
	private boolean aPlayerDidSurrender = false;
	
	// Der letzte erfolgreich ausgeführte Zug
	private Move lastMove;
	
	// Zeigt an, ob der aktuell stattfindende Kampf momentan unentschieden ist
	private boolean activeFightisDrawn = false;

	public GameImpl(GameListener listener1, GameListener listener2) throws RemoteException {
		this.listener1 = listener1;
		this.listener2 = listener2;
		this.player1 = listener1.getPlayer();
		this.player2 = listener2.getPlayer();
	}

	@Override
	public void sendMessage(Player p, String message) throws RemoteException {
		listener1.chatMessage(p, message);
		listener2.chatMessage(p, message);
	}

	@Override
	public void setInitialAssignment(Player p, FigureKind[] assignment) throws RemoteException {
		// Die Startaufstellung eintragen
		// Das Feld darf hier nicht gedreht werden, die AI und der zweite Spieler bekommen
		// es durch den FieldRotatingGame-Decorator bereits gedreht
		
		// Wir erlauben das ändern nur, wenn der setzende Spieler noch keine Aufstellung bereitgestellt hat
		if ((!this.receivedAssignmentFromPlayerOne && player1.equals(p)) || (!this.receivedAssignmentFromPlayerTwo && player2.equals(p))) {
			// Einmal durch das ganze Spielfeld
			for (int x = 0; x < 42; x++) {
				// Ist die betreffende Stelle im assignment belegt?
				if (assignment[x] != null)
					// Figur eintragen
					this.board[x] = new Figure(assignment[x], p);
			}
			
			// Die boolean-Variablen korrekt setzen, damit wir Fehler erkennen
			if (player1.equals(p))
				this.receivedAssignmentFromPlayerOne = true;
			else
				this.receivedAssignmentFromPlayerTwo = true;
			
		} else {
			// Versucht hier einer der Spieler zu schummeln und seine Startaufstellung mehrfach zu setzen?
			throw new IllegalStateException();
		}
		
		// Wenn wir von beiden Spielern eine Aufstellung haben, spielen wir eine Runde Schere, Stein, Papier,
		// und den Spieler zu bestimmen, der zuerst ziehen darf
		if (this.receivedAssignmentFromPlayerOne && this.receivedAssignmentFromPlayerTwo) {
			this.initialChoiceOfPlayerOne = null;
			this.initialChoiceOfPlayerTwo = null;
			this.listener1.provideInitialChoice();
			this.listener2.provideInitialChoice();
		}
	}

	@Override
	public void setInitialChoice(Player p, FigureKind kind) throws RemoteException {
		// Zuerst einmal müssen wir herausfinden, welcher Spieler uns hier anquatscht
		if (player1.equals(p)) {
			// Es war Spieler 1, wir setzen seine Auswahl
			this.initialChoiceOfPlayerOne = kind;
		} else {
			// Es war Spieler 2, wir setzen seine Auswahl
			this.initialChoiceOfPlayerTwo = kind;
		}
		
		// Haben beide Spieler gewählt?
		if ((this.initialChoiceOfPlayerOne != null) && (this.initialChoiceOfPlayerTwo != null)) {
			// Wir berechnen den Kampf
			AttackResult attackResult = this.initialChoiceOfPlayerOne.attack(this.initialChoiceOfPlayerTwo);
			
			// Wir werten den Kampf aus
			if (attackResult == AttackResult.WIN) {
				// Spieler 1 ist der momentan aktive Spieler
				this.activePlayer = player1;
				
				// Spieler 1 hat gewonnen, das Spiel wird gestartet
				this.listener1.startGame();
				this.listener2.startGame();
				
				// Spieler 1 darf den ersten Zug machen
				this.listener1.provideNextMove();
				
				return;
			} else if (attackResult == AttackResult.LOOSE) {
				// Spieler 2 ist der momentan aktive Spieler
				this.activePlayer = player2;
				
				// Spieler 2 hat gewonnen, das Spiel wird gestartet
				this.listener1.startGame();
				this.listener2.startGame();
				
				// Spieler 2 darf den ersten Zug machen
				this.listener2.provideNextMove();
				
				return;
			} else if (attackResult == AttackResult.DRAW) {
				// Unentschieden, beide Spieler müssen neu wählen
				this.initialChoiceOfPlayerOne = null;
				this.initialChoiceOfPlayerTwo = null;
				this.listener1.provideInitialChoice();
				this.listener2.provideInitialChoice();
			}
		}
	}

	@Override
	public void move(Player movingPlayer, int fromIndex, int toIndex) throws RemoteException {
		// Ist der Spieler überhaupt dran? Wenn nein, kriegt er auf die Nase
		if (!this.activePlayer.equals(movingPlayer)) throw new IllegalStateException();
		
		// den letzten Spielzug immer speichern, dabei eine Kopie des Spielfeldes verwenden
		this.lastMove = new Move(fromIndex, toIndex, this.board.clone());
		
		// Ist dieser Zug ein Angriff?
		if (this.board[toIndex] != null) {
			// Den Kampf berechnen lassen
			AttackResult attackResult = this.board[fromIndex].attack(this.board[toIndex]);
			
			// Die angreifende Figur wird aufgedeckt
			this.board[fromIndex].setDiscovered();
			this.lastMove.getOldField()[fromIndex].setDiscovered();
			// Die angegriffene auch
			this.board[toIndex].setDiscovered();
			this.lastMove.getOldField()[toIndex].setDiscovered();
			
			// Wir werten den Kampf aus
			if (attackResult == AttackResult.DRAW) {
				// Kampf ist unentschieden, wir merken uns das
				this.activeFightisDrawn = true;
				// Wir setzen die Choice-Variablen zurück, die benutzen wir jetzt nämlich in setUpdatedKindAfterDrawn
				this.choiceOfPlayerOne = null;
				this.choiceOfPlayerTwo = null;
				// Beide müssen neu wählen
				this.listener1.provideChoiceAfterFightIsDrawn();
				this.listener2.provideChoiceAfterFightIsDrawn();
				// Bei einem Unentschieden müssen wir die Auswahl des nächsten Spielers überspringen
				return;
			} else if (attackResult == AttackResult.WIN) {
				// Der Kampf ist gewonnen, die angreifende Figur ersetzt die angegriffene Figur
				this.board[toIndex] = this.board[fromIndex];
				// Das alte Feld ist nun leer
				this.board[fromIndex] = null;
			} else if (attackResult == AttackResult.LOOSE) {
				// Der Kampf is verloren, die angreifende Figur wird entfernt
				this.board[fromIndex] = null;
			} else if (attackResult == AttackResult.WIN_AGAINST_FLAG) {
				// Der Spieler hat gewonnen, Flagge nicht entfernen
				// Alle Figuren aufdecken
				this.uncoverField();
				
				// Wir verhindern, dass ein Spieler noch irgendetwas machen kann
				this.activePlayer = null;
				
				// Dem Sieger wird der Sieg mitgeteilt
				(player1.equals(movingPlayer) ? this.listener1 : this.listener2).gameIsWon();
				// Dem Verlierer wird die Niederlage mitgeteilt
				(player1.equals(movingPlayer) ? this.listener2 : this.listener1).gameIsLost();
				
				return;
			} else if (attackResult == AttackResult.LOOSE_AGAINST_TRAP) {
				// Gegen Falle verloren, die Figur verschwindet
				this.board[fromIndex] = null;
				// Die Falle verschwindet ebenfalls
				this.board[toIndex] = null;
			}
			
			// Die Spieler über den Kampf informieren
			this.listener1.figureAttacked();
			this.listener2.figureAttacked();
			
		} else {
			// nur ein Zug, kein Kampf
			// neues Feld besetzen
			this.board[toIndex] = this.board[fromIndex];
			// altes Feld leeren
			this.board[fromIndex] = null;
			
			// Spieler informieren
			this.listener1.figureMoved();
			this.listener2.figureMoved();
		}
		
		this.chooseNextPlayer();
	}

	private void uncoverField() {
		// Das gesamte Spielfeld aufdecken
		for (Figure figure : this.board) {
			if (figure != null) {
				figure.setDiscovered();
			}
		}
	}

	private void chooseNextPlayer() throws RemoteException {
		// Der andere Spieler ist dran
		this.activePlayer = (this.activePlayer.equals(player1) ? player2 : player1);
		
		// Kann der Spieler sich überhaupt bewegen?
		if (!this.canPlayerMove(this.activePlayer)) {
			// Spieler nochmals wechseln
			this.activePlayer = (this.activePlayer.equals(player1) ? player2 : player1);
			
			// Kann dieser Spieler sich auch nicht bewegen?
			if (!this.canPlayerMove(this.activePlayer)) {
				// Wir verhindern, dass ein Spieler noch irgendetwas machen kann
				this.activePlayer = null;
				
				// Keiner kann sich mehr bewegen, Spiel endet unentschieden
				this.uncoverField();
				this.listener1.gameIsDrawn();
				this.listener2.gameIsDrawn();
				return;
			}
			
		}
		
		// Zug erfragen
		(this.activePlayer.equals(player1) ? this.listener1 : this.listener2).provideNextMove();
		
	}
	
	// Überprüft, ob ein Spieler mit mindestens einer seiner Figuren ziehen kann
	private boolean canPlayerMove (Player player) {
		// Einmal das ganze Spielfeld betrachten
		for (int counter = 0; counter < 42; counter++) {
			// gehört die Figur diesem Spieler?
			if ((this.board[counter] != null) && this.board[counter].belongsTo(player)) {
				// Können sich die Figur nach oben, unten, links oder rechts bewegen?
				if (GameImpl.isMoveAllowed(this.board, counter, -1, player) || GameImpl.isMoveAllowed(this.board, counter, +1, player) || GameImpl.isMoveAllowed(this.board, counter, -7, player) || GameImpl.isMoveAllowed(this.board, counter, +7, player)) {
					return true;
				}
			}
		}
		
		// Wenn keine der Figuren sich bewegen konnte, kann der Spieler nicht ziehen
		return false;
	}
	
	// Überprüft, ein Zug des Spielers mit der Figur auf fromIndex um offset erlaubt ist
	public static boolean isMoveAllowed (Figure[] board, int fromIndex, int offset, Player player) {
		// Flaggen und Fallen dürfen sich nicht bewegen
		if (!board[fromIndex].getKind().isMovable())
			return false;
		
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
			if (!board[toIndex].belongsTo(player)) {
				// Dort steht eine gegnerische Figur, wir können also ziehen
				return true;
			}
		}
		
		// Kein Zug möglich
		return false;
	}

	@Override
	public void setUpdatedKindAfterDraw(Player p, FigureKind kind) throws RemoteException {
		// Versucht da einer zu schummeln?
		if (!this.activeFightisDrawn) throw new IllegalStateException();
		
		// Der erste Spieler darf nicht zweimal wählen
		if (player1.equals(p) && (this.choiceOfPlayerOne != null)) throw new IllegalStateException();
		
		// Der zweite Spieler darf auch nicht zweimal wählen
		if (player2.equals(p) && (this.choiceOfPlayerTwo != null)) throw new IllegalStateException();
		
		// Wir setzen die Auswahl des Spielers
		if (player1.equals(p)) {
			this.choiceOfPlayerOne = kind;
		} else {
			this.choiceOfPlayerTwo = kind;
		}
		
		// Haben beide gewählt?
		if ((this.choiceOfPlayerOne != null) && (this.choiceOfPlayerTwo != null)) {
			// Erst einmal das Unentschieden zurücksetzen
			this.activeFightisDrawn = false;
			
			// neu gewählte Typen setzen
			if (this.board[this.lastMove.getFrom()].belongsTo(player1)) {
				this.board[this.lastMove.getFrom()] = new Figure(this.choiceOfPlayerOne, player1);
			} else {
				this.board[this.lastMove.getFrom()] = new Figure(this.choiceOfPlayerTwo, player2);
			}
			
			if (this.board[this.lastMove.getTo()].belongsTo(player1)) {
				this.board[this.lastMove.getTo()] = new Figure(this.choiceOfPlayerOne, player1);
			} else {
				this.board[this.lastMove.getTo()] = new Figure(this.choiceOfPlayerTwo, player2);
			}
			
			// Figuren neu aufdecken, da sie durch das updaten überschrieben wurden
			this.board[this.lastMove.getFrom()].setDiscovered();
			this.board[this.lastMove.getTo()].setDiscovered();
			
			// Das Spielfeld des letzten Zuges muss geupdated werden
			this.lastMove.getOldField()[this.lastMove.getFrom()] = this.board[this.lastMove.getFrom()].clone();
			this.lastMove.getOldField()[this.lastMove.getTo()] = this.board[this.lastMove.getTo()].clone();
			
			// Wir können den Kampf jetzt simulieren
			AttackResult attackResult = this.board[this.lastMove.getFrom()].attack(this.board[this.lastMove.getTo()]);
			
			// Und auswerten
			if (attackResult == AttackResult.DRAW) {
				// Schon wieder unentschieden
				this.activeFightisDrawn = true;
				
				// Choice-Variablen resetten
				this.choiceOfPlayerOne = null;
				this.choiceOfPlayerTwo = null;
				
				// Und nochmals neu wählen lassen
				this.listener1.provideChoiceAfterFightIsDrawn();
				this.listener2.provideChoiceAfterFightIsDrawn();
				
				// Auswahl des nächsten Spielers überspringen
				return;
			} else if (attackResult == AttackResult.WIN) {
				// Der Kampf ist gewonnen, die angreifende Figur ersetzt die angegriffene Figur
				this.board[this.lastMove.getTo()] = this.board[this.lastMove.getFrom()];
				// Das alte Feld ist nun leer
				this.board[this.lastMove.getFrom()] = null;
			} else if (attackResult == AttackResult.LOOSE) {
				// Der Kampf is verloren, die angreifende Figur wird entfernt
				this.board[this.lastMove.getFrom()] = null;
			}
			
			this.listener1.figureAttacked();
			this.listener2.figureAttacked();
			this.activeFightisDrawn = false;
			
			// Und der nächste Spieler darf jetzt ran
			this.chooseNextPlayer();
		}
	}

	@Override
	public void surrender(Player p) throws RemoteException {
		// Wenn ein Spieler aufgegeben hat, darf der andere nicht auch noch aufgeben
		if (this.aPlayerDidSurrender) throw new IllegalStateException();
		
		this.uncoverField();
		
		// Die Spieler informieren
		listener1.chatMessage(p, "I surrender");
		listener2.chatMessage(p, "I surrender");
		
		// Der aufgebende Spieler verliert
		(player1.equals(p) ? this.listener1 : this.listener2).gameIsLost();
		// Der andere Spieler gewinnt
		(player1.equals(p) ? this.listener2 : this.listener1).gameIsWon();
		
		// Ein Spieler hat aufgegeben, wir merken uns das
		this.aPlayerDidSurrender = true;
	}
	
	@Override
	public Figure[] getField() throws RemoteException {
		// Wir geben einfach das Spielfeld zurück
		return this.board.clone();
	}

	@Override
	public Move getLastMove() throws RemoteException {
		// Wir geben einfach den letzten Spielzug zurück
		if (this.lastMove == null) return null;
		return new Move(this.lastMove.getFrom(), this.lastMove.getTo(), this.lastMove.getOldField().clone());
	}

	@Override
	public Player getOpponent(Player p) throws RemoteException {
		// Wir geben den Gegner des übergebenen Spielers zurück
		return this.listener1.getPlayer().equals(p) ? this.listener2.getPlayer() : this.listener1.getPlayer();
	}
}