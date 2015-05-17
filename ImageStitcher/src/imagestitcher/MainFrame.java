/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imagestitcher;

/**
 *
 * @author cjhay
 */
import javax.swing.*;
import javax.swing.UIManager.*;

public class MainFrame extends JFrame{
    CloudPanel cPanel;
    SolitaryPanel sPanel;
    InfoPanel iPanel;
    JTabbedPane tabHolder;
    ImageIcon icon1, icon2, icon3;
    
    public MainFrame(){
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		if ("Nimbus".equals(info.getName())) {
        		UIManager.setLookAndFeel(info.getClassName());
                        break;
		}
	    }
        }catch(Exception e){
            System.out.println("Nimbus theme not available");
        }
    
        
        
        this.setTitle("Image Stitcher");
        this.setSize(550,500);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        
        tabHolder = new JTabbedPane();
        
        cPanel = new CloudPanel();
        sPanel = new SolitaryPanel();
        iPanel = new InfoPanel();
        
        icon1 = new ImageIcon(getClass().getResource("/images/cloud_icon_50.png"));
        icon2 = new ImageIcon(getClass().getResource("/images/solitary_icon_50.png"));
        icon3 = new ImageIcon(getClass().getResource("/images/question_icon_50.png"));
        
        tabHolder.addTab("Cloud Stitching   ",icon1,cPanel);
        tabHolder.addTab("Solitary Stitching   ",icon2,sPanel);
        tabHolder.addTab("Information   ",icon3,iPanel);
        
        this.add(tabHolder);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }
}

