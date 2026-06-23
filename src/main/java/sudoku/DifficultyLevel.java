package sudoku;

import java.awt.*;

public final class DifficultyLevel {
    private DifficultyType type;
    private int ordinal;
    private int maxScore;
    private String name;
    private Color backgroundColor;
    private Color foregroundColor;

    public DifficultyLevel() {
    }

    public DifficultyLevel(DifficultyType type, int maxScore, String name, Color backgroundColor, Color foregroundColor) {
        this.setType(type);
        this.setMaxScore(maxScore);
        this.setName(name);
        this.setBackgroundColor(backgroundColor);
        this.setForegroundColor(foregroundColor);
    }

    public int getMaxScore() {
        return this.maxScore;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getForegroundColor() {
        return this.foregroundColor;
    }

    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public DifficultyType getType() {
        return this.type;
    }

    public void setType(DifficultyType type) {
        this.type = type;
        this.ordinal = type.ordinal();
    }

    public int getOrdinal() {
        return this.ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }
}
