package sudoku;

public final class StepConfig implements Cloneable, Comparable<StepConfig> {
    private int index;
    private SolutionType type;
    private int level;
    private SolutionCategory category;
    private int baseScore;
    private int adminScore;
    private boolean enabled;
    private boolean allStepsEnabled;
    private int indexProgress;
    private boolean enabledProgress;
    private boolean enabledTraining;

    public StepConfig() {
    }

    public StepConfig(
            int index,
            SolutionType type,
            int level,
            SolutionCategory category,
            int baseScore,
            int adminScore,
            boolean enabled,
            boolean allStepsEnabled,
            int indexProgress,
            boolean enabledProgress,
            boolean enabledTraining
    ) {
        this.setIndex(index);
        this.setType(type);
        this.setLevel(level);
        this.setCategory(category);
        this.setBaseScore(baseScore);
        this.setAdminScore(adminScore);
        this.setEnabled(enabled);
        this.setAllStepsEnabled(allStepsEnabled);
        this.setIndexProgress(indexProgress);
        this.setEnabledProgress(enabledProgress);
        this.setEnabledTraining(enabledTraining);
    }

    public static String getLevelName(int level) {
        return Options.getInstance().getDifficultyLevels()[level].getName();
    }

    public static String getLevelName(DifficultyLevel level) {
        return Options.getInstance().getDifficultyLevels()[level.getOrdinal()].getName();
    }

    @Override
    public String toString() {
        return this.type.getStepName();
    }

    public SolutionType getType() {
        return this.type;
    }

    public void setType(SolutionType type) {
        this.type = type;
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getBaseScore() {
        return this.baseScore;
    }

    public void setBaseScore(int baseScore) {
        this.baseScore = baseScore;
    }

    public int getAdminScore() {
        return this.adminScore;
    }

    public void setAdminScore(int adminScore) {
        this.adminScore = adminScore;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public SolutionCategory getCategory() {
        return this.category;
    }

    public void setCategory(SolutionCategory category) {
        this.category = category;
    }

    public String getCategoryName() {
        return this.category.getCategoryName();
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int compareTo(StepConfig o) {
        return this.index - o.getIndex();
    }

    public boolean isAllStepsEnabled() {
        return this.allStepsEnabled;
    }

    public void setAllStepsEnabled(boolean allStepsEnabled) {
        this.allStepsEnabled = allStepsEnabled;
    }

    public int getIndexProgress() {
        return this.indexProgress;
    }

    public void setIndexProgress(int indexProgress) {
        this.indexProgress = indexProgress;
    }

    public boolean isEnabledProgress() {
        return this.enabledProgress;
    }

    public void setEnabledProgress(boolean enabledProgress) {
        this.enabledProgress = enabledProgress;
    }

    public boolean isEnabledTraining() {
        return this.enabledTraining;
    }

    public void setEnabledTraining(boolean enabledTraining) {
        this.enabledTraining = enabledTraining;
    }
}
