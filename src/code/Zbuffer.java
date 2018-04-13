package code;

import java.awt.Color;

public class Zbuffer {
	Color[][] colors = new Color[GUI.CANVAS_WIDTH][GUI.CANVAS_HEIGHT];
	float[][] depths = new float[GUI.CANVAS_WIDTH][GUI.CANVAS_HEIGHT];

	public Zbuffer() {
		for (int i = 0; i < GUI.CANVAS_WIDTH; i++) {
			for (int j = 0; j < GUI.CANVAS_HEIGHT; j++) {
				colors[i][j] = Color.gray;
				depths[i][j] = Float.POSITIVE_INFINITY;
			}
		}
	}

	public void drawPolygon(Polygon p, Color shading) {
		Vector3D surfaceNormal = p.getsurfaceNormal();
		if (surfaceNormal.z > 0) {
			return;
		}

		Vector3D v1 = p.getV1();
		Vector3D v2 = p.getV2();
		Vector3D v3 = p.getV3();

		int minY = Math.round(Math.min(Math.min(v1.y, v2.y), v3.y));
		int maxY = Math.round(Math.max(Math.max(v1.y, v2.y), v3.y));
		float[][] el = p.edgelist();
		for (int y = minY; y < maxY; y++) {
			int x = Math.round(el[y][0]);
			float z = el[y][1];
			float mz = (el[y][3] - el[y][1]) / (el[y][2] - el[y][0]);

			while (x <= Math.round(el[y][2]) && x < GUI.CANVAS_WIDTH) {

				if (z < depths[x][y]) {
					depths[x][y] = z;
					colors[x][y] = shading;

				}
				x++;
				z += mz;
			}
		}
	}

}
