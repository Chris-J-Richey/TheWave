import java.awt.Image;


public class Weapon{

	int xDist = 10;
	int yDist = 10;
	int Height = 10;
	int Width = 10;
	int direction = 1;

	Image WeaponImage;

	public void setWeapon(int x, int y, int w, int h){
		xDist = x;
		yDist = y;
		Width = w;
		Height = h;
	}
}

