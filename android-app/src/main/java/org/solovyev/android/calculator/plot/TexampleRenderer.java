// This is based on the OpenGL ES 1.0 sample application from the Android Developer website:
// http://developer.android.com/resources/tutorials/opengl/opengl-es10.html

package org.solovyev.android.calculator.plot;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class TexampleRenderer implements GLSurfaceView.Renderer {

	private GLText glText;                         	// A GLText Instance
	private Context context;                       	// Context (from Activity)

	private int width = 100;                       	// Updated to the Current Width + Height in onSurfaceChanged()
	private int height = 100;

	public TexampleRenderer(Context context) {
		super();
		this.context = context;                     	// Save Specified Context
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background frame color
		gl.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);

		// Create the GLText
		glText = new GLText(gl, context.getAssets());

		// Load the font from file (set size + padding), creates the texture
		// NOTE: after a successful call to this the font is ready for rendering!
		glText.load("Roboto-Regular.ttf", 14, 2, 2);  // Create Font (Height: 14 Pixels / X+Y Padding 2 Pixels)
	}

	public void onDrawFrame(GL10 gl) {
		// Redraw background color
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		// Set to ModelView mode
		gl.glMatrixMode(GL10.GL_MODELVIEW);       	// Activate Model View Matrix
		gl.glLoadIdentity();                        	// Load Identity Matrix

		// enable texture + alpha blending
		// NOTE: this is required for text rendering! we could incorporate it into
		// the GLText class, but then it would be called multiple times (which impacts performance).
		gl.glEnable(GL10.GL_TEXTURE_2D);          	// Enable Texture Mapping
		gl.glEnable(GL10.GL_BLEND);               	// Enable Alpha Blend
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);  // Set Alpha Blend Function

		// TEST: render the entire font texture
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);     	// Set Color to Use
		glText.drawTexture(width, height);        	// Draw the Entire Texture

		// TEST: render some strings with the font
		glText.begin(1.0f, 1.0f, 1.0f, 1.0f);     	// Begin Text Rendering (Set Color WHITE)
		glText.draw("Test String :)", 0, 0);      	// Draw Test String
		glText.draw("Line 1", 50, 50);            	// Draw Test String
		glText.draw("Line 2", 100, 100);          	// Draw Test String
		glText.end();                               	// End Text Rendering

		glText.begin(0.0f, 0.0f, 1.0f, 1.0f);     	// Begin Text Rendering (Set Color BLUE)
		glText.draw("More Lines...", 50, 150);    	// Draw Test String
		glText.draw("The End.", 50, 150 + glText.getCharHeight());  // Draw Test String
		glText.end();                               	// End Text Rendering

		// disable texture + alpha
		gl.glDisable(GL10.GL_BLEND);              	// Disable Alpha Blend
		gl.glDisable(GL10.GL_TEXTURE_2D);         	// Disable Texture Mapping
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width, height);

		// Setup orthographic projection
		gl.glMatrixMode(GL10.GL_PROJECTION);      	// Activate Projection Matrix
		gl.glLoadIdentity();                        	// Load Identity Matrix
		gl.glOrthof(                                	// Set Ortho Projection (Left,Right,Bottom,Top,Front,Back)
				0, width,
				0, height,
				1.0f, -1.0f
		);

		// Save width and height
		this.width = width;                         	// Save Current Width
		this.height = height;                       	// Save Current Height
	}
}
