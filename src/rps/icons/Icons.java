package rps.icons;

import javax.swing.ImageIcon;

// Diese Klasse kümmert sich nur um das Bereitstellen der Icons
public class Icons {
	// Das Hintergrundicon
	public final static ImageIcon grass = new ImageIcon(Icons.class.getResource("/rps/icons/grass.png"));
	
	// Versteckte Figuren werden mit diesem Icon angezeigt
	public final static ImageIcon questionmark_black = new ImageIcon(Icons.class.getResource("/rps/icons/questionmark.png"));
	
	// Die Figurentypen in den beiden Farben
	public final static ImageIcon trap_black = new ImageIcon(Icons.class.getResource("/rps/icons/trap_black.png"));
	public final static ImageIcon trap_white = new ImageIcon(Icons.class.getResource("/rps/icons/trap_white.png"));
	public final static ImageIcon flag_black = new ImageIcon(Icons.class.getResource("/rps/icons/flag_black.png"));
	public final static ImageIcon flag_white = new ImageIcon(Icons.class.getResource("/rps/icons/flag_white.png"));
	public final static ImageIcon paper_black = new ImageIcon(Icons.class.getResource("/rps/icons/paper_black.png"));
	public final static ImageIcon paper_white = new ImageIcon(Icons.class.getResource("/rps/icons/paper_white.png"));
	public final static ImageIcon rock_black = new ImageIcon(Icons.class.getResource("/rps/icons/rock_black.png"));
	public final static ImageIcon rock_white = new ImageIcon(Icons.class.getResource("/rps/icons/rock_white.png"));
	public final static ImageIcon scissors_black = new ImageIcon(Icons.class.getResource("/rps/icons/scissors_black.png"));
	public final static ImageIcon scissors_white = new ImageIcon(Icons.class.getResource("/rps/icons/scissors_white.png"));
	
	// Die Pfeile die die möglichen Züge anzeigen
	public final static ImageIcon arrow_black_up = new ImageIcon(Icons.class.getResource("/rps/icons/arrow_black_up.png"));
	public final static ImageIcon arrow_black_down = new ImageIcon(Icons.class.getResource("/rps/icons/arrow_black_down.png"));
	public final static ImageIcon arrow_black_left = new ImageIcon(Icons.class.getResource("/rps/icons/arrow_black_left.png"));
	public final static ImageIcon arrow_black_right = new ImageIcon(Icons.class.getResource("/rps/icons/arrow_black_right.png"));
	
	// Die Pfeile die den letzten gegnerischen Zug anzeigen
	public final static ImageIcon arrow_red_up = new ImageIcon(Icons.class.getResource("/rps/icons/arrow_red_up.png"));
	public final static ImageIcon arrow_red_down = new ImageIcon(Icons.class.getResource("/rps/icons/arrow_red_down.png"));
	public final static ImageIcon arrow_red_left = new ImageIcon(Icons.class.getResource("/rps/icons/arrow_red_left.png"));
	public final static ImageIcon arrow_red_right = new ImageIcon(Icons.class.getResource("/rps/icons/arrow_red_right.png"));
	
	// Icons für die zwei Buttons auf der rechten Seite
	public final static ImageIcon arrow_random = new ImageIcon(Icons.class.getResource("/rps/icons/arrow_random.png"));
	public final static ImageIcon start = new ImageIcon(Icons.class.getResource("/rps/icons/arrow_random.png"));
}
