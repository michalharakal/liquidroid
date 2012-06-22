/*
 * Copyright 2012 Jakob Flierl
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package liqui.droid.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BarGraphView extends View {
    
    Paint paint = new Paint();
    double a = 25, b = 50, c = 75, d = 85, max = 100;
    double quorum = 0.0f;

    public BarGraphView(Context context) {
        super(context);
        setBackgroundColor(Color.WHITE);
    }
    
    public BarGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public void setValues(double a, double b, double c, double d, double max) {
        this.a = a; this.b = b; this.c = c; this.d = d; this.max = max;
        invalidate();
    }
    
    public void setQuorum(double quorum) {
        this.quorum = quorum;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int h = getHeight() - 1;
        
        // super.onDraw(canvas);
        float width = (float) (getWidth() - 1.0);
        // int height = getHeight();
        
        float xa = (float) (width * (a / (max * 1.0)));
        float xb = (float) (width * (b / (max * 1.0))) + xa;
        float xc = (float) (width * (c / (max * 1.0))) + xb;
        float xd = (float) (width * (d / (max * 1.0))) + xc;
        float qo = (float) (width * (quorum / (max * 1.0)));
        
        float y1 = 2; float y2 = h - 2;

        /*
        paint.setColor(Color.LTGRAY);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);        
        canvas.drawRect(0, y1, width, y2, paint);
        */
        
        if (d != 0) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(128 + 64, 0x0, 0x0));
            canvas.drawRect(xc, y1, xd, y2, paint);
        }
        
        if (c != 0) {
            paint.setColor(Color.LTGRAY);
            canvas.drawRect(xb, y1, xc, y2, paint);
        }
        
        if (b != 0) {
            paint.setColor(Color.DKGRAY);
            canvas.drawRect(xa, y1, xb, y2, paint);
        }
        
        if (a != 0) {
            paint.setColor(Color.rgb(0x0, 128 + 64, 0x0));
            canvas.drawRect(0, y1, xa, y2, paint);
        }
        
        /*
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(0, y1, width, y2 , paint);
        */

        if (quorum != 0.0f) {
            paint.setColor(Color.BLUE);
            canvas.drawRect(qo, 0, qo + 1, h, paint);
        }
    }
}
