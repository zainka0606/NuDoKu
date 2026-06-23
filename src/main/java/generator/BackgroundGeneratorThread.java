package generator;

import sudoku.*;

import java.util.logging.Level;
import java.util.logging.Logger;

public class BackgroundGeneratorThread implements Runnable {
    private static final boolean DEBUG = false;
    private static BackgroundGeneratorThread instance = null;
    private final Thread thread;
    private BackgroundGenerator generator;
    private boolean newRequest = false;
    private boolean threadStarted = false;

    private BackgroundGeneratorThread() {
        this.thread = new Thread(this);
        this.generator = new BackgroundGenerator();
    }

    public static BackgroundGeneratorThread getInstance() {
        if (instance == null) {
            instance = new BackgroundGeneratorThread();
        }

        return instance;
    }

    public synchronized String getSudoku(DifficultyLevel level, GameMode mode) {
        String[] puzzles = this.getPuzzleArray(level, mode);
        String newPuzzle = null;
        if (puzzles[0] != null) {
            newPuzzle = puzzles[0];

            for (int i = 1; i < puzzles.length; i++) {
                puzzles[i - 1] = puzzles[i];
            }

            puzzles[puzzles.length - 1] = null;
        }

        this.startCreation();
        return newPuzzle;
    }

    private synchronized void setSudoku(DifficultyLevel level, GameMode mode, String sudoku) {
        String[] puzzles = this.getPuzzleArray(level, mode);

        for (int i = 0; i < puzzles.length; i++) {
            if (puzzles[i] == null) {
                puzzles[i] = sudoku;
                break;
            }
        }
    }

    public synchronized void resetAll() {
        String[][] puzzles = Options.getInstance().getNormalPuzzles();

        for (int i = 0; i < puzzles.length; i++) {
            for (int j = 0; j < puzzles[i].length; j++) {
                puzzles[i][j] = null;
            }
        }

        this.resetTrainingPractising();
    }

    public synchronized void resetTrainingPractising() {
        String[] puzzles1 = Options.getInstance().getLearningPuzzles();

        for (int i = 0; i < puzzles1.length; i++) {
            puzzles1[i] = null;
        }

        puzzles1 = Options.getInstance().getPractisingPuzzles();

        for (int i = 0; i < puzzles1.length; i++) {
            puzzles1[i] = null;
        }

        this.startCreation();
    }

    public synchronized void setNewLevel(int newLevel) {
        int maxTrainingLevel = this.getTrainingLevel();
        if (maxTrainingLevel != -1 && newLevel >= maxTrainingLevel) {
            if (newLevel != Options.getInstance().getPractisingPuzzlesLevel()) {
                String[] puzzles = Options.getInstance().getPractisingPuzzles();

                for (int i = 0; i < puzzles.length; i++) {
                    puzzles[i] = null;
                }

                Options.getInstance().setPractisingPuzzlesLevel(newLevel);
                this.startCreation();
            }
        }
    }

    public void startCreation() {
        if (this.thread != null) {
            if (!this.threadStarted) {
                this.thread.start();
                this.threadStarted = true;
            }

            synchronized (this.thread) {
                this.newRequest = true;
                this.thread.notify();
            }
        }
    }

    @Override
    public void run() {
        while (!this.thread.isInterrupted()) {
            try {
                synchronized (this.thread) {
                    if (!this.newRequest) {
                        this.thread.wait();
                    }

                    if (!this.newRequest) {
                        continue;
                    }

                    this.newRequest = false;
                }

                DifficultyLevel level = null;

                for (GameMode mode = null; level == null && !this.thread.isInterrupted(); mode = null) {
                    synchronized (this) {
                        String[][] puzzles = Options.getInstance().getNormalPuzzles();

                        for (int i = 0; i < puzzles.length; i++) {
                            for (int j = 0; j < puzzles[i].length; j++) {
                                if (puzzles[i][j] == null) {
                                    level = Options.getInstance().getDifficultyLevel(i + 1);
                                    mode = GameMode.PLAYING;
                                    break;
                                }
                            }

                            if (level != null) {
                                break;
                            }
                        }

                        int trLevel = this.getTrainingLevel();
                        String[] puzzles1 = Options.getInstance().getLearningPuzzles();
                        if (level == null && trLevel != -1) {
                            for (int i = 0; i < puzzles1.length; i++) {
                                if (puzzles1[i] == null) {
                                    level = Options.getInstance().getDifficultyLevel(DifficultyType.EXTREME.ordinal());
                                    mode = GameMode.LEARNING;
                                    break;
                                }
                            }
                        }

                        if (trLevel != -1 && Options.getInstance().getPractisingPuzzlesLevel() == -1) {
                            this.setNewLevel(Options.getInstance().getActLevel());
                        }

                        puzzles1 = Options.getInstance().getPractisingPuzzles();
                        if (level == null && trLevel != -1 && Options.getInstance().getActLevel() >= trLevel) {
                            for (int i = 0; i < puzzles1.length; i++) {
                                if (puzzles1[i] == null) {
                                    level = Options.getInstance().getDifficultyLevel(Options.getInstance().getPractisingPuzzlesLevel());
                                    mode = GameMode.PRACTISING;
                                    break;
                                }
                            }
                        }
                    }

                    if (level == null) {
                        break;
                    }

                    String puzzle = this.generator.generate(level, mode);
                    if (puzzle == null) {
                        break;
                    }

                    this.setSudoku(level, mode, puzzle);
                    level = null;
                }
            } catch (InterruptedException ex) {
                this.thread.interrupt();
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "Error checking progress", ex);
            }
        }
    }

    private String[] getPuzzleArray(DifficultyLevel level, GameMode mode) {
        String[] puzzles = null;
        switch (mode) {
            case PLAYING:
                puzzles = Options.getInstance().getNormalPuzzles()[level.getOrdinal() - 1];
                break;
            case LEARNING:
                puzzles = Options.getInstance().getLearningPuzzles();
                break;
            case PRACTISING:
                puzzles = Options.getInstance().getPractisingPuzzles();
        }

        return puzzles;
    }

    private int getTrainingLevel() {
        StepConfig[] conf = Options.getInstance().getOrgSolverSteps();
        int level = -1;

        for (StepConfig act : conf) {
            if (act.isEnabledTraining()) {
                int actLevel = act.getLevel();
                if (actLevel > level) {
                    level = actLevel;
                }
            }
        }

        return level;
    }
}
