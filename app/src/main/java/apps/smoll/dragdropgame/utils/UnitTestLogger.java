package apps.smoll.dragdropgame.utils;

public class UnitTestLogger extends Logger{

    @Override
    public void e(String tag, String message) {
        System.out.println("E " + tag + ": " + message);
    }

    // similar for other methods
}