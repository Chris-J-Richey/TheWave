
//Programmer Chris Richey
//date: 2/
//Main Game

import java.applet.*;

import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.text.*;

public class Game extends JApplet implements KeyListener, ActionListener{
	JPanel pnlMain = new JPanel();
	Image ManImage;
	Image HeartImage;
	int arrows = 0;
	int Health = 100;
	int foot = 1;
	int Ydirection = 1;
	int jump = 0;
	int Kills = 0;
	int Ground = 350;
	int xManInt = 463;
	int yManInt = 275;
	int Shot = 0;
	int Timecount = 0;
	int direction = 1;
	int weapon = 0;
	int active = 0;
	boolean left = false;
	boolean right = false;
	Timer myTimer = new Timer(100,this);
	ArrayList<Weapon> Weapon = new ArrayList<Weapon>();
	ArrayList<Arrow> Arrow = new ArrayList<Arrow>();
	ArrayList<Crate> Crate = new ArrayList<Crate>();
	ArrayList<Zombie> Zombie = new ArrayList<Zombie>();
	Font normFont = new Font("TimesRoman", Font.PLAIN, 36);
	Font Game = new Font("TimesRoman", Font.BOLD, 72);
	Font Pause = new Font("TimesRoman", Font.PLAIN, 36);
	boolean PLAY = false;
	// Declare buffer variables (probably globally)
	private Graphics2D buffer;
	private Image offscreen;
	
	public void init(){
		myTimer.start();
		ManImage = getImage(getDocumentBase(),"ManRight2.png");

		//make crate
		for(int i = 0; i < 7; i++){
			int temp = (int) (Math.random()*1000);
			while(temp > 400 && temp < 600){
				temp = (int) (Math.random()*1000);
			}
			for(int x = 0; x < Crate.size(); x++){
				if(Crate.get(x).xDist-25 < temp && Crate.get(x).xDist+25 > temp){
					temp = (int) (Math.random()*1000);
				}
			}
			Crate.add(new Crate());
			Crate.get(i).CrateImage = getImage(getDocumentBase(),"crate.png");
			Crate.get(i).setCrate(temp, 325);
		}
		//make zombies
		for(int i = 0; i < 10; i++){
			Zombie.add(new Zombie());
			Zombie.get(i).ZombieImage = getImage(getDocumentBase(),"ZLeft2.png");
			int tempZ = (int) (Math.random()*2);
			if(tempZ == 0){
				Zombie.get(i).xDist = (int) (Math.random()*-500 - 45);
			}
			if(tempZ == 1){
				Zombie.get(i).xDist = (int) (Math.random()*500) + 1000;
			}
		}
		//makes weapons
		setWeapons();
		
		setContentPane(pnlMain);
		resize(1000,400);
		
		addKeyListener(this);
		setFocusable(true);
	}
	public void actionPerformed(ActionEvent evt){
		requestFocus();
		
		if(PLAY){
			if(Health > 0){
				if(active > 0){
					active--;
				}
				//pck up weapon
				PickUp();
				//check to see if zombie is dead
				kill();
				//check to see if zombie hits player
				ZHitM();
				//check to see if player hits zombie
				Attack();
				//moves player
				Move();
				//moves zombie
				ZombieMove();
				//moves Arrow
				MoveArrow();
				MoveArrow();
				
				repaint();
			}
		}
	}
	public void keyPressed(KeyEvent e){
		int key = e.getKeyCode();
		if (key == 10){
			PLAY = true;
		}
		if (key == 82){
			if(Health <= 0){
				newGame();
			}
		}
		if (key == 80){
			if(!PLAY){
				PLAY = true;
			}
			else if(PLAY){
				PLAY = false;
			}
		}
		if(PLAY){
			if (key == 37){
				left = true;
			}
			else if (key == 39){
				right = true;
			}
			else if (key == 38){
				if(jump == 0){
					jump = 10;
				}
			}
			else if (key == 32){
				if(active == 0){
					active = 5;
				}
				if(weapon == 3){
					MakeArrow();
				}
			}
		}
	}
	public void keyReleased(KeyEvent e){
		int key = e.getKeyCode();
		if (key == 37){
			left = false;
		}
		else if (key == 39){
			right = false;
		}
	}
	public void keyTyped(KeyEvent e){
	}
	public void paint(Graphics g){
		// At initialization, create buffer
		offscreen = createImage(getSize().width, getSize().height);
		buffer = (Graphics2D)offscreen.getGraphics();
		super.paint(g);
		Image bk = getImage(getDocumentBase(),"Bk3.png");
		buffer.drawImage(bk, 0, 0, this);
		for (int i = 0; i < Crate.size(); i++) {
			buffer.drawImage(Crate.get(i).CrateImage, Crate.get(i).xDist, Crate.get(i).yDist, 25, 25, this);
		}
		for (int z = 0; z < Zombie.size(); z++) {
			if(Zombie.get(z).Health > 0){
				buffer.drawImage(Zombie.get(z).ZombieImage, Zombie.get(z).xDist, Zombie.get(z).yDist, 45, 75, this);
			}
		}
		buffer.drawImage(ManImage, xManInt, yManInt, 45, 75, this);
		for (int z = 0; z < Weapon.size(); z++) {
			buffer.drawImage(Weapon.get(z).WeaponImage, Weapon.get(z).xDist, Weapon.get(z).yDist, Weapon.get(z).Width, Weapon.get(z).Height, this);
		}
		for (int z = 0; z < Arrow.size(); z++) {
			buffer.drawImage(Arrow.get(z).ArrowImage, Arrow.get(z).xDist, Arrow.get(z).yDist, Arrow.get(z).Width, Arrow.get(z).Height, this);
		}
		buffer.drawImage(HeartImage, 50, 25, 50, 50, this);
		buffer.setFont(normFont);
		if(Health >= 0){
			buffer.drawString("Health: " + Health, 170, 60);
		}
		buffer.drawString("Arrows: " + arrows, 370, 60);
		buffer.drawString("Kills: " + Kills, 570, 60);
		if(Health <= 0){
			buffer.setFont(Game);
			buffer.drawString("GAME OVER ", 200, 200);
			buffer.setFont(normFont);
			buffer.drawString("Press \"R\" to reset", 275, 135);
		}
		if(!PLAY && Health > 0){
			buffer.setFont(Pause);
			buffer.drawString("-Left and Right arrow keys move side to side", 10, 125);
			buffer.drawString("-Space to attack", 10, 175);
			buffer.drawString("-Up arrow key to jump", 10, 225);
			buffer.drawString("-\"P\" to Pause", 10, 275);
			buffer.drawString("-Press Enter to Begin", 10, 325);
		}
		Image logo = getImage(getDocumentBase(),"Logo.png");
		buffer.drawImage(logo, 800, 50, this);
		g.drawImage(offscreen, 0, 0, this);
	}
	public void Update(Graphics gr){
		paint(gr);
	}
	public void ZHitM(){
		//Zombie it man
		for (int i = 0; i < Zombie.size(); i++){
			Rectangle rect1 = new Rectangle(xManInt, yManInt, 45, 75);
			Rectangle rect2 = new Rectangle(Zombie.get(i).xDist, Zombie.get(i).yDist, 45, 75);
			if(rect1.intersects(rect2)){
				Health -= 5;
			}
		}
		if(Health == 0){
			PLAY = false;
		}
		if(Health < 0){
			Health = 0;
		}
		//health image
		if(Health > 0){
			HeartImage = getImage(getDocumentBase(),"0.png");
		}
		if(Health > 10){
			HeartImage = getImage(getDocumentBase(),"10.png");
		}
		if(Health > 20){
			HeartImage = getImage(getDocumentBase(),"20.png");
		}
		if(Health > 30){
			HeartImage = getImage(getDocumentBase(),"30.png");
		}
		if(Health > 40){
			HeartImage = getImage(getDocumentBase(),"40.png");
		}
		if(Health > 50){
			HeartImage = getImage(getDocumentBase(),"50.png");
		}
		if(Health > 60){
			HeartImage = getImage(getDocumentBase(),"60.png");
		}
		if(Health > 70){
			HeartImage = getImage(getDocumentBase(),"70.png");
		}
		if(Health > 80){
			HeartImage = getImage(getDocumentBase(),"80.png");
		}
		if(Health > 90){
			HeartImage = getImage(getDocumentBase(),"90.png");
		}
		if(Health >= 100){
			HeartImage = getImage(getDocumentBase(),"100.png");
		}
	}
	public void Attack(){
		if(active > 0){
			if(weapon == 0){
				//punch
				for (int i = 0; i < Zombie.size(); i++){
					Rectangle rect1 = new Rectangle(Weapon.get(weapon).xDist, Weapon.get(weapon).yDist, Weapon.get(weapon).Width, Weapon.get(weapon).Height);
					Rectangle rect2 = new Rectangle(Zombie.get(i).xDist, Zombie.get(i).yDist, 45, 75);
					if(rect1.intersects(rect2)){
						Zombie.get(i).Health -= 50;
						Health -= 2;
					}
				}
			}
			if(weapon == 1){
				//sword
				for (int i = 0; i < Zombie.size(); i++){
					Rectangle rect1 = new Rectangle(Weapon.get(weapon).xDist, Weapon.get(weapon).yDist, Weapon.get(weapon).Width, Weapon.get(weapon).Height);
					Rectangle rect2 = new Rectangle(Zombie.get(i).xDist, Zombie.get(i).yDist, 45, 75);
					if(rect1.intersects(rect2)){
						Zombie.get(i).Health -= 100;
					}
				}
			}
		}
		if(Arrow.size() > 0){
			//bow
			for (int i = 0; i < Zombie.size(); i++){
				for (int a = 0; a < Arrow.size(); a++){
					Rectangle rect1 = new Rectangle(Arrow.get(a).xDist, Arrow.get(a).yDist, Arrow.get(a).Width, Arrow.get(a).Height);
					Rectangle rect2 = new Rectangle(Zombie.get(i).xDist, Zombie.get(i).yDist, 45, 75);
					if(rect1.intersects(rect2)){
						Zombie.get(i).Health -= 100;
						Arrow.remove(a);
					}
				}
			}
		}
	}
	public void kill(){
		//check zombie health
		for (int i = 0; i < Zombie.size(); i++){
			if(Zombie.get(i).Health <= 0){
				Kills++;
				reset(i);
				if(Kills%3 == 0){
					Zombie.add(new Zombie());
					Zombie.get(i).ZombieImage = getImage(getDocumentBase(),"ZLeft2.png");
					int tempZ = (int) (Math.random()*2);
					if(tempZ == 0){
						Zombie.get(i).xDist = (int) (Math.random()*-500 - 75);
					}
					if(tempZ == 1){
						Zombie.get(i).xDist = (int) (Math.random()*500) + 1000;
					}
				}
			}
		}
	}
	public void PickUp(){
		for (int i = 0; i < Weapon.size(); i++){
			if(i != weapon && i > 0){
				//check if pick up
				Rectangle rect1 = new Rectangle(xManInt, yManInt, 45, 75);
				Rectangle rect2 = new Rectangle(Weapon.get(i).xDist, Weapon.get(i).yDist, Weapon.get(i).Width, Weapon.get(i).Height);
				if(rect1.intersects(rect2)){
					//sword
					if(i == 1){
						Weapon.get(weapon).xDist = -100;
						Weapon.get(weapon).yDist = -100;
						weapon = i;
					}
					//food
					if(i == 2){
						Weapon.get(i).xDist = -100;
						Weapon.get(i).yDist = -100;
						Health += 25;
						if(Health > 100){
							Health = 100;
						}
					}
					//bow
					if(i == 3){
						Weapon.get(weapon).xDist = -100;
						Weapon.get(weapon).yDist = -100;
						weapon = i;
						arrows += 10;
					}
					//arrow
					if(i == 4){
						arrows += 10;
						Weapon.get(i).xDist = -100;
						Weapon.get(i).yDist = -100;
					}
				}
			}
		}
	}
	public void setWeapons(){
		//creates items
		for(int i = 0; i < 5; i++){
			Weapon.add(new Weapon());
			if (i == 0){
				Weapon.get(i).WeaponImage = getImage(getDocumentBase(),"fistR.png");
				Weapon.get(i).setWeapon(-100, -100, 15, 10);
			}
			else if (i == 1){
				Weapon.get(i).WeaponImage = getImage(getDocumentBase(),"swordR.png");
				Weapon.get(i).setWeapon(-100, -100, 45, 15);
			}
			else if (i == 2){
				Weapon.get(i).WeaponImage = getImage(getDocumentBase(),"food.png");
				Weapon.get(i).setWeapon(-100, -100, 15, 15);
			}
			else if (i == 3){
				Weapon.get(i).WeaponImage = getImage(getDocumentBase(),"bowR.png");
				Weapon.get(i).setWeapon(-100, -100, 15, 45);
			}
			else if (i == 4){
				Weapon.get(i).WeaponImage = getImage(getDocumentBase(),"arrowR.png");
				Weapon.get(i).setWeapon(-100, -100, 15, 5);
			}
		}
	}
	public void reset(int z){
		//drops item
		int temp = (int) (Math.random()*4 + 1);
		if (weapon != temp && temp > 0){
			for(int c = 0; c < Crate.size(); c++){
				if (Zombie.get(z).xDist >= Crate.get(c).xDist - Weapon.get(temp).Width && Zombie.get(z).xDist <= Crate.get(c).xDist + 25){
					Weapon.get(temp).setWeapon(Zombie.get(z).xDist, Ground-Weapon.get(temp).Height - 25, Weapon.get(temp).Width, Weapon.get(temp).Height);
				}
				else{
					Weapon.get(temp).setWeapon(Zombie.get(z).xDist, Ground-Weapon.get(temp).Height, Weapon.get(temp).Width, Weapon.get(temp).Height);
				}
			}
		}
		Zombie.get(z).Health = 100;
		int tempZ = (int) (Math.random()*2);
		if(tempZ == 0){
			Zombie.get(z).xDist = (int) (Math.random()*-500 - 75);
		}
		if(tempZ == 1){
			Zombie.get(z).xDist = (int) (Math.random()*500) + 1000;
		}
		Zombie.get(z).yDist = 275;
		Zombie.get(z).jump = 0;
		Zombie.get(z).Spoted = 0;
		Zombie.get(z).foot = 1;
		Zombie.get(z).direction = 0;
		Zombie.get(z).Ydirection = 1;
		Zombie.get(z).left = false;
		Zombie.get(z).right = false;
		Zombie.get(z).RanJump = 0;
	}
	public void Move(){
		boolean over = false;
		//checks to see if man is over a crate
		for (int q = 0; q < Crate.size(); q++){
			if (xManInt >= Crate.get(q).xDist -45 && xManInt <= Crate.get(q).xDist +25 && yManInt <= 250){
				over = true;
			}
		}
		//jumps when not over a crate
		if (!over){
			if (jump != 0){
				yManInt -= jump;
				jump -= (Ground - yManInt - 75)/2 * (Ydirection);
				if(jump >= 0){
					Ydirection *= -1;
				}
				if(yManInt + 75 >= Ground || jump == 10){
					yManInt = Ground - 75;
					jump = 0;
					Ydirection *= -1;
				}
			}
		}
		//jumps while over a crate
		if (over){
			if (jump != 0){
				yManInt -= jump;
				jump -= (Ground - yManInt - 75 - 25)/2 * (Ydirection);
				if(jump >= 0){
					Ydirection *= -1;
				}
				if(yManInt + 75 >= Ground - 25 || jump == 10){
					yManInt = Ground - 75 - 25;
					jump = 0;
					Ydirection *= -1;
				}
			}
		}
		//makes you fall if not over a crate and done jumping
		if (!over && jump == 0 && (yManInt + 75) < 340){
			jump = -100;
		}
		//moves left
		if (left){
			int tempdist = 10;
			for (int q = 0; q < Crate.size(); q++){
				if (xManInt > Crate.get(q).xDist){
					if (xManInt - 10 > Crate.get(q).xDist -45 && xManInt - 10 < Crate.get(q).xDist +25 && yManInt > 250){
						tempdist = xManInt - Crate.get(q).xDist - 25;
						break;
					}
					else{
						tempdist = 10;
					}
				}
			}
			xManInt -= tempdist;
			direction = -1;
			Weapon.get(weapon).direction = -1;
			//imaging
			foot += 1;
			if(foot == 4){
				foot = 0;
			}
			if (foot == 0){
				ManImage = getImage(getDocumentBase(),"ManLeft1.png");
			}
			else if (foot == 1){
				ManImage = getImage(getDocumentBase(),"ManLeft2.png");
			}
			else if (foot == 2){
				ManImage = getImage(getDocumentBase(),"ManLeft3.png");
			}
			else if (foot == 3){
				ManImage = getImage(getDocumentBase(),"ManLeft2.png");
			}
		}
		//moves right
		if (right){
			int tempdist = 10;
			for (int q = 0; q < Crate.size(); q++){
				if (xManInt < Crate.get(q).xDist){
					if (xManInt + 10 > Crate.get(q).xDist -75 && xManInt +10 < Crate.get(q).xDist +25 && yManInt > 250){
						tempdist = Crate.get(q).xDist - xManInt - 45;
						break;
					}
					else{
						tempdist = 10;
					}
				}
			}
			xManInt += tempdist;
			direction = 1;
			Weapon.get(weapon).direction = 1;
			//imaging
			foot += 1;
			if(foot == 4){
				foot = 0;
			}
			if (foot == 0){
				ManImage = getImage(getDocumentBase(),"ManRight1.png");
			}
			else if (foot == 1){
				ManImage = getImage(getDocumentBase(),"ManRight2.png");
			}
				else if (foot == 2){
				ManImage = getImage(getDocumentBase(),"ManRight3.png");
			}
			else if (foot == 3){
				ManImage = getImage(getDocumentBase(),"ManRight2.png");
			}
		}
		if(direction == 1){
			//sets X Weapon when attacking
			if(active > 0){
				if(weapon != 3){
					Weapon.get(weapon).xDist = xManInt + 45;
					Weapon.get(weapon).yDist = yManInt + 20;
				}
				if(weapon == 3){
					Weapon.get(weapon).xDist = xManInt + 30;
					Weapon.get(weapon).yDist = yManInt + 10;
				}
			}
			//sets X Weapon when not attacking
			if(active == 0){
				if(weapon != 3){
					Weapon.get(weapon).xDist = xManInt + 30;
					Weapon.get(weapon).yDist = yManInt + 20;
				}
				if(weapon == 3){
					Weapon.get(weapon).xDist = xManInt + 30;
					Weapon.get(weapon).yDist = yManInt + 10;
				}
			}
			if (weapon == 0){
				Weapon.get(weapon).WeaponImage = getImage(getDocumentBase(),"fistR.png");
			}
			else if (weapon == 1){
				Weapon.get(weapon).WeaponImage = getImage(getDocumentBase(),"swordR.png");
			}
			else if (weapon == 3){
				Weapon.get(weapon).WeaponImage = getImage(getDocumentBase(),"bowR.png");
			}
		}
		if(direction == -1){
			//sets X Weapon when attacking
			if(active > 0){
				if(weapon != 3){
					Weapon.get(weapon).xDist = xManInt - Weapon.get(weapon).Width;
					Weapon.get(weapon).yDist = yManInt + 20;
				}
				if(weapon == 3){
					Weapon.get(weapon).xDist = xManInt + 15 - Weapon.get(weapon).Width;
					Weapon.get(weapon).yDist = yManInt + 10;
				}
			}
			//sets X Weapon when not attacking
			if(active == 0){
				if(weapon != 3){
					Weapon.get(weapon).xDist = xManInt + 15 - Weapon.get(weapon).Width;
					Weapon.get(weapon).yDist = yManInt + 20;
				}
				if(weapon == 3){
					Weapon.get(weapon).xDist = xManInt + 15 - Weapon.get(weapon).Width;
					Weapon.get(weapon).yDist = yManInt + 10;
				}
			}
			if (weapon == 0){
				Weapon.get(weapon).WeaponImage = getImage(getDocumentBase(),"fistL.png");
			}
			else if (weapon == 1){
				Weapon.get(weapon).WeaponImage = getImage(getDocumentBase(),"swordL.png");
			}
			else if (weapon == 3){
				Weapon.get(weapon).WeaponImage = getImage(getDocumentBase(),"bowL.png");
			}
		}
	}
	public void MakeArrow(){
		if(arrows > 0){
			arrows--;
			Arrow.add(new Arrow());
			Arrow.get(Arrow.size()-1).setArrow(Weapon.get(3).xDist, Weapon.get(3).yDist + 20);
			Arrow.get(Arrow.size()-1).direction = direction;
			if(Arrow.get(Arrow.size()-1).direction == -1){
				Arrow.get(Arrow.size()-1).ArrowImage = getImage(getDocumentBase(),"arrowR.png");
			}
			if(Arrow.get(Arrow.size()-1).direction == 1){
				Arrow.get(Arrow.size()-1).ArrowImage = getImage(getDocumentBase(),"arrowL.png");
			}
		}
	}
	public void newGame(){
		for(int c = Crate.size() - 1; c >= 0; c--){
			Crate.get(c).setCrate(-100, -100);;
		}
		repaint();
		arrows = 0;
		Health = 100;
		foot = 1;
		Ydirection = 1;
		jump = 0;
		Kills = 0;
		Ground = 350;
		xManInt = 463;
		yManInt = 275;
		Shot = 0;
		Timecount = 0;
		direction = 1;
		weapon = 0;
		active = 0;
		left = false;
		right = false;
		PLAY = false;
		for(int a = Arrow.size() - 1; a >= 0; a--){
			Arrow.remove(a);
		}
		for(int c = Crate.size() - 1; c >= 0; c--){
			Crate.remove(c);
		}
		for(int z = Zombie.size() - 1; z >= 0; z--){
			Zombie.remove(z);
		}
		for(int i = 0; i < 5; i++){
			if (i == 0){
				Weapon.get(i).setWeapon(-100, -100, 15, 10);
			}
			else if (i == 1){
				Weapon.get(i).setWeapon(-100, -100, 45, 15);
			}
			else if (i == 2){
				Weapon.get(i).setWeapon(-100, -100, 15, 15);
			}
			else if (i == 3){
				Weapon.get(i).setWeapon(-100, -100, 15, 45);
			}
			else if (i == 4){
				Weapon.get(i).setWeapon(-100, -100, 15, 5);
			}
		}
		ManImage = getImage(getDocumentBase(),"ManRight2.png");
		//make crate
		for(int i = 0; i < 7; i++){
			int temp = (int) (Math.random()*1000);
			while(temp > 400 && temp < 600){
				temp = (int) (Math.random()*1000);
			}
			for(int x = 0; x < Crate.size(); x++){
				if(Crate.get(x).xDist-25 < temp && Crate.get(x).xDist+25 > temp){
					temp = (int) (Math.random()*675) + 300;
				}
			}
			Crate.add(new Crate());
			Crate.get(i).CrateImage = getImage(getDocumentBase(),"crate.png");
			Crate.get(i).setCrate(temp, 325);
		}
		//make zombies
		for(int i = 0; i < 10; i++){
			Zombie.add(new Zombie());
			Zombie.get(i).ZombieImage = getImage(getDocumentBase(),"ZLeft2.png");
			int tempZ = (int) (Math.random()*2);
			if(tempZ == 0){
				Zombie.get(i).xDist = (int) (Math.random()*-500 - 75);
			}
			if(tempZ == 1){
				Zombie.get(i).xDist = (int) (Math.random()*500) + 1000;
			}
		}
		//makes weapons
		setWeapons();
	}
	public void MoveArrow(){
		if(Arrow.size() > 0){
			for(int a = 0; a < Arrow.size(); a++){
				if(Arrow.get(a).yDist < Ground-Arrow.get(a).Height){
					Arrow.get(a).xDist += Arrow.get(a).xChange *  Arrow.get(a).direction;
					Arrow.get(a).Dist += 1;
					if(Arrow.get(a).Dist > 15){
						Arrow.get(a).yDist += Arrow.get(a).yChange;
						Arrow.get(a).yChange += 1;
					}
				}
				else{
					Arrow.remove(a);
				}
			}
		}
		if(arrows == 0 && weapon == 3){
			Weapon.get(weapon).xDist = -100;
			Weapon.get(weapon).yDist = -100;
			weapon = 0;
		}
	}
	public void ZombieMove(){
		for (int z = 0; z < Zombie.size(); z++){
			//make jump
			Zombie.get(z).RanJump = (int) (Math.random()*7);
			if(Zombie.get(z).RanJump == 3){
				if(Zombie.get(z).jump == 0){
					Zombie.get(z).jump = 10;
				}
			}
			//choose to move left or right
			if(Zombie.get(z).xDist > xManInt){
				Zombie.get(z).right = false;
				Zombie.get(z).left = true;
			}
			else if(Zombie.get(z).xDist < xManInt){
				Zombie.get(z).right = true;
				Zombie.get(z).left = false;
			}
			//see if over a crate
			for (int q = 0; q < Crate.size(); q++){
				if (Zombie.get(z).xDist + 45 >= Crate.get(q).xDist && Zombie.get(z).xDist <= Crate.get(q).xDist + 25){
					Zombie.get(z).over = true;
					break;
				}
				else{
					Zombie.get(z).over = false;
				}
			}
			//jumping while not over a crate
			if (!Zombie.get(z).over){
					Zombie.get(z).yDist -= Zombie.get(z).jump;
					Zombie.get(z).jump -= (Ground - Zombie.get(z).yDist - 75)/2 * (Zombie.get(z).Ydirection);
					if(Zombie.get(z).jump >= 0){
						Zombie.get(z).Ydirection *= -1;
					}
					if(Zombie.get(z).yDist + 75 >= Ground || Zombie.get(z).jump == 10){
						Zombie.get(z).yDist = Ground - 75;
						Zombie.get(z).jump = 0;
						Zombie.get(z).Ydirection *= -1;
					}
					if(!(Zombie.get(z).yDist + 75 == Ground)/* && Zombie.get(z).jump == 0*/){
						Zombie.get(z).jump = -10;						
					}
			}
			//jumping while over a crate
			if (Zombie.get(z).over){
					Zombie.get(z).yDist -= Zombie.get(z).jump;
					Zombie.get(z).jump -= (Ground - Zombie.get(z).yDist - 75 - 25)/2 * (Zombie.get(z).Ydirection);
					if(Zombie.get(z).jump >= 0){
						Zombie.get(z).Ydirection *= -1;
					}
					if(Zombie.get(z).yDist + 75 >= Ground - 25 || Zombie.get(z).jump == 10){
						Zombie.get(z).yDist = Ground - 75 - 25;
						Zombie.get(z).jump = 0;
						Zombie.get(z).Ydirection *= -1;
					}
					if(!(Zombie.get(z).yDist + 75 == Ground - 25) && Zombie.get(z).jump == 0){
						Zombie.get(z).jump = -10;						
					}
			}
			//moves left
			if (Zombie.get(z).left){
				int tempdist = 5;
				for (int q = 0; q < Crate.size(); q++){
					if (Zombie.get(z).xDist - 5 > Crate.get(q).xDist -45 && Zombie.get(z).xDist - 5 < Crate.get(q).xDist +25 && Zombie.get(z).yDist > 250){
						tempdist = Zombie.get(z).xDist - Crate.get(q).xDist - 25;
						break;
					}
					else{
						tempdist = 5;
					}
				}
				Zombie.get(z).xDist -= tempdist;
				Zombie.get(z).direction = -1;
				Zombie.get(z).foot += 1;
				if(Zombie.get(z).foot == 4){
					Zombie.get(z).foot = 0;
				}
				if (Zombie.get(z).foot == 0){
					Zombie.get(z).ZombieImage = getImage(getDocumentBase(),"ZLeft1.png");
				}
				else if (Zombie.get(z).foot == 1){
					Zombie.get(z).ZombieImage = getImage(getDocumentBase(),"ZLeft2.png");
				}
				else if (Zombie.get(z).foot == 2){
					Zombie.get(z).ZombieImage = getImage(getDocumentBase(),"ZLeft3.png");
				}
				else if (Zombie.get(z).foot == 3){
					Zombie.get(z).ZombieImage = getImage(getDocumentBase(),"ZLeft2.png");
				}
			}
			//moves right
			if (Zombie.get(z).right){
				int tempdist = 5;
				for (int q = 0; q < Crate.size(); q++){
					if (Zombie.get(z).xDist + 5 > Crate.get(q).xDist -75 && Zombie.get(z).xDist + 5 < Crate.get(q).xDist +25 && Zombie.get(z).yDist > 250){
						tempdist = Crate.get(q).xDist - Zombie.get(z).xDist - 45;
						break;
					}
					else{
						tempdist = 5;
					}
				}
				Zombie.get(z).xDist += tempdist;
				Zombie.get(z).direction = 1;
				Zombie.get(z).foot += 1;
				if(Zombie.get(z).foot == 4){
					Zombie.get(z).foot = 0;
				}
				if (Zombie.get(z).foot == 0){
					Zombie.get(z).ZombieImage = getImage(getDocumentBase(),"ZRight1.png");
				}
				else if (Zombie.get(z).foot == 1){
					Zombie.get(z).ZombieImage = getImage(getDocumentBase(),"ZRight2.png");
				}
				else if (Zombie.get(z).foot == 2){
					Zombie.get(z).ZombieImage = getImage(getDocumentBase(),"ZRight3.png");
				}
				else if (Zombie.get(z).foot == 3){
					Zombie.get(z).ZombieImage = getImage(getDocumentBase(),"ZRight2.png");
				}
			}
		}
	}
}