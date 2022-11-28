import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.HashMap;

public class MapGenerator {
	public int map[][];
	public HashMap<Integer, Pair> mapInfo = new HashMap<Integer,Pair>();
	public final int WIDTH = 75;
	public final int HEIGHT = 50;
	
	public MapGenerator(int row, int col) {
		map = new int[row][col];
		for(int i = 0 ;i <row;i++) {
			for(int j=0; j<col;j++) {
				map[i][j] = 1;
				Pair tmp = new Pair(j*WIDTH+80, i*HEIGHT+50);
				mapInfo.put(i*col+j, tmp);
			}
		}
		System.out.println(mapInfo.size());
	}
	
	public void draw(Graphics2D g) { //특정 위치에 블록 그리기
		int row = map.length;
		int col = map[0].length;

		for(int i = 0 ;i <row;i++) {
			for(int j = 0;j<col;j++) {
				if(map[i][j] > 0) {
					g.setColor(Color.white);
					Pair pos = mapInfo.get(i*col+j);
					g.fillRect(pos.getX(), pos.getY(), WIDTH, HEIGHT);
					
					g.setStroke(new BasicStroke(3));
					g.setColor(Color.black);
					g.drawRect(pos.getX(), pos.getY(), WIDTH, HEIGHT);
				}
			}
		}
	}
	public void setBrickValue(int value, int row, int col) {
		map[row][col] = value;
	}
}
