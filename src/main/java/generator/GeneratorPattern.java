package generator;

import java.util.Arrays;

public class GeneratorPattern implements Cloneable {
    private boolean[] pattern = new boolean[81];
    private String name = "";
    private boolean valid = false;

    public GeneratorPattern() {
    }

    public GeneratorPattern(String name) {
        this.name = name;
    }

    public GeneratorPattern(String name, boolean[] pattern) {
        this.name = name;
        this.pattern = Arrays.copyOf(pattern, pattern.length);
    }

    public GeneratorPattern clone() {
        GeneratorPattern newPattern = null;
        newPattern = new GeneratorPattern();
        newPattern.setName(this.name);
        newPattern.setValid(this.valid);
        System.arraycopy(this.pattern, 0, newPattern.pattern, 0, this.pattern.length);
        return newPattern;
    }

    @Override
    public String toString() {
        return this.name + ": " + Arrays.toString(this.pattern);
    }

    public int getAnzGivens() {
        int anz = 0;

        for (int i = 0; i < this.pattern.length; i++) {
            if (this.pattern[i]) {
                anz++;
            }
        }

        return anz;
    }

    public boolean[] getPattern() {
        return this.pattern;
    }

    public void setPattern(boolean[] pattern) {
        this.pattern = pattern;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isValid() {
        return this.valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
