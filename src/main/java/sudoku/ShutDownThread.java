package sudoku;

class ShutDownThread extends Thread {
    private Thread thread;

    ShutDownThread(Thread thread) {
        this.thread = thread;
    }

    @Override
    public void run() {
        this.thread.interrupt();
    }
}
