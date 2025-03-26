package com.telecoop.telecoop.ui.dashboard;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.List;

public class SimpleBarChartView extends View {

    private List<Float> dataPoints; // Liste de valeurs (peut être 7, 4, 12 etc...)
    private String[] xLabels; // Labels pour l'axe X (de même taille que dataPoints)
    private String unitSuffix = "h";
    private Paint barPaint;
    private Paint axisPaint;
    private Paint textPaint;
    private Paint avgLinePaint;
    private Paint dottedLinePaint; // Pour dessiner les graduations

    public SimpleBarChartView(Context context) {
        super(context);
        init();
    }

    public SimpleBarChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleBarChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(Color.BLUE);

        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(2f);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30f);

        avgLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        avgLinePaint.setColor(Color.RED);
        avgLinePaint.setStrokeWidth(3f);
        avgLinePaint.setStyle(Paint.Style.STROKE);
        avgLinePaint.setPathEffect(new DashPathEffect(new float[]{10,10}, 0));

        dottedLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dottedLinePaint.setColor(Color.GRAY);
        dottedLinePaint.setStrokeWidth(1.5f);
        dottedLinePaint.setStyle(Paint.Style.STROKE);
        dottedLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5}, 0));
    }

    // Accepte n'importe quel nombre de points (doit être non vide)
    public void setDataPoints(List<Float> dataPoints) {
        if (dataPoints == null || dataPoints.isEmpty()) {
            throw new IllegalArgumentException("La liste de données ne peut être vide.");
        }
        this.dataPoints = dataPoints;
        invalidate();
    }

    // Accepte un tableau de labels (doit avoir la même longueur que dataPoints)
    public void setXLabels(String[] labels) {
        if (labels == null || labels.length == 0) {
            throw new IllegalArgumentException("Les labels ne peuvent être vides.");
        }
        this.xLabels = labels;
        invalidate();
    }

    // Permet de configurer le suffixe
    public void setUnitSuffix(String suffix) {
        this.unitSuffix = suffix;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dataPoints == null || dataPoints.isEmpty() || xLabels == null || xLabels.length != dataPoints.size())
            return;

        float width = getWidth();
        float height = getHeight();

        // Marges
        float marginLeft = 80f;
        float marginRight = 40f;
        float marginTop = 40f;
        float marginBottom = 60f;

        float chartWidth = width - marginLeft - marginRight;
        float chartHeight = height - marginTop - marginBottom;

        // Axe X
        float axisX_Y = height - marginBottom;
        canvas.drawLine(marginLeft, axisX_Y, width - marginRight, axisX_Y, axisPaint);

        // Axe Y
        canvas.drawLine(marginLeft, marginTop, marginLeft, axisX_Y, axisPaint);

        // Calcul du max
        float maxValue = 0f;
        for (float v : dataPoints) {
            if (v > maxValue) maxValue = v;
        }
        if (maxValue == 0f) maxValue = 1f;

        // Calcul de la moyenne en ignorant les points à 0
        float sumNonZero = 0f;
        int countNonZero = 0;
        for (float v : dataPoints) {
            if (v > 0) {
                sumNonZero += v;
                countNonZero++;
            }
        }
        float avgNonZero = 0f;
        if (countNonZero > 0) {
            avgNonZero = sumNonZero / countNonZero;
        }

        // Ligne moyenne pointillée
        float avgRatio = avgNonZero / maxValue;
        float avgY = axisX_Y - (avgRatio * chartHeight);
        canvas.drawLine(marginLeft, avgY, width - marginRight, avgY, avgLinePaint);

        // Graduation de l'axe Y (avec step = 1)
        int step = 1;
        if (maxValue > 5) {
            step = (int)Math.ceil(maxValue / 5.0);
        }
        for (int val = 0; val <= maxValue; val += step) {
            float ratio = val / maxValue;
            float y = axisX_Y - ratio * chartHeight;
            canvas.drawLine(marginLeft, y, width - marginRight, y, dottedLinePaint);
            String label = String.valueOf(val);
            float labelWidth = textPaint.measureText(label);
            canvas.drawText(label, marginLeft - labelWidth - 10, y + 10, textPaint);
        }

        // Calculer l'espacement horizontal en fonction de la taille des données
        int n = dataPoints.size();
        float barSpace = chartWidth / n;
        float barWidth = barSpace * 0.6f;
        float cornerRadius = 20f;

        for (int i = 0; i < n; i++) {
            float value = dataPoints.get(i);
            float ratio = value / maxValue;
            float barHeight = ratio * chartHeight;

            float left = marginLeft + i * barSpace + (barSpace - barWidth)/2f;
            float top = axisX_Y - barHeight;
            float right = left + barWidth;
            float bottom = axisX_Y;

            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, barPaint);

            String dayLabel = xLabels[i];
            float textWidth = textPaint.measureText(dayLabel);
            float labelX = left + (barWidth - textWidth)/2f;
            float labelY = axisX_Y + textPaint.getTextSize() + 10f;
            canvas.drawText(dayLabel, labelX, labelY, textPaint);

            if (value > 0){
                String valStr = String.format("%.1f%s", value, unitSuffix);
                float valWidth = textPaint.measureText(valStr);
                float valX = left + (barWidth - valWidth)/2f;
                float valY = top - 8f;
                canvas.drawText(valStr, valX, valY, textPaint);
            }
        }
    }
}
