import java.awt.Image;


public class Zombie{

	int Health = 100;
	int xDist = 1050;
	int yDist = 275;
	int jump = 0;
	int Spoted = 0;
	int foot = 1;
	int direction = 0;
	int Ydirection = 1;
	boolean left = false;
	boolean right = false;
	int RanJump = 0;
	boolean over = false;

	Image ZombieImage;

	public void setZombie(int x, int y, int Vy){
		xDist = x;
		yDist = y;
		jump = Vy;
	}
}

