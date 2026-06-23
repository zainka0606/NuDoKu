package sudoku;

import java.awt.*;
import java.awt.Desktop.Action;
import java.net.URI;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MyBrowserLauncher {
    private static MyBrowserLauncher instance = null;
    private static String HTTP_BASE = "http://hodoku.sourceforge.net/";
    private boolean httpSupported = false;

    private MyBrowserLauncher() {
        try {
            if (!Desktop.isDesktopSupported()) {
                this.httpSupported = false;
                return;
            }

            Desktop desktop = Desktop.getDesktop();
            if (desktop.isSupported(Action.BROWSE)) {
                this.httpSupported = true;
            }
        } catch (Exception ex) {
            Logger.getLogger(MyBrowserLauncher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static MyBrowserLauncher getInstance() {
        if (instance == null) {
            instance = new MyBrowserLauncher();
        }

        return instance;
    }

    public void launchUserManual() {
        String url = HTTP_BASE + "docs.php";
        this.browse(url);
    }

    public void launchSolvingGuide() {
        String url = HTTP_BASE + "techniques.php";
        this.browse(url);
    }

    public void launchHomePage() {
        String url = HTTP_BASE + "index.php";
        this.browse(url);
    }

    public void launchTracker() {
        String url = "http://sourceforge.net/p/hodoku/bugs-and-feature-requests/";
        this.browse(url);
    }

    public void launchForum() {
        String url = "http://sourceforge.net/p/hodoku/discussion/907403/";
        this.browse(url);
    }

    private void browse(String url) {
        if (this.httpSupported) {
            try {
                URI uri = new URI(url);
                Desktop.getDesktop().browse(uri);
            } catch (Exception ex) {
                Logger.getLogger(MyBrowserLauncher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
