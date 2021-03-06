/*
 * Created by JFormDesigner on Wed Aug 26 21:08:39 BST 2020
 */

package uk.co.innoxium.candor.window;

import com.formdev.flatlaf.FlatIconColors;
import uk.co.innoxium.candor.util.Resources;
import uk.co.innoxium.candor.util.WindowUtils;
import uk.co.innoxium.candor.window.component.JImage;
import uk.co.innoxium.swing.component.JHyperlink;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * The about dialogs shows the various links and logos connected with Candor.
 */
public class AboutDialog extends JDialog {

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel linksPanel;
    private JHyperlink discordLink;
    private JHyperlink githubLink;
    private JHyperlink websiteLink;
    private JHyperlink twitterLink;
    private JPanel imagePanel;
    private JLabel candorLogo;
    private JLabel innoxiumLogo;
    private JPanel buttonBar;
    private JButton okButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public AboutDialog() {

        super(WindowUtils.mainFrame);
        initComponents();
    }

    /* Create our components away from JFormDesigner code. */
    private void createUIComponents() {

        Font linkFont = Resources.orbitron.deriveFont(24f);

        candorLogo = new JImage(Resources.CANDOR_LOGO);
        innoxiumLogo = new JImage(Resources.INNOXIUM_LOGO);

        discordLink = new JHyperlink("Discord", "discord.gg/CMG9ZtS");
        discordLink.setFont(linkFont);
        discordLink.setColour(Color.decode(String.valueOf(FlatIconColors.ACTIONS_GREY_DARK.rgb)).darker());
        discordLink.setHighlightedColour(Color.decode(String.valueOf(FlatIconColors.ACTIONS_GREY_DARK.rgb)).darker()); // requires update to SwingExt to function correctly, will be in 0.3.x
        discordLink.setClickedColour(Color.decode(String.valueOf(FlatIconColors.ACTIONS_GREY_DARK.rgb)));
        githubLink = new JHyperlink("Github", "github.com/InnoxiumTech/CandorManager");
        githubLink.setFont(linkFont);
        discordLink.setColour(Color.decode(String.valueOf(FlatIconColors.ACTIONS_GREY_DARK.rgb)).darker());
        discordLink.setHighlightedColour(Color.decode(String.valueOf(FlatIconColors.ACTIONS_GREY_DARK.rgb)).darker());
        discordLink.setClickedColour(Color.decode(String.valueOf(FlatIconColors.ACTIONS_GREY_DARK.rgb)));
        websiteLink = new JHyperlink("Website", "innoxium.co.uk");
        websiteLink.setFont(linkFont);
        discordLink.setColour(Color.decode(String.valueOf(FlatIconColors.ACTIONS_GREY_DARK.rgb)).darker());
        discordLink.setHighlightedColour(Color.decode(String.valueOf(FlatIconColors.ACTIONS_GREY_DARK.rgb)).darker());
        discordLink.setClickedColour(Color.decode(String.valueOf(FlatIconColors.ACTIONS_GREY_DARK.rgb)));
        twitterLink = new JHyperlink("Twitter", "twitter.com/InnoxiumTech");
        twitterLink.setFont(linkFont);
        discordLink.setColour(Color.decode(String.valueOf(FlatIconColors.ACTIONS_GREY_DARK.rgb)).darker());
        discordLink.setHighlightedColour(Color.decode(String.valueOf(FlatIconColors.ACTIONS_GREY_DARK.rgb)).darker());
        discordLink.setClickedColour(Color.decode(String.valueOf(FlatIconColors.ACTIONS_GREY_DARK.rgb)));
    }

    private void okClicked(ActionEvent e) {

        // Close this dialog.
        this.dispose();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        createUIComponents();

        dialogPane = new JPanel();
        contentPanel = new JPanel();
        linksPanel = new JPanel();
        imagePanel = new JPanel();
        buttonBar = new JPanel();
        okButton = new JButton();

        //======== this ========
        setTitle("About Candor!");
        setModal(true);
        setResizable(false);
        var contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BorderLayout());

                //======== linksPanel ========
                {
                    linksPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 5));

                    //---- discordLink ----
                    discordLink.setText("Discord");
                    linksPanel.add(discordLink);

                    //---- githubLink ----
                    githubLink.setText("Github");
                    linksPanel.add(githubLink);

                    //---- websiteLink ----
                    websiteLink.setText("Website");
                    linksPanel.add(websiteLink);

                    //---- twitterLink ----
                    twitterLink.setText("Twitter");
                    linksPanel.add(twitterLink);
                }
                contentPanel.add(linksPanel, BorderLayout.SOUTH);

                //======== imagePanel ========
                {
                    imagePanel.setLayout(new FlowLayout());
                    imagePanel.add(candorLogo);
                    imagePanel.add(innoxiumLogo);
                }
                contentPanel.add(imagePanel, BorderLayout.NORTH);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

                //---- okButton ----
                okButton.setText("OK");
                okButton.addActionListener(e -> okClicked(e));
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.PAGE_END);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
}
