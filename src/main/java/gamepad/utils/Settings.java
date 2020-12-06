package gamepad.utils;

import java.io.File;

public class Settings {

    // Editor Settings
    public static int GRID_WIDTH = 32;
    public static int GRID_HEIGHT = 32;

    // File Settings
    public static final File HOME_DIRECTORY = new File(System.getProperty("user.home"));
    public static final File GAMEPAD_DIRECTORY = new File(HOME_DIRECTORY + File.separator + ".gamepad");
    public static final File INI_SAVE_LOCATION = new File(GAMEPAD_DIRECTORY + File.separator + "editorcache.dat");

    static {
        if(!HOME_DIRECTORY.exists()) HOME_DIRECTORY.mkdirs();
        if(!GAMEPAD_DIRECTORY.exists()) GAMEPAD_DIRECTORY.mkdirs();
    }

}
