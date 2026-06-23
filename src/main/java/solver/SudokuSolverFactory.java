package solver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SudokuSolverFactory {
    private static final SudokuSolver defaultSolver = new SudokuSolver();
    private static final long SOLVER_TIMEOUT = 300000L;
    private static List<SudokuSolverFactory.SolverInstance> instances = new ArrayList<>();    private static final Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                synchronized (SudokuSolverFactory.thread) {
                    SudokuSolverFactory.defaultSolver.getStepFinder().cleanUp();
                    Iterator<SudokuSolverFactory.SolverInstance> iterator = SudokuSolverFactory.instances.iterator();

                    while (iterator.hasNext()) {
                        SudokuSolverFactory.SolverInstance act = iterator.next();
                        if (!act.inUse && System.currentTimeMillis() - act.lastUsedAt > 300000L) {
                            iterator.remove();
                        } else {
                            act.instance.getStepFinder().cleanUp();
                        }
                    }
                }

                try {
                    Thread.sleep(300000L);
                } catch (InterruptedException ex) {
                }
            }
        }
    });

    static {
        thread.start();
    }

    private SudokuSolverFactory() {
    }

    public static SudokuSolver getDefaultSolverInstance() {
        return defaultSolver;
    }

    public static SudokuSolver getInstance() {
        SudokuSolver ret = null;
        synchronized (thread) {
            for (SudokuSolverFactory.SolverInstance act : instances) {
                if (!act.inUse) {
                    act.inUse = true;
                    ret = act.instance;
                    break;
                }
            }

            if (ret == null) {
                ret = new SudokuSolver();
                instances.add(new SudokuSolverFactory.SolverInstance(ret));
            }

            return ret;
        }
    }

    public static void giveBack(SudokuSolver solver) {
        synchronized (thread) {
            for (SudokuSolverFactory.SolverInstance act : instances) {
                if (act.instance == solver) {
                    act.inUse = false;
                    act.lastUsedAt = System.currentTimeMillis();
                    break;
                }
            }
        }
    }

    private static class SolverInstance {
        SudokuSolver instance = null;
        boolean inUse = true;
        long lastUsedAt = -1L;

        private SolverInstance(SudokuSolver instance) {
            this.instance = instance;
        }
    }


}
