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

import java.util.Arrays;
import java.util.List;

/**
 * Un simple diagramme en barres pour 7 jours, avec :
 * - Barres arrondies
 * - Ligne horizontale pointillée pour la moyenne
 * - Graduation de l'axe Y
 * - Indication de la valeur moyenne
 */
public class SimpleBarChartView extends View {

    private List<Float> dataPoints;    // 7 valeurs, ex : [2.0, 3.5, 1.0, 4.0, 5.0, 2.5, 3.0]
    private String[] dayLabels = {"L", "M", "M", "J", "V", "S", "D"};

    private Paint barPaint;
    private Paint axisPaint;
    private Paint textPaint;
    private Paint avgLinePaint;
    private Paint dottedLinePaint;  // Pour dessiner les lignes horizontales de graduation

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
        // Peinture pour les barres
        barPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        barPaint.setColor(Color.BLUE);

        // Peinture pour les axes
        axisPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(2f);

        // Peinture pour le texte
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30f);

        // Peinture pour la ligne moyenne (pointillée)
        avgLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        avgLinePaint.setColor(Color.RED);
        avgLinePaint.setStrokeWidth(3f);
        avgLinePaint.setStyle(Paint.Style.STROKE);
        avgLinePaint.setPathEffect(new DashPathEffect(new float[]{10,10}, 0));

        // Peinture pour les lignes de graduation (pointillées)
        dottedLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dottedLinePaint.setColor(Color.GRAY);
        dottedLinePaint.setStrokeWidth(1.5f);
        dottedLinePaint.setStyle(Paint.Style.STROKE);
        dottedLinePaint.setPathEffect(new DashPathEffect(new float[]{5,5}, 0));
    }

    public void setDataPoints(List<Float> dataPoints) {
        if (dataPoints.size() != 7) {
            throw new IllegalArgumentException("Il faut exactement 7 valeurs (une par jour).");
        }
        this.dataPoints = dataPoints;
        invalidate();
    }

    public void setDayLabels(String[] labels) {
        if (labels.length != 7) {
            throw new IllegalArgumentException("Il faut 7 labels pour 7 jours.");
        }
        this.dayLabels = labels;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (dataPoints == null || dataPoints.isEmpty()) return;

        float width = getWidth();
        float height = getHeight();

        // Marges
        float marginLeft = 80f;
        float marginRight = 40f;
        float marginTop = 40f;
        float marginBottom = 60f;

        float chartWidth = width - marginLeft - marginRight;
        float chartHeight = height - marginTop - marginBottom;

        // Dessiner l'axe X
        float axisX_Y = height - marginBottom;
        canvas.drawLine(marginLeft, axisX_Y, width - marginRight, axisX_Y, axisPaint);

        // Dessiner l'axe Y
        float axisY_X = marginLeft;
        canvas.drawLine(axisY_X, marginTop, axisY_X, axisX_Y, axisPaint);

        // Trouver la valeur max
        float maxValue = 0f;
        for (float v : dataPoints) {
            if (v > maxValue) maxValue = v;
        }
        if (maxValue == 0f) maxValue = 1f;

        // Calculer la moyenne
        float sum = 0f;
        for (float v : dataPoints) {
            sum += v;
        }
        float avg = sum / dataPoints.size();  // moyenne

        // Dessiner la ligne moyenne (pointillée)
        float avgRatio = avg / maxValue;
        float avgY = axisX_Y - (avgRatio * chartHeight);
        canvas.drawLine(marginLeft, avgY, width - marginRight, avgY, avgLinePaint);

        // Dessiner une échelle Y (graduations)
        // Par exemple, on fait un step = 1 (ou ajuster si maxValue > 5)
        int step = 1;
        if (maxValue > 5) {
            step = (int)Math.ceil(maxValue / 5.0); // un step approximatif
        }

        // Dessiner les lignes horizontales pour chaque graduation
        for (int val = 0; val <= maxValue; val += step) {
            float ratio = val / maxValue;
            float y = axisX_Y - ratio * chartHeight;
            // petite ligne horizontale en pointillé
            canvas.drawLine(marginLeft, y, width - marginRight, y, dottedLinePaint);

            // label du grad sur l'axe Y
            String label = String.valueOf(val);
            float labelWidth = textPaint.measureText(label);
            canvas.drawText(label, marginLeft - labelWidth - 10, y + 10, textPaint);
        }

        // Calculer l'espacement horizontal pour 7 barres
        float barSpace = chartWidth / 7f;
        float barWidth = barSpace * 0.6f;
        float cornerRadius = 20f; // Rayon pour arrondir le haut des barres

        // Dessiner chaque barre
        for (int i = 0; i < dataPoints.size(); i++) {
            float value = dataPoints.get(i);
            float ratio = value / maxValue;
            float barHeight = ratio * chartHeight;

            float left = marginLeft + i * barSpace + (barSpace - barWidth)/2f;
            float top = axisX_Y - barHeight;
            float right = left + barWidth;
            float bottom = axisX_Y;

            // Dessiner la barre arrondie en haut => RoundRect
            RectF rect = new RectF(left, top, right, bottom);
            canvas.drawRoundRect(rect, cornerRadius, cornerRadius, barPaint);

            // Dessiner le label du jour
            String dayLabel = dayLabels[i];
            float textWidth = textPaint.measureText(dayLabel);
            float labelX = left + (barWidth - textWidth)/2f;
            float labelY = axisX_Y + textPaint.getTextSize() + 10f;
            canvas.drawText(dayLabel, labelX, labelY, textPaint);

            // Afficher la valeur au-dessus de la barre
            String valStr = String.format("%.1fh", value);
            float valWidth = textPaint.measureText(valStr);
            float valX = left + (barWidth - valWidth)/2f;
            float valY = top - 8f;
            canvas.drawText(valStr, valX, valY, textPaint);
        }
    }
}
