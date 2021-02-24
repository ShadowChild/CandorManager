package uk.co.innoxium.candor;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import org.lwjgl.system.Platform;
import uk.co.innoxium.candor.game.GamesList;
import uk.co.innoxium.candor.module.ModuleSelector;
import uk.co.innoxium.candor.tool.ToolsList;
import uk.co.innoxium.candor.util.Logger;
import uk.co.innoxium.candor.util.Resources;
import uk.co.innoxium.candor.util.WindowUtils;
import uk.co.innoxium.cybernize.zip.ZipUtils;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;


/**
 * The entry point for candor!
 */
public class CandorLauncher {

    public static void main(String[] args) {

        // Initialise our Logger
        Logger.initialise();

        Logger.info("Candor Initialising");
        // Ensure the 7zip natives are loaded
        ZipUtils.setUpSevenZip();

        if(Platform.get() == Platform.MACOSX) {

            // We want to restart the application here with the -XstartOnFirstThread JVM argument
            // This is slightly more difficult for us as we have the java agent to deal with
        }

        // Get a plash screen instance, currently, we don't do any manipulation, this may be increased in the future.
        SplashScreen splash = SplashScreen.getSplashScreen();
        if(splash != null) {

            Graphics2D g = splash.createGraphics();

            g.setPaintMode();
            g.setFont(new Font("Calibri", Font.PLAIN, 25));
            g.drawString("Loading Candor Resources", splash.getBounds().width / 2, splash.getBounds().height / 2);

            splash.update();
        }

        try {

            // We next want to install our fonts,
            Resources.installFonts();
        } catch(IOException | FontFormatException e) {

            // Print stack if it fails.
            e.printStackTrace();
        }

        try {

            // Check if the generic module exists, if not download it.
            ModuleSelector.checkGenericModule();
            // Load modules from disk.
            ModuleSelector.initModules();
            // Load the lists of user set up games.
            GamesList.loadFromFile();
        } catch(Exception e) {

            // Probably from IDE, ignore for now
            e.printStackTrace();
        }
        // Handle our configurations
        ConfigHandler.handleCore();
        // Install our Look and Feel.
        if(Settings.darkTheme) {

            FlatDarculaLaf.install();
        } else {

            FlatIntelliJLaf.install();
        }
        //
        setThemeCustomizations();
        // Add our shutdown hook for saving configs
        Runtime.getRuntime().addShutdownHook(new RuntimeHook());

        // Close the splash screen now
        if(splash != null) splash.close();

        // We can now set up the frame.
        WindowUtils.initialiseFrame();
    }

    private static void setThemeCustomizations() {

        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }

    private static class RuntimeHook extends Thread {

        @Override
        public void run() {

            try {

                // Write the games list to file
                GamesList.writeToFile();
                ToolsList.writeToJson();
            } catch(IOException e) {

                e.printStackTrace();
            }
            Logger.info("Candor shutting down.");
        }
    }
}