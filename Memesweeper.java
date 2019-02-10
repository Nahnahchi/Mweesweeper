import javax.swing.plaf.ColorUIResource;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

class Field {

	static final File data = new File("data");
	static final Timer timer = new Timer();
	
	private final JPanel gui;
	private final JLabel message;
	private final JButton[][] field;
	private final ArrayList<int[]> mines;	
	private static JLabel timeOnTimer;
	private ImageIcon flagIcon, doubtIcon, mweeIcon;
	private int cellsLeft, width, height, count;
	private boolean actionsAllowed;
	
	static void updateTimer(String newTime) {
		timeOnTimer.setText(newTime);
	}
	
	static void resetTimer() {
		timer.setRunning(false);
		timer.setCount(0);
	}

	JPanel getGui() {
		return gui;
	}

	int getHeight() {
		return height;
	}

	int getWidth() {
		return width;
	}

	int getCount() {
		return count;
	}

	private enum Mine {

		ROW(0), COL(1);

		private static String mine = "kys";
		private final int ind;

		private Mine(int ind) {
			this.ind = ind;
		}

		int ind() {
			return ind;
		}

		static String msg() {
			return mine;
		}

	}

	Field(int height, int width, int count) {
		
		this.gui = new JPanel(new BorderLayout(3, 3));
		this.height = height;
		this.width = width;
		this.count = count;
		this.cellsLeft = width * height;
		this.message = new JLabel(Mine.msg() + ": x" + count);
		this.field = new JButton[height][width];
		this.mines = new ArrayList<int[]>(count);
		this.actionsAllowed = true;

		startGame();
		
	}

	private void newGame() {
		
		gui.setVisible(false);
		actionsAllowed = true;
		cellsLeft = height * width;
		close(height / 2, width / 2);
		mines.clear();
		placeMines();
		message.setText(Mine.msg() + ": x" + count);
		resetTimer();
		gui.setVisible(true);

	}

	private void gameComplete(boolean win) {

		actionsAllowed = false;
		ImageIcon ic;
		Font fn;
		Color cl;

		timer.setRunning(false);
		
		if (win) {
			ic = mweeIcon;
			fn = new Font(null, 0, 0);
			cl = Color.PINK;
		} else {
			ic = null;
			fn = new Font("Arial", 20, 20);
			cl = Color.RED;
		}

		gui.setVisible(false);

		for (int i = 0; i < mines.size(); i++) {

			int[] cell = mines.get(i);
			int r = cell[Mine.ROW.ind()];
			int c = cell[Mine.COL.ind()];

			field[r][c].setBackground(cl);

			if (ic != null || field[r][c].getIcon() == null) {
				field[r][c].setIcon(ic);
				field[r][c].setFont(fn);
			}

		}
		
		gui.setVisible(true);

	}

	private void markCell(JButton btn) {

		int left = Integer.parseInt(message.getText().substring(Mine.msg().length() + 3));

		if (btn.getIcon() == null && left > 0) {
			btn.setIcon(flagIcon);
			left--;
		} else if (btn.getIcon() == flagIcon) {
			btn.setIcon(doubtIcon);
			left++;
		} else {
			btn.setIcon(null);
			return;
		}

		message.setText(Mine.msg() + ": x" + left);

	}

	private Image getScaledImage(Image srcImg, int w, int h) {

		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = resizedImg.createGraphics();

		g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2d.drawImage(srcImg, 0, 0, w, h, null);
		g2d.dispose();

		return resizedImg;

	}

	private boolean hasMine(int row, int col) {
		return field[row][col].getText().equals(Mine.msg());
	}

	private int mineAt(int row, int col) {
		if (row >= 0 && col >= 0 && row < height && col < width) {
			return hasMine(row, col) ? 1 : 0;
		} else {
			return 0;
		}
	}

	private String minesNearby(int row, int col) {

		int mns = mineAt(row - 1, col - 1);

		mns += mineAt(row - 1, col);
		mns += mineAt(row - 1, col + 1);
		mns += mineAt(row, col - 1);
		mns += mineAt(row, col + 1);
		mns += mineAt(row + 1, col - 1);
		mns += mineAt(row + 1, col);
		mns += mineAt(row + 1, col + 1);

		return String.valueOf(mns);

	}

	private void placeMines() {

		Random ran = new Random();

		for (int i = 0; i < count; i++) {

			while (true) {

				int r = ran.nextInt(height);
				int c = ran.nextInt(width);

				if (!hasMine(r, c)) {
					field[r][c].setText(Mine.msg());
					mines.add(new int[] { r, c });
					break;
				}

			}

		}

		for (int r = 0; r < height; r++) {
			for (int c = 0; c < width; c++) {
				if (!hasMine(r, c)) {
					field[r][c].setText(minesNearby(r, c));
				}
			}
		}

	}

	private void leftClick(int row, int col) {

		gui.setVisible(false);

		if (cellsLeft == width * height) {
			timer.setRunning(true);
		}
		
		open(row, col);
		
		if (cellsLeft == count) {

			message.setText(Mine.msg() + ": x0");
			gameComplete(true);
			Memesweeper.updateResults(timeOnTimer.getText(), timer.getCount());
			gui.setVisible(true);

			int game = JOptionPane.showOptionDialog(gui, "Start a new game?", "Game Complete",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);

			if (game == JOptionPane.YES_OPTION) {
				newGame();
			}

			return;

		}

		gui.setVisible(true);

	}

	private void open(int row, int col) {

		if (row < 0 || row >= height || col < 0 || col >= width) {

			return;

		} else if (field[row][col].isEnabled()) {

			int minesNear = Integer.parseInt(field[row][col].getText());

			if (field[row][col].getIcon() == flagIcon) {
				markCell(field[row][col]);
			}

			field[row][col].setEnabled(false);
			cellsLeft--;
			field[row][col].setIcon(null);

			if (minesNear == 0) {
				open(row - 1, col);
				open(row - 1, col - 1);
				open(row - 1, col + 1);
				open(row, col - 1);
				open(row, col + 1);
				open(row + 1, col);
				open(row + 1, col - 1);
				open(row + 1, col + 1);
				return;
			}

			field[row][col].setFont(new Font("Arial", 20, 20));

		}

	}

	private void close(int row, int col) {

		if (row < 0 || row >= height || col < 0 || col >= width) {

			return;

		} else if (!field[row][col].getText().equals("")) {

			field[row][col].setBackground(null);
			field[row][col].setText("");

			close(row - 1, col);
			close(row, col - 1);
			close(row, col + 1);
			close(row + 1, col);

			field[row][col].setEnabled(true);
			field[row][col].setFont(new Font(null, 0, 0));
			field[row][col].setIcon(null);

		}

	}

	private void startGame() {

		File icon = new File(data, "icon");
		icon.mkdirs();
		try {
			flagIcon = new ImageIcon(getScaledImage(ImageIO.read(new File(icon, "flag.bmp")), 30, 30));
		} catch (IOException e) {
			new JOptionPane(e.getMessage(), JOptionPane.ERROR_MESSAGE).createDialog("Error").setVisible(true);
		} 
		try {
			doubtIcon = new ImageIcon(getScaledImage(ImageIO.read(new File(icon, "qmark.bmp")), 30, 30));
		} catch (IOException e) {
			new JOptionPane(e.getMessage(), JOptionPane.ERROR_MESSAGE).createDialog("Error").setVisible(true);
		} 
		try {
			mweeIcon = new ImageIcon(getScaledImage(ImageIO.read(new File(icon, "mwee.bmp")), 34, 34));
		} catch (IOException e) {
			new JOptionPane(e.getMessage(), JOptionPane.ERROR_MESSAGE).createDialog("Error").setVisible(true);
		}

		gui.setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel board = new JPanel(new GridLayout(height, width));
		JPanel extraNG = new JPanel();
		JPanel bottom = new JPanel();
		JButton mweeNG = new JButton();

		mweeNG.setPreferredSize(new Dimension(35, 35));
		mweeNG.setBackground(Color.WHITE);
		mweeNG.setIcon(mweeIcon);

		mweeNG.addActionListener((ActionEvent e) -> {
			newGame();
			//Memesweeper.restart(height, width, count);
		});

		extraNG.add(mweeNG);
		gui.add(extraNG, BorderLayout.NORTH);
		board.setBorder(new LineBorder(Color.BLACK));
		gui.add(board);
		bottom.add(message);
		bottom.add(new JLabel("           "));
		
		timeOnTimer = new JLabel();
		resetTimer();
		
		bottom.add(timeOnTimer);
		gui.add(bottom, BorderLayout.SOUTH);

		JPopupMenu menu = new JPopupMenu();

		JMenuItem newGame = new JMenuItem("Новая Игра");
		newGame.addActionListener((ActionEvent e) -> {
			newGame();
		});
		
		JMenuItem bestResult = new JMenuItem("Лучший Результат");
		bestResult.addActionListener((ActionEvent e) -> {
			new JOptionPane(Memesweeper.res.getTime(), JOptionPane.PLAIN_MESSAGE).createDialog("Лучший Результат").setVisible(true);
		});

		JMenuItem options = new JMenuItem("Настройки");
		options.addActionListener((ActionEvent e) -> {
			Memesweeper.options(this);
		});

		JMenuItem exitGame = new JMenuItem("Выход");
		exitGame.addActionListener((ActionEvent e) -> {
			JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(gui);
			frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		});

		menu.add(newGame);
		menu.addSeparator();
		menu.add(bestResult);
		menu.addSeparator();
		menu.add(options);
		menu.addSeparator();
		menu.add(exitGame);

		gui.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isRightMouseButton(e)) {
					menu.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});

		Insets buttonMargin = new Insets(0, 0, 0, 0);
		for (int row = 0; row < height; row++) {
			for (int col = 0; col < width; col++) {

				final int r = row, c = col;

				JButton btn = new JButton();
				btn.setFont(new Font(null, 0, 0));
				btn.setMargin(buttonMargin);
				btn.setPreferredSize(new Dimension(40, 40));
				btn.setBorder(new LineBorder(Color.BLACK));

				btn.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseReleased(MouseEvent arg0) {
						
						if (actionsAllowed) {

							if (SwingUtilities.isRightMouseButton(arg0) && btn.isEnabled()) {

								markCell(btn);

							} else if (SwingUtilities.isLeftMouseButton(arg0)) {

								if (btn.getText().equals(Mine.msg())) {
									gameComplete(false);
								} else {
									leftClick(r, c);
								}

							}

						}

					}
				});

				field[row][col] = btn;
				board.add(field[row][col]);

			}
		}

		placeMines();
		
	}

}

class Stat implements Serializable {
	
	private static final long serialVersionUID = -4111334602500991215L;
	private String time;
	private int count;
	
	Stat() {
		this.time = "--:--:--";
		this.count = Integer.MAX_VALUE;
	}

	Stat(String time, int count) {
		this.time = time;
		this.count = count;
	}
		
	int getCount() {
		return count;
	}
	
	String getTime() {
		return time;
	}
	
}

class Timer implements Runnable {

	private final SimpleDateFormat sdf;
	private final Calendar cl;
	private final long timeOut;
	private boolean toCount;
	private boolean toStop;
	private int count;

	Timer() {

		this.sdf = new SimpleDateFormat("HH:mm:ss");
		this.cl = Calendar.getInstance();
		this.timeOut = 1000;
		this.toCount = false;
		this.toStop = false;
		this.count = 0;

	}

	synchronized void setRunning(boolean toCount) {
		if (this.toCount = toCount) {
			notify();
		}
	}
	
	void stop() {
		toStop = true;
	}
	
	int getCount() {
		return count;
	}

	void setCount(int count) {		
		try {
			cl.setTime(sdf.parse("00:00:00"));
			cl.add(Calendar.SECOND, this.count = count);	
			Field.updateTimer(sdf.format(cl.getTime()));
		} catch (ParseException e) {
			new JOptionPane(e.getMessage(), JOptionPane.ERROR_MESSAGE).createDialog("Error").setVisible(true);
		}	
	}

	@Override
	public void run() {

		try {
			
			synchronized (this) {
				
				if (toStop) {
					
					toStop = false;
				
				} else {
					
					if (!toCount) {
						
						wait();
					
					} else {
					
						cl.add(Calendar.SECOND, 1);
						Field.updateTimer(sdf.format(cl.getTime()));
						count++;
						wait(timeOut);	
				
					}
					
					run();
				
				}
				
			}
			
		} catch (InterruptedException e) {
			
			new JOptionPane(e.getMessage(), JOptionPane.ERROR_MESSAGE).createDialog("Error").setVisible(true);
		
		}

	}

}

public class Memesweeper {
	
	static JFrame fr;
	static OptionDialog opt;
	static File savefile;
	static Stat res;
	
	static enum Source {

		HEIGHT(0), WIDTH(1), COUNT(2), RESULT(3);

		private final int ind;

		private Source(int ind) {
			this.ind = ind;
		}

		int index() {
			return ind;
		}

		static Object getValue(File savefile, Source source) {
			Object[] sourceValues = (Object[]) loadFrom(savefile);
			return sourceValues[source.index()];
		}

	}

	static void save(Object b, File f) {
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f))) {
			oos.writeObject(b);
		} catch (Exception e) {
			new JOptionPane(e.getMessage(), JOptionPane.ERROR_MESSAGE).createDialog("Error").setVisible(true);
		}
	}

	static Object loadFrom(File f) {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
			return ois.readObject();
		} catch (Exception ex) {
			return null;
		}
	}

	static void init(int h, int w, int c) {

		fr = new JFrame("Memesweeper");
		Field minesweeper = new Field(h, w, c);
						
		fr.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {

				save(new Object[] { 
						minesweeper.getHeight(), 
						minesweeper.getWidth(), 
						minesweeper.getCount(), res
				}, savefile);
				
				System.exit(0);

			}

		});

		fr.add(minesweeper.getGui());
		fr.setLocationByPlatform(true);
		fr.pack();
		fr.setVisible(true);
		
		Field.timer.run();
		
	}
	
	/*static void restart(int h, int w, int c) {
		frameReset();
		init(h, w, c);
	}*/

	static void options(Field fld) {
		opt = new OptionDialog(fld);
	}

	static void frameReset() {
		Field.timer.stop();
		fr.dispose();
	}
	
	static void updateResults(String time, int count) {
		if (count < res.getCount()) {
			res = new Stat(time, count);
		}
	}

	public static void main(String[] args) throws IOException {

		UIManager.put("Button.disabledText", new ColorUIResource(Color.BLACK));
				
		int h, w, c;
		
		File save = new File(Field.data, "save");
		save.mkdirs();
		savefile = new File(save, "savefile.dat");
		
		if (!savefile.exists()) {
			savefile.createNewFile();
		}
		
		try {
			res = (Stat) Source.getValue(savefile, Source.RESULT);
		} catch (Exception e) {
			res = new Stat();
		}

		try {
			h = (int) Source.getValue(savefile, Source.HEIGHT);
			w = (int) Source.getValue(savefile, Source.WIDTH);
			c = (int) Source.getValue(savefile, Source.COUNT);
		} catch (Exception e) {
			h = 13;
			w = 13;
			c = 25;
		}

		init(h, w, c);

	}

}
