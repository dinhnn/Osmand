package net.osmand.plus.myplaces;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import net.osmand.plus.GPXUtilities;
import net.osmand.plus.R;

import java.util.List;

/**
 * Created by maw on 02.09.15., original credit goes to gibbsnich
 * Developed further by Hardy 2016-11
 */
public class ElevationView extends ImageView {

	double maxElevation, minElevation;
	float xDistance;
	List<GPXUtilities.Elevation> elevationList;

	public ElevationView(Context ctx, AttributeSet as) {
		super(ctx, as);
	}

	public void onDraw(Canvas canvas) {
		final float screenScale = getResources().getDisplayMetrics().density;
		//TODO: Hardy: Perhaps support the other units of length in graph

		final int maxBase = (int)(maxElevation / 100) * 100 + 100, minBase = (int)(minElevation / 100) * 100;
		final float yDistance = maxBase - minBase;
		final float xPer = (float)canvas.getWidth() / xDistance;
		final float yPer = (float)canvas.getHeight() / yDistance;
		final float canvasRight = (float)canvas.getWidth() - 1f;
		final float canvasBottom = (float)canvas.getHeight() - 1f;

		// This y transform apparently needed to assure top and bottom lines show up on all devices
		final float yOffset = 2f;
		final float ySlope = ((float)canvas.getHeight() - 2f * yOffset) / (float)canvas.getHeight();

		Paint barPaint = new Paint();
		barPaint.setColor(getResources().getColor(R.color.dialog_inactive_text_color_dark));
		barPaint.setTextSize((int)(16f * screenScale + 0.5f));
		barPaint.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));
		float yTextLast = 9999f;
		for (int i = minBase; i <= maxBase ; i+=100) {
			float y = yOffset + ySlope * (canvasBottom - yPer * (float)(i - minBase));
			canvas.drawLine(0, y, canvasRight, y, barPaint);
			if ((yTextLast - y) >= (int)(32f * screenScale + 0.5f)) { // Overlap prevention
				canvas.drawText(String.valueOf(i) + " m", (int)(8f * screenScale + 0.5f), y - (int)(2f * screenScale + 0.5f), barPaint);
				yTextLast = y;
			}
		}

		float lastX = 0, lastY = 0;
		float xDistSum = 0;

		Paint paint = new Paint();
		paint.setColor(getResources().getColor(R.color.gpx_altitude_asc));
		paint.setStrokeWidth((int)(2f * screenScale + 0.5f));
		boolean first = true;
		if (elevationList != null) {
			for (GPXUtilities.Elevation elevation : elevationList) {
				xDistSum += elevation.distance;
				float nextX = xPer * xDistSum;
				float nextY = yOffset + ySlope * (canvasBottom - yPer * (float)(elevation.elevation - minBase));
				if (first) {
					first = false;
				} else {
					//Log.d("ElevationView", "curElevation: "+elevation.elevation+", drawLine: ("+lastX+", "+lastY+") -> ("+nextX+", "+nextY+")");
					canvas.drawLine(lastX, lastY, nextX, nextY, paint);
				}
				lastX = nextX;
				lastY = nextY;
			}
			//Log.d("ElevationView", "yMin: "+yMin+", yMax = "+yMax+", smallestY = "+smallestYFound+", biggestY = "+biggestYFound);
		}
	}

	public void setElevationData(List<GPXUtilities.Elevation> elevationData) {
		elevationList = elevationData;
	}

	public void setMaxElevation(double max) {
		maxElevation = max;
	}

	public void setMinElevation(double min) {
		minElevation = min;
	}

	public void setTotalDistance(float dist) {
		xDistance = dist;
	}

}
