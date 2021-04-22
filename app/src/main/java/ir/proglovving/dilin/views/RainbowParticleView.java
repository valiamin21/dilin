package ir.proglovving.dilin.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RainbowParticleView extends View {
    private int[] colors = {Color.parseColor("#00239C"), Color.parseColor("#E10600")};

    private final Random random = new Random();
    private final List<RainbowParticleView.Particle> particles = new ArrayList<>();
    private int particlesLength = -1;
    int w, h;
    int lineLimit;

    private final Paint linePaint = new Paint();
    private final Paint paint = new Paint();

    public RainbowParticleView(Context context) {
        super(context);
    }

    public RainbowParticleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RainbowParticleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void setup() {
        if (isInEditMode())
            return;
        if (particlesLength != -1)
            return;
        w = getWidth();
        h = getHeight();
        lineLimit = (int) (Math.hypot(w, h) / 10);
        particlesLength = (int) (Math.hypot(w, h) / 25);
        for (int i = 0; i < particlesLength; i++) {
            particles.add(new RainbowParticleView.Particle());
        }
    }

    public void setColors(int[] colors){
        this.colors = colors;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode())
            return;
        setup();

        for (int i = 0; i < particlesLength; i++) {
            particles.get(i).draw(canvas);
            for (int j = i + 1; j < particlesLength; j++) {
                if (distance(particles.get(i), particles.get(j)) < lineLimit) {
                    linePaint.setColor(random.nextInt(2) == 0 ? particles.get(i).color : particles.get(j).color);

                    canvas.drawLine(particles.get(i).x, particles.get(i).y,
                            particles.get(j).x, particles.get(j).y, linePaint);
                }
            }
            particles.get(i).move();
        }

        try {
            Thread.sleep(1);
            invalidate();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

    double distance(RainbowParticleView.Particle p1, RainbowParticleView.Particle p2) {
        return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
    }

    private int getRandomColor() {
        return colors[random.nextInt(colors.length)];
    }

    class Particle {
        int size;
        int velocityX;
        int velocityY;
        int x, y;
        int color;

        public Particle() {
            size = random.nextInt(3) + 5;
            velocityX = (random.nextInt(2) + 1) * randomSign();
            velocityY = (random.nextInt(2) + 1) * randomSign();
            x = random.nextInt(w);
            y = random.nextInt(h);
            color = getRandomColor();
        }

        public void move() {
            if (x <= 0 || x >= w) {
                velocityX *= -1;
            }

            if (y <= 0 || y >= h) {
                velocityY *= -1;
            }
            x += velocityX;
            y += velocityY;
        }

        private int randomSign() {
            return (random.nextInt(2) == 0 ? -1 : 1);
        }

        public void draw(Canvas canvas) {
            paint.setColor(color);
            canvas.drawCircle(x, y, size, paint);
        }
    }
}
