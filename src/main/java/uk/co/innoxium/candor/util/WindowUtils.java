package uk.co.innoxium.candor.util;

import uk.co.innoxium.candor.Settings;
import uk.co.innoxium.candor.game.Game;
import uk.co.innoxium.candor.game.GamesList;
import uk.co.innoxium.candor.mod.store.ModStore;
import uk.co.innoxium.candor.module.AbstractModule;
import uk.co.innoxium.candor.module.ModuleSelector;
import uk.co.innoxium.candor.window.EntryScene;
import uk.co.innoxium.candor.window.GameSelectScene;
import uk.co.innoxium.candor.window.ModScene;
import uk.co.innoxium.cybernize.util.ClassLoadUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class WindowUtils {

    public static JFrame mainFrame = new JFrame();

    public static void initialiseFrame(boolean showIntro) {

        File game = new File(Settings.gameExe);
        AbstractModule module = ModuleSelector.getModuleForGame(game);
        module.setGame(game);
        module.setModsFolder(new File(Settings.modsFolder));
        ModStore.initialise();
        mainFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        mainFrame.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {

                boolean result = Dialogs.showInfoDialog(
                        "Candor Mod Manager",
                        "Are you sure you wish to exit?",
                        "yesno",
                        "question",
                        false);
                if(result) System.exit(0);
            }
        });
        mainFrame.setResizable(false);
        mainFrame.setTitle("Candor Mod Manager");
        mainFrame.setIconImage(new ImageIcon(ClassLoadUtil.getCL().getResource("logo.png")).getImage());
        JPanel scene = showIntro ? new EntryScene() : new ModScene();
        Resources.currentScene = scene;
        mainFrame.setContentPane(scene);
        // TODO: Allow the window to stay on the same screen it was used on
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public static void setupModScene(Game game) {

        AbstractModule module = ModuleSelector.currentModule;
        GamesList.addGame(game);
        module.setGame(new File(game.getGameExe()));
        module.setModsFolder(new File(game.getModsFolder()));
        ModStore.initialise();
        mainFrame.setVisible(false);
        mainFrame.setResizable(true);
        ModScene modScene = new ModScene();
        Resources.currentScene = modScene;
        mainFrame.setContentPane(modScene);
        mainFrame.setMinimumSize(new Dimension(1200, 768));
        // TODO: Allow the window to stay on the same screen it was used on
        WindowUtils.mainFrame.pack();
        WindowUtils.mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }

    public static void setupGameSelectScene() {

//        mainFrame.setLocationByPlatform(true);
        mainFrame.setVisible(false);
        mainFrame.setResizable(false);
        mainFrame.setMinimumSize(new Dimension(435, 300));
        GameSelectScene scene = new GameSelectScene();
        scene.initComponents();
        Resources.currentScene = scene;
        mainFrame.setContentPane(scene);
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setVisible(true);
    }
}
