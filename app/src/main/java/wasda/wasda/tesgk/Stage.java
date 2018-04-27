package wasda.wasda.tesgk;

import android.content.Context;
import android.opengl.EGLConfig;
import android.opengl.GLES10;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by user on 4/13/2018.
 */

public class Stage extends GLSurfaceView {
    private float w, h ;
    private int screenW, screenH ;
    private Texture tex;
    private String img;
    private float xPos, yPos, r, dist1, dist2 ;

    private FloatBuffer vertexBuffer ;

    MyRenderer mRenderer ;

    public Stage(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLConfigChooser(8, 8, 8, 8, 0, 0);
        mRenderer = new MyRenderer();
        setRenderer(new MyRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        float vertices[] = {
                -0.5f, -0.5f, 0.0f, //left-bottom
                0.5f, -0.5f, 0.0f, //right-bottom
                -0.5f, 0.5f, 0.0f, //left-top
                0.5f, 0.5f, 0.0f, //right-top
        };

        ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuffer = vbb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        img = "tes";
        tex = new Texture(getResources().getIdentifier(img, "drawable", context.getPackageName()));
    }

    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction() & MotionEvent.ACTION_MASK;
        float x, y ;
        int pointerIndex ;
        float x1, x2, y1, y2, ratio ;

        if(event.getPointerCount()==2){
            if (action == MotionEvent.ACTION_POINTER_UP) {
                x1 = event.getX(0);
                y1 = event.getY(0);
            } else {
                x1 = event.getX(0);
                y1 = event.getY(0);
            } if (action == MotionEvent.ACTION_POINTER_DOWN) {
                x2 = event.getX(1);
                y2 = event.getY(1);
                dist1 = (float)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
            } else {
                x2 = event.getX(1);
                y2 = event.getY(1);
                dist2 = (float)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
            }
            ratio = dist2/dist1;
            mRenderer.setRatio(ratio);
            requestRender();
        }

        if(event.getPointerCount()==1){
            if (action == MotionEvent.ACTION_POINTER_DOWN){
                x = event.getX();
                y = event.getY();
            } else {
                pointerIndex = event.getActionIndex();
                x = event.getX(pointerIndex);
                y = event.getY(pointerIndex);
            }
            mRenderer.setXY(x, y);
            requestRender();
        }

        return true;
    }

    private final class MyRenderer implements GLSurfaceView.Renderer {

        public void setXY(float x, float y){
            xPos = x * w / screenW;
            yPos = y * h / screenH;
        }

        public void setRatio(float scale){
            r = scale;
        }

        @Override
        public final void onDrawFrame(GL10 gl) {

            gl.glClear(GLES10.GL_COLOR_BUFFER_BIT);
            tex.prepare(gl, GL10.GL_CLAMP_TO_EDGE);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
            tex.draw(gl, xPos, yPos, tex.getWidth()*r, tex.getHeight()*r, 0);
        }

        @Override
        public void onSurfaceCreated(GL10 gl, javax.microedition.khronos.egl.EGLConfig eglConfig) {
            gl.glEnable(GL10.GL_ALPHA_TEST);
            gl.glEnable(GL10.GL_BLEND);
            gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);
            // We are in 2D, so no need depth
            gl.glDisable(GL10.GL_DEPTH_TEST);
            // Enable vertex arrays (we'll use them to draw primitives).
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            tex.load(getContext());

        }

        @Override
        public final void onSurfaceChanged(GL10 gl, int width, int height) {
            gl.glClearColor(0, 0, 0, 1.0f);
            if(width > height) {
                h = 600;
                w = width * h / height;
            } else {
                w = 600;
                h = height * w / width;
            }
            screenW = width;
            screenH = height;

            xPos = w/2;
            yPos = h/2;
            r=1;

            gl.glViewport(0, 0, screenW, screenH);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glOrthof(0, w, h, 0, -1, 1);
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            gl.glLoadIdentity();
        }

    }
}
