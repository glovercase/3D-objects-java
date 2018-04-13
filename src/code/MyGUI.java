package code;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


/**
 * A simple example of how to extend the GUI class. The converBitmapToImage
 * method is particularly useful.
 */
public class MyGUI extends GUI {

	
	float xangle = 0f;
	float yangle = 0f;
	//data structure
	ArrayList<Polygon> polygons = new ArrayList<Polygon>();
	Vector3D lightsource = null;
	
	@Override
	protected void onLoad(File file) {
		
		try {
			// make a reader
			polygons.clear();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			String firstline = br.readLine();
			String[] firsttokens = firstline.split("[\\s]+");
			
			lightsource = new Vector3D(asFloat(firsttokens[0]),
					   				   asFloat(firsttokens[1]),
					   				   asFloat(firsttokens[2]));
			
			while ((line = br.readLine()) != null) {
				
				String[] tokens = line.split("[\\s]+");
				if (tokens.length < 12) {
					continue;
				}
				
				Vector3D v1 = new Vector3D(asFloat(tokens[0]),
										   asFloat(tokens[1]),
										   asFloat(tokens[2]));
				Vector3D v2 = new Vector3D(asFloat(tokens[3]),
						   				   asFloat(tokens[4]),
						   				   asFloat(tokens[5]));
				Vector3D v3 = new Vector3D(asFloat(tokens[6]),
						   				   asFloat(tokens[7]),
						   				   asFloat(tokens[8]));
				Color color = new Color(asInt(tokens[9]),
										asInt(tokens[10]),
										asInt(tokens[11]));
				Polygon poly = new Polygon(v1,v2,v3,color);
				polygons.add(poly);

			}

		br.close();
	
	} catch (IOException e) {
		throw new RuntimeException("file reading failed.");
	}
	}

	private int asInt(String string) {
		return Integer.parseInt(string);
	}

	private Float asFloat(String string) {
		return Float.parseFloat(string);
	}

	@Override
	protected void onKeyPress(KeyEvent ev) {
		if (ev.getKeyCode() == KeyEvent.VK_LEFT
				|| Character.toUpperCase(ev.getKeyChar()) == 'A'){
			xangle += 0.001;
			
		}else if (ev.getKeyCode() == KeyEvent.VK_RIGHT
				|| Character.toUpperCase(ev.getKeyChar()) == 'D'){
			xangle -= 0.001;
		}else if(ev.getKeyCode() == KeyEvent.VK_UP || Character.toUpperCase(ev.getKeyChar()) == 'W'){
			yangle -= 0.001;
		}else if(ev.getKeyCode() == KeyEvent.VK_DOWN || Character.toUpperCase(ev.getKeyChar()) == 'S'){
			yangle += 0.001;
		}
		
	}

	@Override
	protected BufferedImage render() {
		Zbuffer zbuffer = new Zbuffer();

		float minx = Float.POSITIVE_INFINITY;
		float miny = Float.POSITIVE_INFINITY;
		float minz = Float.POSITIVE_INFINITY;
		
		float maxx = Float.NEGATIVE_INFINITY;
		float maxy = Float.NEGATIVE_INFINITY;
		float maxz = Float.NEGATIVE_INFINITY;
		
		for(Polygon p : polygons){
			
			minx = Math.min(p.minX(), minx);
			maxx = Math.max(p.maxX(), maxx);
			
			miny = Math.min(p.minY(), miny);
			maxy = Math.max(p.maxY(), maxy);
			
			minz = Math.min(p.minZ(), minz);
			maxz = Math.max(p.maxZ(), maxz);
		}
		float width = maxx- minx;
		float height = maxy - miny;
		//float depth = maxz - minz;
		
		Transform rotatey = Transform.newYRotation(yangle);
		Transform rotatex = Transform.newXRotation(xangle);
		for(Polygon p: polygons){
		
			p.transform(rotatey);
		
			
		}
		for(Polygon p: polygons){
			p.transform(rotatex);
		}
		
		Transform translation2;
		float scaling;
	
		
		if((width) > (height)  ){
			
			scaling = (CANVAS_WIDTH-100)/(maxx-minx);
			
			translation2 = Transform.newTranslation(50,50 , 0);
			 
		}else{
			scaling = (CANVAS_WIDTH-100)/(maxy-miny);
			
			translation2 = Transform.newTranslation(50,50 , 0);
		}
		//set to zero
		Transform translation = Transform.newTranslation(-minx, -miny, 0);
		//then scale
		Transform scale = Transform.newScale(scaling, scaling, 1);
		
		Transform total = translation2.compose(scale).compose(translation);
		int[] ambient = getAmbientLight();
		for(Polygon p :polygons){
			Color shading = p.setcolor(ambient, lightsource);
			p.transform(total);
	
			zbuffer.drawPolygon(p, shading);
			
		}
		// render the bitmap to the image so it can be displayed (and saved)
		return convertBitmapToImage(zbuffer.colors);
	}

	/**
	 * Converts a 2D array of Colors to a BufferedImage. Assumes that bitmap is
	 * indexed by column then row and has imageHeight rows and imageWidth
	 * columns. Note that image.setRGB requires x (col) and y (row) are given in
	 * that order.
	 */
	private BufferedImage convertBitmapToImage(Color[][] bitmap) {
		BufferedImage image = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT,
				BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < CANVAS_WIDTH; x++) {
			for (int y = 0; y < CANVAS_HEIGHT; y++) {
				image.setRGB(x, y, bitmap[x][y].getRGB());
			}
		}
		return image;
	}

	public static void main(String[] args) {
		new MyGUI();
	}


}// code for COMP261 assignments