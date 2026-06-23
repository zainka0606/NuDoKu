package generator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SudokuGeneratorFactory {
    private static final SudokuGenerator defaultGenerator = new SudokuGenerator();
    private static final long GENERATOR_TIMEOUT = 300000L;
    private static List<SudokuGeneratorFactory.generatorInstance> instances = new ArrayList<>();    private static final Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                synchronized (SudokuGeneratorFactory.thread) {
                    Iterator<SudokuGeneratorFactory.generatorInstance> iterator = SudokuGeneratorFactory.instances.iterator();

                    while (iterator.hasNext()) {
                        SudokuGeneratorFactory.generatorInstance act = iterator.next();
                        if (!act.inUse && System.currentTimeMillis() - act.lastUsedAt > 300000L) {
                            iterator.remove();
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

    private SudokuGeneratorFactory() {
    }

    public static SudokuGenerator getDefaultGeneratorInstance() {
        return defaultGenerator;
    }

    public static SudokuGenerator getInstance() {
        SudokuGenerator ret = null;
        synchronized (thread) {
            for (SudokuGeneratorFactory.generatorInstance act : instances) {
                if (!act.inUse) {
                    act.inUse = true;
                    ret = act.instance;
                    break;
                }
            }

            if (ret == null) {
                ret = new SudokuGenerator();
                instances.add(new SudokuGeneratorFactory.generatorInstance(ret));
            }

            return ret;
        }
    }

    public static void giveBack(SudokuGenerator generator) {
        synchronized (thread) {
            for (SudokuGeneratorFactory.generatorInstance act : instances) {
                if (act.instance == generator) {
                    act.inUse = false;
                    act.lastUsedAt = System.currentTimeMillis();
                    break;
                }
            }
        }
    }

    private static class generatorInstance {
        SudokuGenerator instance = null;
        boolean inUse = true;
        long lastUsedAt = -1L;

        private generatorInstance(SudokuGenerator instance) {
            this.instance = instance;
        }
    }


}
