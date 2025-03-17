package com.telecoop.telecoop.data;

public class TempsPositif {
    private String title;
    private long durationMs;   // Durée totale en millisecondes
    private long timeSpentMs;  // Temps déjà passé (en ms)
    private boolean isRunning; // Indique si le timer est en cours

    public TempsPositif(String title, long durationMs) {
        this.title = title;
        this.durationMs = durationMs;
        this.timeSpentMs = 0;
        this.isRunning = false;
    }

    public String getTitle() { return title; }
    public long getDurationMs() { return durationMs; }
    public long getTimeSpentMs() { return timeSpentMs; }
    public boolean isRunning() { return isRunning; }

    public void setTitle(String title) { this.title = title; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }
    public void setTimeSpentMs(long timeSpentMs) { this.timeSpentMs = timeSpentMs; }
    public void setRunning(boolean running) { isRunning = running; }

    //Retourne la fraction d'avancement (entre 0 et 1)
    public float getProgressFraction() {
        if (durationMs == 0) return 0f;
        return (float) timeSpentMs / (float) durationMs;
    }

    /**
     * Retourne la couleur d'état :
     *  - Rouge si pas commencé (timeSpentMs == 0)
     *  - Orange si en cours (0 < timeSpentMs < durationMs)
     *  - Vert si terminé (timeSpentMs >= durationMs)
     */
    public int getStateColor() {
        if (timeSpentMs == 0) {
            return 0xFFFF0000; // Rouge
        } else if (timeSpentMs < durationMs) {
            return 0xFFFFA500; // Orange
        } else {
            return 0xFF00FF00; // Vert
        }
    }
}
