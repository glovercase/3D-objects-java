package code;

import java.awt.Color;
import java.util.ArrayList;

public class Polygon {

	Vector3D[] vectors;
	ArrayList<Edge> edges;
	Color color;

	public Polygon(Vector3D v1, Vector3D v2, Vector3D v3, Color color) {
		vectors = new Vector3D[] { v1, v2, v3 };
		this.color = color;
	}

	public Vector3D getV1() {

		return vectors[0];
	}

	public Vector3D getV2() {
		return vectors[1];
	}

	public Vector3D getV3() {

		return vectors[2];
	}

	// create edge list
	public float[][] edgelist() {
		// create edges and add to list
		Edge e1 = new Edge(getV1(), getV2());
		Edge e2 = new Edge(getV2(), getV3());
		Edge e3 = new Edge(getV3(), getV1());
		edges = new ArrayList<Edge>();
		edges.add(e1);
		edges.add(e2);
		edges.add(e3);
		// new edge list
		float[][] el = new float[GUI.CANVAS_HEIGHT][4];
		// set to infinity
		for (int j = 0; j < GUI.CANVAS_HEIGHT; j++) {
			el[j][0] = Float.POSITIVE_INFINITY;
			el[j][1] = Float.POSITIVE_INFINITY;
			el[j][2] = Float.NEGATIVE_INFINITY;
			el[j][3] = Float.POSITIVE_INFINITY;
		}

		for (Edge e : edges) {
			Vector3D va = findLowY(e.v1, e.v2);
			Vector3D vb = findother(e.v1, e.v2);
			float mz = (vb.z - va.z) / (vb.y - va.y);
			float mx = (vb.x - va.x) / (vb.y - va.y);

			float x = va.x;
			float z = va.z;

			int maxi = Math.round(vb.y);

			for (int i = Math.round(va.y); i < maxi; i++) {
				if (x < el[i][0]) {
					el[i][0] = x;
					el[i][1] = z;
				}
				if (x > el[i][2]) {
					el[i][2] = x;
					el[i][3] = z;
				}
				x += mx;
				z += mz;
			}

		}
		return el;

	}

	public Vector3D getsurfaceNormal() {
		Vector3D first = getV2().minus(getV1());
		Vector3D second = getV3().minus(getV2());
		return first.crossProduct(second);

	}

	public Color setcolor(int[] ambient, Vector3D lightsource) {

		Vector3D surfaceNormal = getsurfaceNormal();
		
		float costh = surfaceNormal.unitVector().dotProduct(lightsource.unitVector());

		float red = color.getRed();
		float green = color.getGreen();
		float blue = color.getBlue();
		float amRed = ambient[0];
		float amGreen = ambient[1];
		float amBlue = ambient[2];
		float intensity = 0.5f;

		// set to number between 0 and 1
		red /= 255;
		green /= 255;
		blue /= 255;
		amRed /= 255;
		amBlue /= 255;
		amGreen /= 255;

		// Illumination formula
		float redlight = (amRed + intensity * costh) * red;
		float greenlight = (amGreen + intensity * costh) * green;
		float bluelight = (amBlue + intensity * costh) * blue;

		// check if in bounds of 0 and 1
		redlight = Math.min(redlight, 1);
		greenlight = Math.min(greenlight, 1);
		bluelight = Math.min(bluelight, 1);
		redlight = Math.max(redlight, 0);
		greenlight = Math.max(greenlight, 0);
		bluelight = Math.max(bluelight, 0);

		return new Color(redlight, greenlight, bluelight);
	}

	private Vector3D findother(Vector3D v1, Vector3D v2) {
		if (v1.y > v2.y) {
			return v1;
		} else {
			return v2;
		}
	}

	private Vector3D findLowY(Vector3D v1, Vector3D v2) {
		if (v1.y < v2.y) {
			return v1;
		} else {
			return v2;
		}

	}

	public float minX() {
		return Math.min(Math.min(vectors[1].x, vectors[2].x), vectors[0].x);
	}

	public float maxX() {
		return Math.max(Math.max(vectors[1].x, vectors[2].x), vectors[0].x);
	}

	public float minY() {
		return Math.min(Math.min(vectors[1].y, vectors[2].y), vectors[0].y);
	}

	public float maxY() {
		return Math.max(Math.max(vectors[1].y, vectors[2].y), vectors[0].y);
	}

	public void transform(Transform t) {

		vectors[0] = t.multiply(getV1());
		vectors[1] = t.multiply(getV2());
		vectors[2] = t.multiply(getV3());

	}

	public float minZ() {
		return Math.min(Math.min(vectors[1].z, vectors[2].z), vectors[0].z);
	}

	public float maxZ() {
		return Math.max(Math.max(vectors[1].z, vectors[2].z), vectors[0].z);
	}

}
