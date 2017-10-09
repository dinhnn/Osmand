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


/**
 * Created by dinhnn on 10/6/17.
 */

public class HudAtlasMapRendererView extends AtlasMapRendererView {

    public HudAtlasMapRendererView(Context context) {
        super(context, null);
    }

    public HudAtlasMapRendererView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HudAtlasMapRendererView(Context context, AttributeSet attrs, int defaultStyle) {
        super(context, attrs, defaultStyle);
        for(Field field:MapRendererView.class.getDeclaredFields()){
            if(GLSurfaceView.class.equals(field.getType())){
                field.setAccessible(true);
                try {
                    GLSurfaceView view = (GLSurfaceView) field.get(this);
                    view.setRenderer(new RendererProxy());
                }catch(Exception e){

                }
            }
        }
    }
    private static final String TAG = "OsmAndCore:Android/MapRendererView";
    private final class RendererProxy implements GLSurfaceView.Renderer {
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
            gl.glScalex(1,-1,1);
            // Allow renderer to update
            _mapRenderer.update();

            // In case a new frame was prepared, render it
            if (_mapRenderer.prepareFrame())
                _mapRenderer.renderFrame();

            // Flush all the commands to GPU
            gl.glFlush();
        }
    }

}
