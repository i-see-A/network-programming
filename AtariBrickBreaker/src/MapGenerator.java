import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

public class MapGenerator {
	public int map[][];
	public int brickWidth;
	public int brickHeight;
	
	public MapGenerator(int row, int col) {
		map = new int[row][col];
		for(int i = 0 ;i <map.length;i++) {
			for(int j = 0;j<map[0].length;j++) {
				map[i][j] = 1; //블록 o
			}
		}
		
		brickWidth = 540/col;
		brickHeight = 150/row;
	}
	public void draw(Graphics2D g) { //특정 위치에 블록 그리기
		for(int i = 0 ;i <map.length;i++) {
			for(int j = 0;j<map[0].length;j++) {
				if(map[i][j] > 0) {
					g.setColor(Color.white);
					g.fillRect(j*brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
					
					g.setStroke(new BasicStroke(3));
					g.setColor(Color.black);
					g.drawRect(j*brickWidth + 80, i * brickHeight + 50, brickWidth, brickHeight);
				}
			}
		}
	}
	public void setBrickValue(int value, int row, int col) {
		map[row][col] = value;
		
	}
}
