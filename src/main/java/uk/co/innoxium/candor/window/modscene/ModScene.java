/*
 * Created by JFormDesigner on Mon Jun 22 16:06:01 BST 2020
 */

package uk.co.innoxium.candor.window.modscene;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.extras.FlatAnimatedLafChange;
import com.github.f4b6a3.uuid.util.UuidConverter;
import net.miginfocom.swing.MigLayout;
import uk.co.innoxium.candor.CandorLauncher;
import uk.co.innoxium.candor.Settings;
import uk.co.innoxium.candor.game.Game;
import uk.co.innoxium.candor.game.GamesList;
import uk.co.innoxium.candor.mod.Mod;
import uk.co.innoxium.candor.mod.ModList;
import uk.co.innoxium.candor.mod.handler.ModInstallationHandler;
import uk.co.innoxium.candor.mod.store.ModStore;
import uk.co.innoxium.candor.module.AbstractModule;
import uk.co.innoxium.candor.module.ModuleSelector;
import uk.co.innoxium.candor.module.RunConfig;
import uk.co.innoxium.candor.process.ProcessLauncher;
import uk.co.innoxium.candor.util.Logger;
import uk.co.innoxium.candor.util.NativeDialogs;
import uk.co.innoxium.candor.util.Resources;
import uk.co.innoxium.candor.util.WindowUtils;
import uk.co.innoxium.candor.window.AboutDialog;
import uk.co.innoxium.candor.window.ManageRunConfigs;
import uk.co.innoxium.candor.window.RunConfigDialog;
import uk.co.innoxium.candor.window.component.RunConfigMenuItem;
import uk.co.innoxium.candor.window.dnd.mod.ModListFileTransferHandler;
import uk.co.innoxium.candor.window.tool.ToolAddWindow;
import uk.co.innoxium.swing.util.DesktopUtil;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;


public class ModScene extends JPanel {

    private final LinkedList<Mod> queuedMods = new LinkedList<>();
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel managerPanel;
    private JPanel managerPaneMenu;
    private JLabel gameLabel;
    private JButton addModButton;
    private JButton removeModsButton;
    private JButton installModsButton;
    private JButton toggleButton;
    private JScrollPane tableScrollPane;
    private JTable modsTable;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenuItem applyModsMenuItem;
    private JMenuItem loadNewGameMenuItem;
    private JMenuItem settingsMenuItem;
    private JRadioButtonMenuItem darkThemeRadioButton;
    private JMenu gameMenu;
    private JMenuItem openGameFolderMenuItem;
    private JMenuItem opemModsFolderMenuItem;
    private JMenu launchMenu;
    private JMenuItem launchGameMenuItem;
    private JMenuItem addLaunchConfigMenuItem;
    private JMenuItem runConfigsMenuItem;
    private JMenu aboutMenu;
    private JMenuItem aboutMenuItem;
    private JMenuItem candorSettingButton;
    private JMenu toolsMenu;
    private JMenuItem addToolItem;
    private JMenu moduleMenu;
    private JMenu helpMenu;
    private JMenuItem reloadModsMenuItem;
    private JMenuItem reloadModulesMenuItem;
    private JMenuItem reinstallMenuItem;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public ModScene(String gameUuid) {

        // Set game stuff
        Game game = GamesList.getGameFromUUID(UuidConverter.fromString(gameUuid));
        assert game != null;
        AbstractModule module = ModuleSelector.getModuleForGame(game);
        module.setGame(new File(game.getGameExe()));
        module.setModsFolder(new File(game.getModsFolder()));
        ModStore.initialise();
        Settings.lastGameUuid = gameUuid;

        try {

            WindowUtils.mainFrame.setMinimumSize(new Dimension(1200, 768));
            ModStore.determineInstalledMods();
        } catch(IOException e) {

            Logger.info("This shouldn't happen, likely a corrupt mods.json :(");
            e.printStackTrace();
            CandorLauncher.safeExit(-1);
        }
        initComponents();
        WindowUtils.mainFrame.setJMenuBar(menuBar);
        ModStore.MODS.addListener(new ModList.ListChangeListener<>() {

            @Override
            public void handleChange(String identifier, Mod mod, boolean result) {

                handleChange(identifier, Collections.singletonList(mod), result);
            }

            @Override
            public void handleChange(String identifier, Collection<? extends Mod> c, boolean result) {

                ((AbstractTableModel)modsTable.getModel()).fireTableDataChanged();
//                c.forEach(mod -> {
//
//                    if(identifier.equals("install")) {
//
//                        queuedMods.removeFirst();
//                        if(queuedMods.size() > 0) {
//
//                            queuedMods.getFirst().start();
//                        }
//                    }
//                });
            }
        });
    }

    private void createUIComponents() {

        darkThemeRadioButton = new JRadioButtonMenuItem("Enable Dark Theme", Settings.darkTheme);

        modsTable = new JTable(new ModsTableModel());
        modsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        modsTable.setDragEnabled(true);
        modsTable.setFont(Resources.getFantasque(20f));
        modsTable.setShowGrid(true);
        // Having autoSort messes with indices which i cba to deal with rn
//        modsTable.setAutoCreateRowSorter(true);
        modsTable.addMouseListener(new ModSceneMouseAdapter(this));
    }

    private void settingsClicked(ActionEvent e) {

        // Disable while we don't have enough settings
//        JDialog dialog = new SettingsFrame();
//        dialog.setAlwaysOnTop(true);
//        dialog.setLocationRelativeTo(this);
//        dialog.setVisible(true);
    }

    private void addModClicked(ActionEvent e) {

        NativeDialogs.openMultiFileDialog(ModuleSelector.currentModule.getModFileFilterList()).forEach(file -> {

            ModStore.Result result = ModStore.addModFile(file);

            switch(result) {

                case DUPLICATE -> NativeDialogs.showErrorMessage("Mod is a Duplicate and already installed.\nIf updating, please uninstall old file first.");
                case FAIL -> NativeDialogs.showErrorMessage(String.format("Mod file %s could not be added.\nPlease try again.", file.getName()));
                // Fallthrough on default
                default -> {
                }
            }
        });
    }

    private void removeModsSelected(ActionEvent e) {

        if(NativeDialogs.showConfirmDialog("Remove Selected Mods")) {

            if(modsTable.getSelectedRows().length == 0) {

                NativeDialogs.showInfoDialog(
                        "Candor Mod Manager",
                        "You have not selected any mods to remove.",
                        "ok",
                        "warning",
                        false);
            } else {

                int[] selectedRows = modsTable.getSelectedRows();
                for(int i : selectedRows) {

                    try {

                        ModStore.removeModFile(ModStore.MODS.getAtIndex(i), true);
                    } catch(IOException ioException) {

                        ioException.printStackTrace();
                    }
                }
            }
        }
    }

    private void installModsClicked(ActionEvent e) {

        if(modsTable.getSelectedRows().length > 0) {

            ArrayList<Mod> mods = new ArrayList<>();
            for(int i : modsTable.getSelectedRows()) {

                mods.add(ModStore.MODS.getAtIndex(i));
            }
            doInstallMod(mods);
        } else {

            NativeDialogs.showInfoDialog("Candor Mod Manager",
                    "You have not selected any mods to install.",
                    "ok",
                    "info",
                    true);
        }
    }

    private void runGameClicked(ActionEvent e) {

        // TODO: this has been updated, baby steps towards decoupling code from the UI
        RunConfig runConfig = ModuleSelector.currentModule.getDefaultRunConfig();

        try {

            Process process = ProcessLauncher.startProcess(runConfig);
        } catch(IOException ioException) {

            ioException.printStackTrace();
        }
    }

    // TODO: Add support for modules to determine how to toggle mods, e.g. via a plugin list for GameBryo games
    // Use toggleSelectedModsTable for the time being until table ui is finished
    @Deprecated
    public void toggleSelectedMods(ActionEvent e) {

        toggleSelectedModsTable(e);
    }

    public void toggleSelectedModsTable(ActionEvent e) {

        if(NativeDialogs.showConfirmDialog("Toggle Selected Mods")) {

            int[] selectedRows = modsTable.getSelectedRows();
            int column = modsTable.getSelectedColumn();
            ArrayList<Mod> toInstall = new ArrayList<>();
            ArrayList<Mod> toUninstall = new ArrayList<>();
            for(int i : selectedRows) {

                Mod mod = ModStore.MODS.getAtIndex(i);
                if(mod != null) {

                    // Check if we should uninstall
                    if(mod.getState() == Mod.State.ENABLED) {

                        toUninstall.add(mod);
                    } else {

                        // Else add to mod install queue
                        toInstall.add(mod);
                    }
                }
                // Attempt to install any mods we toggled
                if(toInstall.size() > 0) doInstallMod(toInstall);
                // Then attempt to uninstall any selected mods
                if(toUninstall.size() > 0) doUninstallMods(toUninstall);
            }
        }
    }

    public void doInstallMod(ArrayList<Mod> mods) {

        // Any mods in here will enable the message box saying it was installed
        ArrayList<Mod> badMods = new ArrayList<>();

        // Instantiate a Thread for each mod selected
        mods.forEach(mod -> {

            if(mod.getState() == Mod.State.ENABLED) {

                badMods.add(mod);
                mods.remove(mod);
            }
        });

        if(badMods.size() > 0) {

            StringBuilder builder = new StringBuilder();
            builder.append("The following mods are already installed and will be ignored: \n");
            for(Mod badMod : badMods) {

                builder.append(badMod.getReadableName()).append("\n");
            }

            NativeDialogs.showInfoDialog(
                    "Candor Mod Manager",
                    builder.toString(),
                    "ok",
                    "info",
                    true);
        }

        // install the mods one by one
        if(mods.size() > 0)
            new ModInstallationHandler(new LinkedList<>(mods)).installMods().join();
    }

    private void doUninstallMods(ArrayList<Mod> toUninstall) {

        toUninstall.forEach(mod -> {

            if(ModuleSelector.currentModule.getModInstaller().uninstall(mod)) {

                ModStore.updateModState(mod, Mod.State.DISABLED);
                ModStore.MODS.fireChangeToListeners("uninstall", mod, true);
                mod.setState(Mod.State.DISABLED);
                ((AbstractTableModel)modsTable.getModel()).fireTableDataChanged();
            }
        });
    }

    private void newGameClicked(ActionEvent e) {

        WindowUtils.setupEntryScene();
//        WindowUtils.setupGameSelectScene();
    }

    private void aboutClicked(ActionEvent e) {

        AboutDialog dialog = new AboutDialog();
        dialog.setResizable(true);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void openFolder(ActionEvent e) {

        switch(e.getActionCommand()) {

            case "game" -> DesktopUtil.openURL("", ModuleSelector.currentModule.getGame().getParent());
            case "mods" -> DesktopUtil.openURL("", ModuleSelector.currentModule.getModsFolder().getAbsolutePath());
            case "candor" -> DesktopUtil.openURL("", Resources.CANDOR_DATA_PATH.getAbsolutePath());
            default -> {
            }
        }
    }

    private void themeChangeButtonClicked(ActionEvent e) {

        if(!darkThemeRadioButton.isSelected()) {

            try {

                FlatAnimatedLafChange.showSnapshot();
                UIManager.setLookAndFeel(new FlatIntelliJLaf());
                UIManager.getLookAndFeelDefaults().put("defaultFont", Resources.orbitron.deriveFont(Font.PLAIN, 14));
                FlatLaf.updateUI();
                FlatAnimatedLafChange.hideSnapshotWithAnimation();
            } catch(UnsupportedLookAndFeelException exception) {

                exception.printStackTrace();
            }
        } else {

            try {

                FlatAnimatedLafChange.showSnapshot();
                UIManager.setLookAndFeel(new FlatDarculaLaf());
                UIManager.getLookAndFeelDefaults().put("defaultFont", Resources.orbitron.deriveFont(Font.PLAIN, 14));
                FlatLaf.updateUI();
                FlatAnimatedLafChange.hideSnapshotWithAnimation();
            } catch(UnsupportedLookAndFeelException exception) {

                exception.printStackTrace();
            }
        }
        Settings.darkTheme = darkThemeRadioButton.isSelected();
    }

    private void addToolClicked(ActionEvent e) {

        ToolAddWindow window = new ToolAddWindow();
        window.setVisible(true);
    }

    private void addLaunchConfig(ActionEvent e) {

        RunConfigDialog dialog = new RunConfigDialog(WindowUtils.mainFrame);
        dialog.setVisible(true);
    }

    private void manageRunConfigsClicked(ActionEvent e) {

        ManageRunConfigs manageDialog = new ManageRunConfigs(this, WindowUtils.mainFrame);
        manageDialog.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        createUIComponents();

        managerPanel = new JPanel();
        managerPaneMenu = new JPanel();
        gameLabel = new JLabel();
        addModButton = new JButton();
        removeModsButton = new JButton();
        installModsButton = new JButton();
        toggleButton = new JButton();
        tableScrollPane = new JScrollPane();
        menuBar = new JMenuBar();
        fileMenu = new JMenu();
        applyModsMenuItem = new JMenuItem();
        loadNewGameMenuItem = new JMenuItem();
        settingsMenuItem = new JMenuItem();
        gameMenu = new JMenu();
        openGameFolderMenuItem = new JMenuItem();
        opemModsFolderMenuItem = new JMenuItem();
        launchMenu = new JMenu();
        launchGameMenuItem = new JMenuItem();
        addLaunchConfigMenuItem = new JMenuItem();
        runConfigsMenuItem = new JMenuItem();
        aboutMenu = new JMenu();
        aboutMenuItem = new JMenuItem();
        candorSettingButton = new JMenuItem();
        toolsMenu = new JMenu();
        addToolItem = new JMenuItem();
        moduleMenu = new JMenu();
        helpMenu = new JMenu();
        reloadModsMenuItem = new JMenuItem();
        reloadModulesMenuItem = new JMenuItem();
        reinstallMenuItem = new JMenuItem();

        //======== this ========
        setLayout(new BorderLayout());

        //======== managerPanel ========
        {
            managerPanel.setLayout(new MigLayout(
                "fill,insets panel,hidemode 3",
                // columns
                "[fill]",
                // rows
                "[fill]" +
                "[fill]"));

            //======== managerPaneMenu ========
            {
                managerPaneMenu.setLayout(new FlowLayout(FlowLayout.LEFT));

                //---- gameLabel ----
                gameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                managerPaneMenu.add(gameLabel);

                //---- addModButton ----
                addModButton.setText("Add Mod(s)");
                addModButton.setIcon(null);
                addModButton.addActionListener(e -> addModClicked(e));
                managerPaneMenu.add(addModButton);

                //---- removeModsButton ----
                removeModsButton.setText("Remove Selected");
                removeModsButton.setIcon(null);
                removeModsButton.setMaximumSize(new Dimension(173, 30));
                removeModsButton.setMinimumSize(new Dimension(173, 30));
                removeModsButton.addActionListener(e -> removeModsSelected(e));
                managerPaneMenu.add(removeModsButton);

                //---- installModsButton ----
                installModsButton.setText("Install Selected Mod(s)");
                installModsButton.setIcon(null);
                installModsButton.addActionListener(e -> installModsClicked(e));
                managerPaneMenu.add(installModsButton);

                //---- toggleButton ----
                toggleButton.setText("Toggle Enabled");
                toggleButton.addActionListener(e -> toggleSelectedMods(e));
                managerPaneMenu.add(toggleButton);
            }
            managerPanel.add(managerPaneMenu, "cell 0 0");

            //======== tableScrollPane ========
            {
                tableScrollPane.setViewportView(modsTable);
            }
            managerPanel.add(tableScrollPane, "cell 0 1,dock center");
        }
        add(managerPanel, BorderLayout.CENTER);

        //======== menuBar ========
        {

            //======== fileMenu ========
            {
                fileMenu.setText("File");
                fileMenu.setMnemonic('F');

                //---- applyModsMenuItem ----
                applyModsMenuItem.setText("Apply Mod(s)");
                applyModsMenuItem.setMnemonic('A');
                fileMenu.add(applyModsMenuItem);

                //---- loadNewGameMenuItem ----
                loadNewGameMenuItem.setText("Load New Game");
                loadNewGameMenuItem.setMnemonic('L');
                loadNewGameMenuItem.addActionListener(e -> newGameClicked(e));
                fileMenu.add(loadNewGameMenuItem);

                //---- settingsMenuItem ----
                settingsMenuItem.setText("Settings");
                settingsMenuItem.setMnemonic('S');
                settingsMenuItem.setEnabled(false);
                settingsMenuItem.setToolTipText("Feature disabled currently, unfinished");
                settingsMenuItem.addActionListener(e -> settingsClicked(e));
                fileMenu.add(settingsMenuItem);

                //---- darkThemeRadioButton ----
                darkThemeRadioButton.addActionListener(e -> themeChangeButtonClicked(e));
                fileMenu.add(darkThemeRadioButton);
            }
            menuBar.add(fileMenu);

            //======== gameMenu ========
            {
                gameMenu.setText("Game");
                gameMenu.setMnemonic('G');

                //---- openGameFolderMenuItem ----
                openGameFolderMenuItem.setText("Open Game Folder");
                openGameFolderMenuItem.setActionCommand("game");
                openGameFolderMenuItem.setMnemonic('G');
                openGameFolderMenuItem.addActionListener(e -> openFolder(e));
                gameMenu.add(openGameFolderMenuItem);

                //---- opemModsFolderMenuItem ----
                opemModsFolderMenuItem.setText("Open Mods Folder");
                opemModsFolderMenuItem.setActionCommand("mods");
                opemModsFolderMenuItem.setMnemonic('M');
                opemModsFolderMenuItem.addActionListener(e -> openFolder(e));
                gameMenu.add(opemModsFolderMenuItem);
            }
            menuBar.add(gameMenu);

            //======== launchMenu ========
            {
                launchMenu.setText("Launch");
                launchMenu.setMnemonic('L');

                //---- launchGameMenuItem ----
                launchGameMenuItem.setText("Launch Game");
                launchGameMenuItem.setMnemonic('L');
                launchGameMenuItem.addActionListener(e -> runGameClicked(e));
                launchMenu.add(launchGameMenuItem);

                //---- addLaunchConfigMenuItem ----
                addLaunchConfigMenuItem.setText("Add Launch Config");
                addLaunchConfigMenuItem.setMnemonic('A');
                addLaunchConfigMenuItem.addActionListener(e -> addLaunchConfig(e));
                launchMenu.add(addLaunchConfigMenuItem);
                launchMenu.addSeparator();

                //---- runConfigsMenuItem ----
                runConfigsMenuItem.setText("Manage Run Config(s)");
                runConfigsMenuItem.setMnemonic('M');
                runConfigsMenuItem.addActionListener(e -> manageRunConfigsClicked(e));
                launchMenu.add(runConfigsMenuItem);
            }
            menuBar.add(launchMenu);

            //======== aboutMenu ========
            {
                aboutMenu.setText("About");
                aboutMenu.setMnemonic('A');

                //---- aboutMenuItem ----
                aboutMenuItem.setText("About Candor");
                aboutMenuItem.setMnemonic('A');
                aboutMenuItem.addActionListener(e -> aboutClicked(e));
                aboutMenu.add(aboutMenuItem);

                //---- candorSettingButton ----
                candorSettingButton.setText("Open Candor Folder");
                candorSettingButton.setActionCommand("candor");
                candorSettingButton.setMnemonic('O');
                candorSettingButton.addActionListener(e -> openFolder(e));
                aboutMenu.add(candorSettingButton);
            }
            menuBar.add(aboutMenu);

            //======== toolsMenu ========
            {
                toolsMenu.setText("Tools");
                toolsMenu.setMnemonic('T');

                //---- addToolItem ----
                addToolItem.setText("Add Tool");
                addToolItem.setToolTipText("Disabled for now, feature unfinished");
                addToolItem.addActionListener(e -> addToolClicked(e));
                toolsMenu.add(addToolItem);
            }
            menuBar.add(toolsMenu);

            //======== moduleMenu ========
            {
                moduleMenu.setText("Module");
            }
            menuBar.add(moduleMenu);

            //======== helpMenu ========
            {
                helpMenu.setText("Help");
                helpMenu.setMnemonic('H');

                //---- reloadModsMenuItem ----
                reloadModsMenuItem.setText("Reload Mods from Disk");
                reloadModsMenuItem.setEnabled(false);
                helpMenu.add(reloadModsMenuItem);

                //---- reloadModulesMenuItem ----
                reloadModulesMenuItem.setText("Reload Modules");
                reloadModulesMenuItem.setEnabled(false);
                helpMenu.add(reloadModulesMenuItem);

                //---- reinstallMenuItem ----
                reinstallMenuItem.setText("Re-install mods");
                reinstallMenuItem.setEnabled(false);
                helpMenu.add(reinstallMenuItem);
            }
            menuBar.add(helpMenu);
        }
        add(menuBar, BorderLayout.NORTH);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        postCreate();
    }

    private void postCreate() {

        // Set the label text
        gameLabel.setText(ModuleSelector.currentModule.getReadableGameName(GamesList.getGameFromUUID(UuidConverter.fromString(Settings.lastGameUuid))).toUpperCase(Locale.ROOT));
        // Add the Drag'n'Drop handler
        modsTable.setTransferHandler(new ModListFileTransferHandler());
//        installedModsJList.setTransferHandler(new ModListFileTransferHandler());
        // Add the run configs to a menu item
        Game game = GamesList.getCurrentGame();
        game.customLaunchConfigs.forEach(this::addNewRunConf);

        ModuleSelector.currentModule.getMenuItems().forEach(moduleMenu::add);
    }

    public JMenu getToolsMenu() {

        return toolsMenu;
    }

    public void addNewRunConf(RunConfig conf) {

        launchMenu.add(new RunConfigMenuItem(conf));
    }

    public void refreshRunConfigs() {

        for(Component menuItem : launchMenu.getMenuComponents()) {

            if(menuItem instanceof RunConfigMenuItem) {

                Logger.info("Removing menu item: " + ((RunConfigMenuItem) menuItem).getRunConfig().getRunConfigName());
                launchMenu.remove(menuItem);
            }
        }
        Game game = GamesList.getCurrentGame();
        game.customLaunchConfigs.forEach(this::addNewRunConf);
    }
}
