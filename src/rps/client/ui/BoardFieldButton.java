package rps.client.ui;

import rps.icons.Icons;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.border.LineBorder;

import rps.game.data.FigureKind;

public class BoardFieldButton extends JButton {
	private static final long serialVersionUID = 7411168296932813801L;
	
	private FigureKind figureKind = null;
	private boolean isPlayersFigure = false;
	private boolean isHidden;
	
	private int id;
	private int moveSource = -1;

	public static final int MOVE_LEFT = 1;
	public static final int MOVE_RIGHT = 2;
	public static final int MOVE_UP = 3;
	public static final int MOVE_DOWN = 4;
	public static final int MOVED_LEFT = 5;
	public static final int MOVED_RIGHT = 6;
	public static final int MOVED_UP = 7;
	public static final int MOVED_DOWN = 8;
	
	public BoardFieldButton(int id) {
		// Eigentlich nicht nötig, aber da die Buttons angepasst wurden, muss dieser Aufruf so gemacht werden
		super("empty");
		this.id=id;
		this.setBackground(Color.BLACK);
		this.setFocusPainted(false);
		this.setIcon(Icons.grass);
		this.setBorder(new LineBorder(Color.BLACK, 1));
		this.isHidden = true;
		
		
		
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// Wenn die Maus über dem Button liegt umrahmen wir den Button grün
				setBorder(new LineBorder(Color.GREEN, 2));
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// Wenn die Maus den Button verlässt, umrahmen wir sie schwarz bzw. rot wenn sie nicht hidden ist
				refreshBorder();
			}

			@Override
			public void mousePressed(MouseEvent arg0) {}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
		});
	}
	
	private void refreshBorder () {
		if (!isHidden && isPlayersFigure) {
			// Wenn die Figur entdeckt ist und dem Spieler gehört, umrahmen wir sie rot
			setBorder(new LineBorder(Color.RED, 2));
		} else {
			// Ansonsten genügt uns schwarz
			setBorder(new LineBorder(Color.BLACK, 1));
		}
	}
	
	public boolean isPlayersFigure() {
		return this.isPlayersFigure;
	}
	
	public void setIsPlayersFigure(boolean bool) {
		this.isPlayersFigure = bool;
	}
	
	public void setFigureKind(FigureKind kind) {
		this.figureKind = kind;
		this.setImage();
	}
	
	
	public int getId() {
		return id;
	}
	
	public FigureKind getFigureKind() {
		return this.figureKind;
	}
	
	public void setHidden(boolean hidden) {
		this.isHidden = hidden;
		this.refreshBorder();
	}
	
	public void setImage() {
		this.refreshBorder();

		if (this.figureKind == null) {
			this.setIcon(Icons.grass);
			this.revalidate();
			return;
		}
		
		if (this.isPlayersFigure) {
			switch (this.figureKind) {
			case SCISSORS:
				this.setIcon(Icons.scissors_white);
				break;
			case ROCK:
				this.setIcon(Icons.rock_white);
				break;
			case PAPER:
				this.setIcon(Icons.paper_white);
				break;
			case TRAP:
				this.setIcon(Icons.trap_white);
				break;
			case FLAG:
				this.setIcon(Icons.flag_white);
				break;
			case HIDDEN:
				// Eine eigene Figur kann eigentlich nie ein HIDDEN sein
				this.setIcon(null);
				break;
			}
		}
		else {
			switch (this.figureKind) {
			case SCISSORS:
				this.setIcon(Icons.scissors_black);
				break;
			case ROCK:
				this.setIcon(Icons.rock_black);
				break;
			case PAPER:
				this.setIcon(Icons.paper_black);
				break;
			case TRAP:
				this.setIcon(Icons.trap_black);
				break;
			case FLAG:
				this.setIcon(Icons.flag_black);
				break;
			case HIDDEN:
				this.setIcon(Icons.questionmark_black);
				break;
			}
		}
		this.revalidate();
	}
	
	public void showAsPossible(int type) {
		
		switch (type) {
		case MOVE_LEFT:
			this.setIcon(Icons.arrow_black_left);
			break;
		case MOVE_RIGHT:
			this.setIcon(Icons.arrow_black_right);
			break;
		case MOVE_UP:
			this.setIcon(Icons.arrow_black_up);
			break;
		case MOVE_DOWN:
			this.setIcon(Icons.arrow_black_down);
			break;
		case MOVED_LEFT:
			this.setIcon(Icons.arrow_red_left);
			break;
		case MOVED_RIGHT:
			this.setIcon(Icons.arrow_red_right);
			break;
		case MOVED_UP:
			this.setIcon(Icons.arrow_red_up);
			break;
		case MOVED_DOWN:
			this.setIcon(Icons.arrow_red_down);
			break;
		}

		this.revalidate();
	}
	
	public void setMoveSource(int source) {
		this.moveSource = source;
	}
	
	public void resetMoveSource() {
		this.moveSource = -1;
	}
	
	public boolean hasMoveSource() {
		return this.moveSource != -1;
	}
	
	public int getMoveSource() {
		return this.moveSource;
	}

	
}
