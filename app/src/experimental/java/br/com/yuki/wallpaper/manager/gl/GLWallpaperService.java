package br.com.yuki.wallpaper.manager.gl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public abstract class GLWallpaperService extends WallpaperService {

    public class GLEngine extends Engine {
        class WallpaperGLSurfaceView extends GLSurfaceView {
            private static final String TAG = "WallpaperGLSurfaceView";

            WallpaperGLSurfaceView(Context context) {
                super(context);
                Log.d(TAG, "WallpaperGLSurfaceView(" + context + ")");
            }

            @Override
            public SurfaceHolder getHolder() {
                Log.d(TAG, "getHolder(): returning " + getSurfaceHolder());

                return getSurfaceHolder();
            }

            public void onDestroy() {
                Log.d(TAG, "onDestroy()");
                super.onDetachedFromWindow();
            }
        }

        private static final String TAG = "GLEngine";

        private WallpaperGLSurfaceView glSurfaceView;
        private boolean rendererHasBeenSet;

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            Log.d(TAG, "onCreate(" + surfaceHolder + ")");
            super.onCreate(surfaceHolder);
            glSurfaceView = new WallpaperGLSurfaceView(GLWallpaperService.this);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.d(TAG, "onVisibilityChanged(" + visible + ")");

            super.onVisibilityChanged(visible);

            if (rendererHasBeenSet) {
                if (visible) {
                    glSurfaceView.onResume();
                } else {
                    glSurfaceView.onPause();
                }
            }
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, "onDestroy()");

            super.onDestroy();
            glSurfaceView.onDestroy();
        }

        protected void setRenderer(GLSurfaceView.Renderer renderer) {
            Log.d(TAG, "setRenderer(" + renderer + ")");

            glSurfaceView.setRenderer(renderer);
            rendererHasBeenSet = true;
        }

        protected void setPreserveEGLContextOnPause(boolean preserve) {
            Log.d(TAG, "setPreserveEGLContextOnPause(" + preserve + ")");
            glSurfaceView.setPreserveEGLContextOnPause(preserve);
        }

        protected void setEGLContextClientVersion(int version) {
            Log.d(TAG, "setEGLContextClientVersion(" + version + ")");
            glSurfaceView.setEGLContextClientVersion(version);
        }
    }
}