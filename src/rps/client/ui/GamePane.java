package rps.client.ui;

import static javax.swing.BoxLayout.Y_AXIS;

import rps.icons.Icons;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import rps.game.Game;
import rps.game.GameImpl;
import rps.game.data.Figure;
import rps.game.data.FigureKind;
import rps.game.data.Move;
import rps.game.data.Player;

public class GamePane {
	private final JPanel gameAndStatusPane = new JPanel();
	private final JPanel gamePane = new JPanel();
	private final JLabel statusLabel = new JLabel();
	private final JPanel chatPane = new JPanel();
	private final JPanel boardPane = new JPanel();
	private final JTextField chatInput = new JTextField();
	private final JTextArea chat = new JTextArea(4, 30);
	private final NotificationPane notification;
	
	private BoardFieldButton[] boardFields = new BoardFieldButton[42];
	
	private final JButton initialAssignmentAccept = new JButton("Start game!");
	private final JButton randomAssignment = new JButton("Random");
	
	private JFrame rootFrame;

	private Game game;
	private Player player;
	private Player opponentPlayer;
	
	private boolean provideMove = false;
	private boolean isFirstMove = true;
	private boolean initialAssigmentTime = false;
	
	/**
	 * Initialisiert die Container des GamePanes
	 * @param parent Elterncontainer, in unserem Fall vom Hauptfenster der Inhalt
	 */
	public GamePane(Container parent, JFrame rootFrame) {
		this.rootFrame = rootFrame;
		
		JPanel rightPane = new JPanel();
		// Ein Panel, dass die Statusbar enthält
		JPanel statusPanel = new JPanel();
		
		// Chat
		chatPane.setLayout(new BoxLayout(chatPane, Y_AXIS));
		chatPane.add(chatInput);
		chatPane.add(new JScrollPane(chat));
		chat.setLineWrap(true);
		chat.setEditable(false);
		
		// Brett
		boardPane.setLayout(new GridLayout(6,7));
		for (int i = 0; i < 42; i++) {
			boardFields[i] = new BoardFieldButton(i);
			boardFields[i].setSize(20, 20);
	
			boardPane.add(boardFields[i]);
			
		}
		
		// Hinweisfenster
		notification = new NotificationPane(rootFrame);
		
		// Steuerelemente rundigrum
		initialAssignmentAccept.setIcon(Icons.start);
		randomAssignment.setIcon(Icons.arrow_random);
		
		// GamePane an sich
		gamePane.setLayout(new BorderLayout(5,5));
		gamePane.add(chatPane, BorderLayout.SOUTH);
		gamePane.add(boardPane, BorderLayout.CENTER);
		gamePane.add(rightPane, BorderLayout.EAST);
		rightPane.setLayout(new GridLayout(8,1));
		rightPane.add(initialAssignmentAccept);
		rightPane.add(randomAssignment);
		
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		statusPanel.setPreferredSize(new Dimension(1000, 16));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);
		
		gameAndStatusPane.setLayout(new BorderLayout());
		gameAndStatusPane.add(gamePane, BorderLayout.CENTER);
		gameAndStatusPane.add(statusPanel, BorderLayout.SOUTH);
		gameAndStatusPane.setVisible(false);
		
		// Und nun dem Hauptfenster hinzufügen
		parent.add(gameAndStatusPane);
		
		// Funktionen der Buttons zuweisen
		bindButtons();
	}

	private void bindButtons() {
		chatInput.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				// Bei Enter fügen wir Text zum Chat hinzu
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					addToChat();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {}

			@Override
			public void keyReleased(KeyEvent e) {}
		});
		
		initialAssignmentAccept.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// Nur wenn die richtige Anzahl der jeweiligen Figuren vorhanden ist dürfen wir starten
				if (getAmountOfSpecificFiguresOfPlayerOnBoard(FigureKind.SCISSORS) == 4 &&
						getAmountOfSpecificFiguresOfPlayerOnBoard(FigureKind.PAPER) == 4 &&
						getAmountOfSpecificFiguresOfPlayerOnBoard(FigureKind.ROCK) == 4 &&
						getAmountOfSpecificFiguresOfPlayerOnBoard(FigureKind.TRAP) == 1 &&
						getAmountOfSpecificFiguresOfPlayerOnBoard(FigureKind.FLAG) == 1) {
					

					// Eine kleine Sicherheitsabfrage
					int result = JOptionPane.showConfirmDialog(null, "Is your assignment okay?", "Can we start?", JOptionPane.YES_NO_OPTION);
					// Der Spieler hat abgebrochen, wir brechen hier also auch ab
					if (result == JOptionPane.NO_OPTION)
						return;
					
					try {
						// Wir setzen die Startaufstellung
						game.setInitialAssignment(player, getAssignmentOfPlayer());
						// Wir warten auf die gegnerische Startaufstellung
						showAsStatus("Waiting for opponent assignment");
						// Die Zuweisungsphase ist vorbei
						initialAssigmentTime = false;
						// Der Shuffle-Button wird deaktiviert
						randomAssignment.setEnabled(false);
						// Der Startbutton wird deaktiviert
						initialAssignmentAccept.setEnabled(false);
					}
					catch (RemoteException e) {
						e.printStackTrace();
					}
				} else {
					// Der Spieler hat eine ungültige Startaufstellung angegeben
					JOptionPane.showMessageDialog(gamePane, "Please set exactly 4 figures of each type, 1 flag and 1 trap!" );
				}
			}
		});
		
		randomAssignment.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				// Wenn der Spieler mischen will darf er nur eine Flagge und eine Falle haben
				if (getAmountOfSpecificFiguresOfPlayerOnBoard(FigureKind.FLAG) > 1 || getAmountOfSpecificFiguresOfPlayerOnBoard(FigureKind.TRAP) > 1) {
					JOptionPane.showMessageDialog(gamePane, "Please set maximum 1 flag and maximum 1 trap before you shuffle" );
					return;
				}
				
				// Will der Spieler wirklich mischen?
				int result = JOptionPane.showConfirmDialog(null, "Shuffle your assignment? Flag and Trap will stay", "Shuffle", JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.NO_OPTION) {
					return;
				}

				// Einen Zufallszahlengenerator holen
				Random generator = new Random();
				
				// Falle setzen, falls nicht gesetzt
				if (getAmountOfSpecificFiguresOfPlayerOnBoard(FigureKind.TRAP) == 0) {
					int i = 28 + generator.nextInt(13);
					// Wir wollen nicht auf die Flagge setzen
					while (boardFields[i].getFigureKind() == FigureKind.FLAG) {
						i = 28 + generator.nextInt(13);
					}
					// Die Falle setzen
					boardFields[i].setFigureKind(FigureKind.TRAP);
				}
				
				// Flagge setzen, falls nicht gesetzt
				if (getAmountOfSpecificFiguresOfPlayerOnBoard(FigureKind.FLAG) == 0) {
					int i = 28 + generator.nextInt(13);
					// Wir wollen nicht auf die Falle setzen
					while (boardFields[i].getFigureKind() == FigureKind.TRAP) {
						i = 28 + generator.nextInt(13);
					}
					// Die Flagge setzen
					boardFields[i].setFigureKind(FigureKind.FLAG);
				}
				
				// Eine Collection zum mischen erstellen
				Vector<BoardFieldButton> v = new Vector<BoardFieldButton>();
				
				for (int i = 28; i < 42; i++) {
					if (boardFields[i].getFigureKind() == FigureKind.FLAG || boardFields[i].getFigureKind() == FigureKind.TRAP) {
						continue;
					}
					v.add(boardFields[i]);
				}
				
				// Einmal gut durchmischen
				Collections.shuffle(v);

				// gemischte Figurentypen setzen
				v.elementAt(0).setFigureKind(FigureKind.ROCK);
				v.elementAt(1).setFigureKind(FigureKind.ROCK);
				v.elementAt(2).setFigureKind(FigureKind.ROCK);
				v.elementAt(3).setFigureKind(FigureKind.ROCK);

				v.elementAt(4).setFigureKind(FigureKind.PAPER);
				v.elementAt(5).setFigureKind(FigureKind.PAPER);
				v.elementAt(6).setFigureKind(FigureKind.PAPER);
				v.elementAt(7).setFigureKind(FigureKind.PAPER);

				v.elementAt(8).setFigureKind(FigureKind.SCISSORS);
				v.elementAt(9).setFigureKind(FigureKind.SCISSORS);
				v.elementAt(10).setFigureKind(FigureKind.SCISSORS);
				v.elementAt(11).setFigureKind(FigureKind.SCISSORS);
			}
		});
	}

	// Liest den Chat-Input aus, sendet den Inhalt ab und leert ihn
	private void addToChat() {
		String message = chatInput.getText().trim();
		if (message.length() > 0) {
			try {
				game.sendMessage(player, message);
				chatInput.setText("");
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void showAsStatus(String text) {
		this.statusLabel.setText("Status: " + text);
	}

	public void hide() {
		gameAndStatusPane.setVisible(false);
		notification.hide();
	}

	public void startGame(Player player, Game game) throws RemoteException {
		this.player = player;
		this.game = game;
		this.opponentPlayer = game.getOpponent(player);
		reset();
		gameAndStatusPane.setVisible(true);
		chat.append("Welcome to Rock Paper Scissors\n");
	}

	public void receivedMessage(Player sender, String message) {
		String formatted = sender.getNick() + ": " + message + "\n";
		chat.append(formatted);
		chat.setCaretPosition(chat.getDocument().getLength());
	}

	private void reset() {
		chat.setText(null);
		this.initialAssignmentAccept.setEnabled(true);
		
		boardPane.removeAll();
		for (int i = 0; i < 42; i++) {
			boardFields[i] = new BoardFieldButton(i);
			boardFields[i].setSize(20, 20);
			boardPane.add(boardFields[i]);
			
		}
		
		this.isFirstMove = true;
		this.provideMove = false;
	}
	
	public void prepareBoardButtonsForInitialAssignment() {
		initialAssigmentTime = true;

		initialAssignmentAccept.setEnabled(true);
		randomAssignment.setEnabled(true);
		for (int i = 28; i<=41; i++) {
			boardFields[i].setEnabled(true);
			boardFields[i].setIsPlayersFigure(true);
			boardFields[i].addMouseListener(new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {}
				@Override
				public void mousePressed(MouseEvent e) {}
				@Override
				public void mouseExited(MouseEvent e) {}
				@Override
				public void mouseEntered(MouseEvent e) {}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					if (!initialAssigmentTime) {
						return;
					}
					
					// Wenn ein Rechtsklick verwendet wurde, gehen wir die Figurtypen in umgekehrter Reihenfolge durch
					boolean reverse = (e.getButton() == MouseEvent.BUTTON3);
					BoardFieldButton button = (BoardFieldButton) e.getSource();
					
					// null braucht eine Sonderbehandlung, da bei switch sonst eine Exception fliegt
					if (button.getFigureKind() == null) {
						button.setFigureKind(reverse ? FigureKind.FLAG : FigureKind.SCISSORS);
						return;
					}
					
					switch (button.getFigureKind()) {
					case SCISSORS:
						button.setFigureKind(reverse ? null : FigureKind.ROCK);
						break;
					case ROCK:
						button.setFigureKind(reverse ? FigureKind.SCISSORS : FigureKind.PAPER);
						break;
					case PAPER:
						button.setFigureKind(reverse ? FigureKind.ROCK : FigureKind.TRAP);
						break;
					case TRAP:
						button.setFigureKind(reverse ? FigureKind.PAPER : FigureKind.FLAG);
						break;
					case FLAG:
						button.setFigureKind(reverse ? FigureKind.TRAP : null);
						break;
					default:
						button.setFigureKind(null);
						break;
					}
				}
			});
		}

		notification.show("Set your figures!", Color.BLUE);
	}
	
	public int getAmountOfSpecificFiguresOfPlayerOnBoard(FigureKind kind) {
		int amount = 0;
		 
		for (BoardFieldButton button: boardFields)
			if (button.getFigureKind() != null && button.isPlayersFigure() && button.getFigureKind().equals(kind))
				amount++;
		
		return amount;
		//FUUU
	}
	
	public FigureKind[] getAssignmentOfPlayer() {
		FigureKind[] assignment = new FigureKind[42];
		
		for (int i = 0; i<42; i++) {
			if (!boardFields[i].isPlayersFigure()) continue;
			assignment[i] = boardFields[i].getFigureKind();
		}
		
		return assignment;
	}
	
	public void showInitialChoicePane() {
		new InitialChoicePane(game, this, player, rootFrame);
		this.initialAssignmentAccept.setEnabled(false);
		this.randomAssignment.setEnabled(false);
	}
	
	public void showChoiceAfterFightIsDrawnPane () {
		new ChoiceAfterFightIsDrawnPane(game, this, player, rootFrame);
	}
	
	public void prepareBoardButtonsForGame() {
		boardPane.removeAll();
		
		for (int i = 0; i < 42; i++) {
			boardFields[i] = new BoardFieldButton(i);
			boardFields[i].setSize(20, 20);
			boardFields[i].setEnabled(true);
			boardPane.add(boardFields[i]);
		}
		
		for (int i = 0; i < 42; i ++) {
			
			boardFields[i].addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent action) {
					if (!provideMove) return;
					
					for (int i = 0; i < 42; i ++) {
						boardFields[i].setImage();
						
					}
					try {
						Move lastMove = game.getLastMove();
						
						if (lastMove != null)
							visualizeLastMove(lastMove);
						
						BoardFieldButton button = ((BoardFieldButton) action.getSource());
						
						if (button.hasMoveSource()) {
							int source = button.getMoveSource();
							int destination = button.getId();
							provideMove = false;
							game.move(player, source, destination);
							showAsStatus("Waiting for opponent move");

							for (int i = 0; i < 42; i ++) {
								boardFields[i].resetMoveSource();
							}
							
							return;
						}
						

						for (int i = 0; i < 42; i ++) {
							boardFields[i].resetMoveSource();
						}
					
						if (!button.isPlayersFigure()) return;

						Figure[] gameField = game.getField();
					
						//Nach links
						if (GameImpl.isMoveAllowed(gameField, ((BoardFieldButton) action.getSource()).getId(), -1, player)) {
							boardFields[button.getId()-1].showAsPossible(BoardFieldButton.MOVE_LEFT);
							boardFields[button.getId()-1].setMoveSource(button.getId());
						}
						//Nach rechts
						if (GameImpl.isMoveAllowed(gameField, ((BoardFieldButton) action.getSource()).getId(), +1, player)) {
							boardFields[button.getId()+1].showAsPossible(BoardFieldButton.MOVE_RIGHT);
							boardFields[button.getId()+1].setMoveSource(button.getId());
						}
						//Nach oben
						if (GameImpl.isMoveAllowed(gameField, ((BoardFieldButton) action.getSource()).getId(), -7, player)) {
							boardFields[button.getId()-7].showAsPossible(BoardFieldButton.MOVE_UP);
							boardFields[button.getId()-7].setMoveSource(button.getId());
						}
						//nach unten
						if (GameImpl.isMoveAllowed(gameField, ((BoardFieldButton) action.getSource()).getId(), +7, player)) {
							boardFields[button.getId()+7].showAsPossible(BoardFieldButton.MOVE_DOWN);
							boardFields[button.getId()+7].setMoveSource(button.getId());
						}
					}
					catch (RemoteException e) {
						e.printStackTrace();
					}
					
				}
			});
		}
	}
	
	public void updateBoard(Figure[] field) {
		
		for (int i = 0; i < 42; i ++) {
			if (field[i] == null) {
				this.boardFields[i].setIsPlayersFigure(false);
				this.boardFields[i].setFigureKind(null);
				this.boardFields[i].setHidden(true);
				continue;
			}
			
			this.boardFields[i].setIsPlayersFigure(field[i].belongsTo(player));
			// Versteckte Figuren brauchen keine Sonderbehandlung, das übernimmt bereits FigureHidingGame
			this.boardFields[i].setFigureKind(field[i].getKind());
			this.boardFields[i].setHidden(!field[i].isDiscovered());
		}
		
	}
	
	public void askForNextMove() throws RemoteException {
		this.provideMove = true;

		if (this.isFirstMove && game.getLastMove() == null) {
			notification.show("You start", Color.green);
		}
	}
	
	public void move() {
		this.isFirstMove = false;
	}
	
	public void showWinMessage() {
		notification.show("You win", Color.GREEN);
	}
	
	public void showLostMessage() {
		notification.show("You loose", Color.RED);
	}
	
	public void showDrawnMessage() {
		notification.show("Drawn!", new Color(255, 142, 17));
	}
	
	// Konvertiert einen Figurentyp zu einem String, der die entsprechende Bezeichnung enthält
	private String figureKindToString (FigureKind figureKind) {
		switch (figureKind) {
		case FLAG:
			return "FLAG";
		case HIDDEN:
			return "HIDDEN";
		case PAPER:
			return "PAPER";
		case ROCK:
			return "ROCK";
		case SCISSORS:
			return "SCISSORS";
		case TRAP:
			return "TRAP";
		default:
			return "";
		}
	}

	public void showAttackInChat(Move lastMove) throws RemoteException {
		Player attacker = lastMove.getOldField()[lastMove.getFrom()].belongsTo(this.player) ? this.player : this.opponentPlayer;
		Player attacked = attacker.equals(this.player) ? this.opponentPlayer : this.player;
		
		String formatted = "";
		formatted += figureKindToString(lastMove.getOldField()[lastMove.getFrom()].getKind());
		formatted += " (" + attacker.getNick() + ") attacked ";
		formatted += figureKindToString(lastMove.getOldField()[lastMove.getTo()].getKind());
		formatted += " (" + attacked.getNick() + ")\n";
		
		chat.append(formatted);
		chat.setCaretPosition(chat.getDocument().getLength());
	}

	public void visualizeLastMove(Move move) {
		int type = 0;
		
		if (move == null) return;
		
		switch (move.getTo()-move.getFrom()) {
		case 1:
			type = BoardFieldButton.MOVED_RIGHT;
			break;
		case -1:
			type = BoardFieldButton.MOVED_LEFT;
			break;
		case 7:
			type = BoardFieldButton.MOVED_DOWN;
			break;
		case -7:
			type = BoardFieldButton.MOVED_UP;
			break;
		}
		
		this.boardFields[move.getFrom()].showAsPossible(type);
	}
}