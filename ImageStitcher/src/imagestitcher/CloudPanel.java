/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imagestitcher;

/**
 *
 * @author cjhay
 */
import java.awt.Checkbox;
import java.awt.Color;
import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author cjramos
 */
public class CloudPanel extends JPanel implements ActionListener {
    
    JLabel label1,cloudLabel,uName,pWord,category;
    JButton selectButton2,deleteButton2,stitchButton2,confirmDelete2,upload;
    JTable table;
    JScrollPane scroll;
    JFrame delFrame;
    JPanel delPanel;
    JScrollPane panelHolder;
    JTextField username,password;
    JRadioButton option1, option2;
    JFileChooser chooser = new JFileChooser();
    File[] f;
    DefaultTableModel dtm;
    int i,rows=11,columns=2;
    //ArrayList<String> path,filename;
    String tmp,tmp2;
    String[] header;
    int trigger1;
    ArrayList<Checkbox> checkboxes;
    ArrayList<String> path = new ArrayList<String>();
    ArrayList<String> filename = new ArrayList<String>();
    Font cloudFont;
    GroupLayout layout;
    
    
    public CloudPanel(){

       //this.setLayout(null);
        
        layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        selectButton2 = new JButton("Select Images");
        //selectButton.setBounds(10,275,150,40);
       // selectButton.setBackground(new Color(255,100,17));
        selectButton2.addActionListener(this);
        selectButton2.setForeground(Color.blue);
        //this.add(selectButton);
        
        deleteButton2= new JButton("Remove Image");
        //deleteButton.setBounds(170,275,150,40);
        deleteButton2.addActionListener(this);
        //deleteButton2.setEnabled(false);
        deleteButton2.setForeground(Color.blue);
        //this.add(deleteButton);
        
        stitchButton2 = new JButton("STITCH IMAGES");
       // stitchButton.setBounds(330,275,150,40);
        stitchButton2.addActionListener(this);
        //stitchButton2.setEnabled(false);
        stitchButton2.setForeground(Color.blue);
        
     /*   upload = new JButton("Upload");
        upload.addActionListener(this);
        upload.setEnabled(false);
        upload.setForeground(Color.blue);*/
        
        header = new String[]{"Filename","Path"};
        table = new JTable();
        dtm = new DefaultTableModel(0,0);
        dtm.setColumnIdentifiers(header);
        table.setModel(dtm);
        
        scroll = new JScrollPane(table);
        //scroll.setPreferredSize(475,250);
        //scroll.setBounds(10,5,475,250);
        //this.add(scroll);
        
         cloudLabel = new JLabel("ACCESS CLOUD");
        //cloudLabel.setBounds(20,10,100,30);
        cloudFont = new Font(cloudLabel.getFont().getName(),Font.BOLD,cloudLabel.getFont().getSize());
        cloudLabel.setFont(cloudFont);
        //cloudLabel.setBackground(new Color(0,255,0));
        //this.add(cloudLabel);
        uName = new JLabel("Username: ");
        pWord = new JLabel("Password: ");
        username = new JTextField();
        password = new JTextField();
        
        option1 = new JRadioButton("Main Computer");
        option2 = new JRadioButton("SubUnit");
        ButtonGroup group = new ButtonGroup();
        group.add(option1);
        group.add(option2);
        
        category = new JLabel("                                       ");
        
        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                 .addComponent(cloudLabel)
                 .addGroup(layout.createSequentialGroup()
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addComponent(uName)
                        .addComponent(username)
                        .addComponent(pWord)
                        .addComponent(password)
                        //.addComponent(upload )
                        .addComponent(category)
                        .addComponent(category)
                        .addComponent(option1)
                        .addComponent(option2)))
                  .addComponent(stitchButton2))
            .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                 .addComponent(scroll) 
                 .addGroup(layout.createSequentialGroup()
                        .addComponent(selectButton2,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(deleteButton2,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)) 
                )  
        );
    
        layout.linkSize(SwingConstants.VERTICAL, password, pWord);
        layout.linkSize(SwingConstants.VERTICAL, username, uName);
        
        layout.setVerticalGroup(layout.createSequentialGroup()
              .addComponent(cloudLabel)
              .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                     .addGroup(layout.createSequentialGroup()
                                 .addComponent(uName)
                                 .addComponent(username)
                                 .addComponent(pWord)
                                 .addComponent(password)  
                                 //.addComponent(upload)
                                 .addComponent(category)
                                 .addComponent(category)
                                 .addComponent(option1)
                                 .addComponent(option2))
                     .addComponent(scroll))
               .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                    
                     .addComponent(stitchButton2)
                     .addComponent(selectButton2,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                     .addComponent(deleteButton2,GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        
       /* if(option1.isSelected()){
            stitchButton2.setEnabled(false);
            deleteButton2.setEnabled(false);
        }
        else{
            selectButton2.setEnabled(false);
        }*/
    }
    

    @Override
    public void actionPerformed(ActionEvent e){
        if(e.getSource() == selectButton2){
                //path = new ArrayList<>();
                //filename = new ArrayList<>();
                
                chooser.addChoosableFileFilter(new FileNameExtensionFilter("Images","jpg","jpeg","png","gif","bmp"));
                chooser.setMultiSelectionEnabled(true); //enables the multiple file selection
                chooser.showOpenDialog(null);
                f = chooser.getSelectedFiles();
                for(i=0;i<f.length;i++){                //adds the filename and path to arraylists
                    path.add(f[i].getAbsolutePath());
                    filename.add(f[i].getName());
                    dtm.addRow(new Object[]{filename.get(i),path.get(i)});
                }
                
                deleteButton2.setEnabled(true);
                stitchButton2.setEnabled(true);
                selectButton2.setText("Add Image");
        }
          else if(e.getSource() == deleteButton2){
            checkboxes = new ArrayList<Checkbox>();
            
            delFrame = new JFrame("Images");
            delFrame.setSize(300,400);
            delFrame.setLocationRelativeTo(null);
            delFrame.setResizable(false);
            delFrame.setLayout(null);
            delFrame.setVisible(true);
            
            delPanel = new JPanel();
            delPanel.setLayout(new BoxLayout(delPanel,BoxLayout.Y_AXIS));
            
            for(i=0;i<filename.size();i++){
                Checkbox checkbox = new Checkbox(filename.get(i));
                checkboxes.add(checkbox);
                delPanel.add(checkboxes.get(i));
            }
            
            panelHolder = new JScrollPane(delPanel);
            panelHolder.setBounds(0,0,300,300);
            
            delFrame.add(panelHolder);
            
            confirmDelete2 = new JButton("Delete");
            confirmDelete2.setBounds(50,320,100,40);
            confirmDelete2.addActionListener(this);
            delFrame.add(confirmDelete2);
            
        }
        else if(e.getSource()==stitchButton2){
            if(option1.isSelected()){
                //upload 6/10 selected pictures to cloud
                //stitch 4 remaining pictures
                //upload to cloud stitching result
                //upload images with low feature match
                //Reminder: do not upload input images that have been successfully stitched
                //loop: download 2 pics, stitch, 
                    //if successful upload result, delete input images  
                    //if low match, discard second pic then download new pic 
            }
            else{
                //create folder for pictures
                //download 3 pictures from cloud
                //try stitching
                //upload to cloud stitching result
                //upload images with low feature match
                //loop: download 2 pics, stitch, 
                    //if successful upload result, delete input images  
                    //if low match, discard second pic then download new pic 
            }
        }

  }
}   
