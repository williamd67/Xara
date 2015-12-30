package nl.marayla.Xara.Platform;


public class XaraLog {
    public static boolean DEBUG = false;

    public static XaraLog log = new XaraLog();

    public void v(String module, String message) {}
    public void d(String module, String message) {}
    public void e(String module, String message) {}
}