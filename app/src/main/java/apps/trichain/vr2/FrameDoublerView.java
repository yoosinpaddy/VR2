package apps.trichain.vr2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.core.view.ViewCompat;

public class FrameDoublerView extends androidx.appcompat.widget.AppCompatImageView {
    private Bitmap bitmap = null;
    private Rect clipLeft;
    private Rect clipRight;
    private Matrix matrixLeft;
    private Matrix matrixRight;
    private Paint paint;

    public FrameDoublerView(Context context) {
        super(context);
        init();
    }

    public FrameDoublerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public FrameDoublerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }



    private void init() {
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setFilterBitmap(true);
        this.paint.setDither(true);
    }

    public void setImageSize(int i, int i2, int i3, int i4, boolean z, int i5, int i6) {
        float f;
        float f2;
        int i7 = i;
        int i8 = i2;
        int i9 = i3;
        int i10 = i4;
        int i11 = i6;
        float f3 = (float) i5;
        float f4 = ((((float) i7) / 2.0f) * f3) / ((float) (i9 * 100));
        float f5 = (f3 * ((float) i8)) / ((float) (i10 * 100));
        if (!z) {
            f2 = Math.min(f4, f5);
            f = f2;
        } else if (i11 == 90 || i11 == 270) {
            f2 = f5;
            f = f4;
        } else {
            f = f5;
            f2 = f4;
        }
        int i12 = (int) (((float) i9) * f2);
        int i13 = i7 / 2;
        int i14 = i3;
        int i15 = i4;
        int i16 = i13;
        int i17 = (i8 / 2) - (((int) (((float) i10) * f)) / 2);
        float f6 = f2;
        int i18 = i13;
        float f7 = f;
        int i19 = i12;
        int i20 = i6;
        this.matrixRight = getMatrix(i14, i15, i16, i17, f6, f7, i20);
        this.matrixLeft = getMatrix(i14, i15, i18 - i19, i17, f6, f7, i20);
        this.clipLeft = new Rect(0, 0, i18, i8);
        this.clipRight = new Rect(i18, 0, i7, i8);
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap2) {
        this.bitmap = bitmap2;
        postInvalidate();
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(ViewCompat.MEASURED_STATE_MASK);
        if (this.bitmap != null) {
            canvas.clipRect(this.clipLeft, Region.Op.REPLACE);
            canvas.drawBitmap(this.bitmap, this.matrixLeft, this.paint);
            canvas.clipRect(this.clipRight, Region.Op.REPLACE);
            canvas.drawBitmap(this.bitmap, this.matrixRight, this.paint);
        }
    }

    /* access modifiers changed from: package-private */
    public Matrix getMatrix(int i, int i2, int i3, int i4, float f, float f2, int i5) {
        Matrix matrix = new Matrix();
        matrix.reset();
        if (i5 != 0) {
            matrix.preTranslate((float) ((-i) / 2), (float) ((-i2) / 2));
            matrix.postRotate((float) i5);
            matrix.postTranslate((float) (i / 2), (float) (i2 / 2));
        }
        matrix.postScale(f, f2);
        matrix.postTranslate((float) i3, (float) i4);
        return matrix;
    }
}
