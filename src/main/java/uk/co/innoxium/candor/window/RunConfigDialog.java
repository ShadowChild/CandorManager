/*
 * Created by JFormDesigner on Sun Mar 28 20:16:50 BST 2021
 */

package uk.co.innoxium.candor.window;

import net.miginfocom.swing.MigLayout;
import uk.co.innoxium.candor.game.Game;
import uk.co.innoxium.candor.game.GamesList;
import uk.co.innoxium.candor.module.RunConfig;
import uk.co.innoxium.candor.util.NativeDialogs;
import uk.co.innoxium.candor.util.Resources;
import uk.co.innoxium.candor.window.dialog.SwingDialogs;
import uk.co.innoxium.candor.window.modscene.ModScene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;


/**
 * @author Zach Piddock
 */
public class RunConfigDialog extends JDialog {

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JLabel nameLabel;
    private JTextField nameField;
    private JLabel programSelectLabel;
    private JTextField programSelectField;
    private JButton programSelectBrowseButton;
    private JLabel argsLabel;
    private JTextField argsField;
    private JPanel buttonBar;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private void okClicked(ActionEvent e) {

        if(verifyStatus()) {

            RunConfig conf = new RunConfig(nameField.getText());
            conf.setStartCommand(programSelectField.getText());
            if(!argsField.getText().isEmpty()) conf.setProgramArgs(argsField.getText());
            GamesList.getCurrentGame().addRunConfig(conf);

            // Add it to the Menu
            assert Resources.currentScene instanceof ModScene;
            ModScene modScene = (ModScene)Resources.currentScene;
            modScene.addNewRunConf(conf);
        } else {

            SwingDialogs.showInfoMessage("Candor Mod Manager", "You have not selected a game.", JOptionPane.WARNING_MESSAGE);
        }
        this.dispose();
    }

    private boolean verifyStatus() {

        return !nameField.getText().isEmpty() && !programSelectField.getText().isEmpty();
    }

    private void browseProgram(ActionEvent e) {

        Game currGame = GamesList.getCurrentGame();
        File file = NativeDialogs.showSingleFileDialog("exe", new File(currGame.getGameExe()).getParentFile());
        if(file != null) {

            try {

                this.programSelectField.setText(file.getCanonicalPath());
            } catch(IOException ioException) {

                // This should never happen as it was chosen with a fle browser
                ioException.printStackTrace();
            }
        }
    }

    private void initComponents() {

        preCreate();
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        nameLabel = new JLabel();
        nameField = new JTextField();
        programSelectLabel = new JLabel();
        programSelectField = new JTextField();
        programSelectBrowseButton = new JButton();
        argsLabel = new JLabel();
        argsField = new JTextField();
        buttonBar = new JPanel();
        okButton = new JButton();

        //======== this ========
        setTitle("Add Run Config");
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new MigLayout(
                    "fillx,insets dialog,hidemode 3",
                    // columns
                    "[fill]" +
                    "[fill]",
                    // rows
                    "[]" +
                    "[]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]"));

                //---- nameLabel ----
                nameLabel.setText("Enter a name for this config.");
                contentPanel.add(nameLabel, "cell 0 0,growy,gapy 5 5");
                contentPanel.add(nameField, "cell 0 1 2 1");

                //---- programSelectLabel ----
                programSelectLabel.setText("Locate the program to run.");
                programSelectLabel.setToolTipText("By default this is the game executable");
                contentPanel.add(programSelectLabel, "cell 0 2,growy,gapy 5 5");

                //---- programSelectField ----
                programSelectField.setHorizontalAlignment(SwingConstants.LEFT);
                contentPanel.add(programSelectField, "cell 0 3");

                //---- programSelectBrowseButton ----
                programSelectBrowseButton.setText("...");
                programSelectBrowseButton.addActionListener(e -> browseProgram(e));
                contentPanel.add(programSelectBrowseButton, "cell 1 3");

                //---- argsLabel ----
                argsLabel.setText("Enter any program arguments.");
                contentPanel.add(argsLabel, "cell 0 4,gapy 5 5");
                contentPanel.add(argsField, "cell 0 5 2 1,growx");
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setLayout(new MigLayout(
                    "insets dialog,alignx center",
                    // columns
                    "[button,fill]",
                    // rows
                    null));

                //---- okButton ----
                okButton.setText("OK");
                okButton.addActionListener(e -> okClicked(e));
                buttonBar.add(okButton, "cell 0 0");
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        postCreate();
    }

    private void preCreate() {

        this.setIconImage(Resources.CANDOR_LOGO.getImage());
    }

    private void postCreate() {

        try {

            File gamePath = new File(GamesList.getCurrentGame().getGameExe());
            if(gamePath.exists()) {

                this.programSelectField.setText(gamePath.getCanonicalPath());
            }
        } catch(IOException e) {

            e.printStackTrace();
        }
        this.programSelectLabel.setFont(programSelectLabel.getFont().deriveFont(16f));
        this.argsLabel.setFont(argsLabel.getFont().deriveFont(16f));
        pack();
        setLocationRelativeTo(getOwner());
    }

    public RunConfigDialog(Window owner) {
        super(owner);
        initComponents();
    }
}
