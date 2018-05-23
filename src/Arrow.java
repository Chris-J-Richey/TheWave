import java.awt.Image;


public class Arrow{

	int xDist = 10;
	int yDist = 10;
	int Height = 5;
	int Width = 15;
	int direction = 1;
	int xChange = direction*10;
	int yChange = 2;
	int Dist = 0;

	Image ArrowImage;

	public void setArrow(int x, int y){
		xDist = x;
		yDist = y;
	}
}

