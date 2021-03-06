import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.Timer;

//the JPanel with the game in it.
@SuppressWarnings("serial")
public class GamePanel extends JPanel{

	private CanvasPanel canvas;
	private int score, xpos, ypos;
	private Snake snek;
	private FoodDot dot;
	private boolean gameStart, gameOver, makingMove;
	private Timer timer;
	public static final int UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3;
	public int direction;
	private boolean robotMode;
	
	public GamePanel(int choice) {
		setup();
		
		//check if it's robot mode
		if(choice == 1)
			robotMode = true;
		else
			robotMode = false;
		
		//set up the key listener
		canvas = new CanvasPanel();	
		addKeyListener(new ArrowListener());
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		
		//put it all together
		setLayout(new BorderLayout());
		add(canvas, BorderLayout.CENTER);
	}
	
	//initial settings
	public void setup(){
		//snake start position
		xpos = 290;
		ypos = 290;
		snek = new Snake(xpos, ypos);
		dot = new FoodDot(xpos + 50, ypos);
		direction = 0;
		makingMove = false;
				
		//score info
		score = 0;
		gameOver = false;
				
		//set up timer
		gameStart = false;
		timer = new Timer(60, new TimerListener());
	}
	
	//panel where everything will show up
	private class CanvasPanel extends JPanel
	{
		public void paintComponent(Graphics page){
			super.paintComponent(page);
			setBackground(Color.BLACK);
			
			//set score label
			page.setFont(new Font(Font.SERIF, Font.BOLD, 25));
			page.setColor(Color.WHITE);
			page.drawString("Score: " + score, 40, 30);
			
			//robot mode label
			if(robotMode)
				page.drawString("Robot Mode", 235, 30);
			
			//bounds
			page.drawRect(40, 40, 500, 500);
			
			//draw the snake
			snek.draw(page);
			
			//draw the dot
			dot.draw(page);
			
			//show it game over if it is
			if(gameOver){
				page.setFont(new Font(Font.SERIF, Font.BOLD, 50));
				page.setColor(Color.RED);
				page.drawString("GAME OVER", 145, 250);
			}
		}
	}
	
	public boolean hasEaten(){
		if(dot.getX() == snek.getX() && dot.getY() == snek.getY())
			return true;
		else
			return false;
	}
	
	public Point getFoodDotLoc(){
		Point p = new Point(dot.getX(), dot.getY());
		return p;
	}
	
	public Point getSnekHeadLoc(){
		Point p = new Point(xpos, ypos);
		return p;
	}
	
	public int getDirection(){
		return direction;
	}
	
	public boolean isGameOver(){
		return gameOver;
	}
	
	public boolean isSnekPoint(Point p){
		ArrayList<Point> snekPoints = snek.getTail();
		return snekPoints.contains(p);
	}
	
	private class ArrowListener implements KeyListener{

		@Override
		public void keyPressed(KeyEvent key) {			
			if(!gameOver && !makingMove){	
				//change the snake's direction
				if(key.getKeyCode() == KeyEvent.VK_UP && ((direction != DOWN && direction != UP)|| snek.getTailLength() == 0)){	
					//System.out.println("up");
					direction = UP;
					makingMove = true;
				}
				else if(key.getKeyCode() == KeyEvent.VK_DOWN && ((direction != DOWN && direction != UP) || snek.getTailLength() == 0)){
					//System.out.println("down");
					direction = DOWN;
					makingMove = true;
				}
				else if(key.getKeyCode() == KeyEvent.VK_LEFT && ((direction != RIGHT && direction != LEFT) || snek.getTailLength() == 0)){
					//System.out.println("left");
					direction = LEFT;
					makingMove = true;
				}
				else if(key.getKeyCode() == KeyEvent.VK_RIGHT && ((direction != RIGHT && direction != LEFT) || snek.getTailLength() == 0)){
					//System.out.println("right");
					direction = RIGHT;
					makingMove = true;
				}
				
				//start the timer if the game has begun yet
				if(!gameStart){
					gameStart = true;
					timer.start();
				}			
			}
			//if the game's over and you press space, restart
			else if(key.getKeyCode() == KeyEvent.VK_SPACE){
				setup();				
				repaint();
			}

		}

		@Override
		public void keyReleased(KeyEvent arg0) {
			// TODO Auto-generated method stub
		}

		@Override
		public void keyTyped(KeyEvent arg0) {
			// TODO Auto-generated method stub

		}
		
	}
	
	private class TimerListener implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			//change snake's makeup
			snek.addToTail(xpos, ypos);	
			
			//move the snake
			if(direction == UP){					
				if(ypos > 40)
					ypos-=10;
			}
			else if(direction == DOWN){
				if(ypos < 530)
					ypos+=10;
			}
			else if(direction == LEFT){
				if(xpos > 40)
					xpos-=10;
			}
			else if(direction == RIGHT){
				if(xpos < 530)
					xpos+=10;
			}
			snek.setCoords(xpos, ypos);
			
			if(snek.touchedSelf())
				gameOver = true;
			
			//if the snake has eaten, then move the dot and don't chop off end
			if(hasEaten()){
				score++;
				dot.changePos(xpos, ypos, snek.getTail());
			}
			else
				snek.chopOffEnd();

			//stop the timer if gameOver
			if(gameOver)
				timer.stop();
			
			
			makingMove = false;
			//repaint 
			repaint();
		}
	
	}
}
