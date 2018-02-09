package net.osmand.plus.hud;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import net.osmand.core.android.AtlasMapRendererView;
import net.osmand.core.android.GLSurfaceView;
import net.osmand.core.android.MapRendererView;
import net.osmand.core.jni.AreaI;
import net.osmand.core.jni.PointI;

import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.widget.Toast;

/**
 * Created by dinhnn on 10/6/17.
 */

public class HudAtlasMapRendererView extends AtlasMapRendererView {

    public HudAtlasMapRendererView(Context context) {
        this(context, null);
    }

    public HudAtlasMapRendererView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HudAtlasMapRendererView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
	//this.setElevationAngle(45);
	//Toast.makeText(context,"HUD mode supported", Toast.LENGTH_LONG).show();
/*
        GLSurfaceView view = null;
        for(Field field:MapRendererView.class.getDeclaredFields()){
            if(GLSurfaceView.class.equals(field.getType())){
                field.setAccessible(true);
                try {
                    view = (GLSurfaceView) field.get(this);
		    break;
                }catch(Exception e){
		    Toast.makeText(context,"HUD mode "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
	if(view!=null){
            for(Field field:GLSurfaceView.class.getDeclaredFields()){
		if(GLSurfaceView.Renderer.class.equals(field.getType())){
		    field.setAccessible(true);
		    try {
		        field.set(view,new HUDRendererProxy());
			Toast.makeText(context,"HUD mode supported", Toast.LENGTH_LONG).show();
			break;
		    }catch(Exception e){
			Toast.makeText(context,"HUD mode "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
		}
            }
	}
*/
    }
    private static final String TAG = "OsmAndCore:Android/MapRendererView";
    private final class HUDRendererProxy implements GLSurfaceView.Renderer {
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {

            // In case a new surface was created, and rendering was initialized it means that
            // surface was changed, so release rendering to allow it to initialize on next
            // call to onSurfaceChanged
            if (_mapRenderer.isRenderingInitialized()) {

                // Context still exists here and is active, so just release resources
                _mapRenderer.releaseRendering();
            }
        }

        public void onSurfaceChanged(GL10 gl, int width, int height) {

            // Set new "window" size and viewport that covers entire "window"
            _mapRenderer.setWindowSize(new PointI(width, height));
            _mapRenderer.setViewport(new AreaI(0, 0, height, width));

            // In case rendering is not initialized, initialize it
            // (happens when surface is created for the first time, or recreated)
            if (!_mapRenderer.isRenderingInitialized()) {

                if (!_mapRenderer.initializeRendering());
            }
        }

        public void onDrawFrame(GL10 gl) {

            // In case rendering was not initialized yet, don't do anything
            if (!_mapRenderer.isRenderingInitialized()) {
                return;
            }
	    gl.glPushMatrix();
            gl.glScalef(1.f, -1.f, 1.f);
            // Allow renderer to update
            _mapRenderer.update();

            // In case a new frame was prepared, render it
            if (_mapRenderer.prepareFrame())
                _mapRenderer.renderFrame();
            gl.glPopMatrix();
	    gl.glClearColor(0.0f,0.0f,0.0f,0.5f);
            // Flush all the commands to GPU
            gl.glFlush();
        }
    }

}
