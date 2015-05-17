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
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author cjramos
 */
class SolitaryPanel extends JPanel implements ActionListener{
    
    JButton selectButton,deleteButton,stitchButton,confirmDelete;
    JTable table;
    JScrollPane scroll;
    JFrame delFrame;
    JPanel delPanel;
    JScrollPane panelHolder;
    JFileChooser chooser = new JFileChooser();;
    File[] f;
    File directory;
    DefaultTableModel dtm;
    int i,rows=11,columns=2;
    //ArrayList<String> path,filename;
    String tmp,tmp2;
    String[] header;
    int trigger1;
    ArrayList<Checkbox> checkboxes;
    ArrayList<String> path = new ArrayList<String>();
    ArrayList<String> filename = new ArrayList<String>();
    GroupLayout layout;
    
    public SolitaryPanel(){
        //this.setLayout(null);
        layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        selectButton = new JButton("Select Images");
        //selectButton.setBounds(10,275,150,40);
       // selectButton.setBackground(new Color(255,100,17));
        selectButton.addActionListener(this);
        selectButton.setForeground(Color.blue);
        //this.add(selectButton);
        
        deleteButton = new JButton("Remove Image");
        //deleteButton.setBounds(170,275,150,40);
        deleteButton.addActionListener(this);
        deleteButton.setEnabled(false);
        deleteButton.setForeground(Color.blue);
        //this.add(deleteButton);
        
        stitchButton = new JButton("Stitch Images");
       // stitchButton.setBounds(330,275,150,40);
        stitchButton.addActionListener(this);
        stitchButton.setEnabled(false);
        stitchButton.setForeground(Color.blue);
       // this.add(stitchButton);
        
        header = new String[]{"Filename","Path"};
        table = new JTable();
        dtm = new DefaultTableModel(0,0);
        dtm.setColumnIdentifiers(header);
        table.setModel(dtm);
        
        scroll = new JScrollPane(table);
        //scroll.setPreferredSize(475,250);
        //scroll.setBounds(10,5,475,250);
        //this.add(scroll);
        
        /*GROUP LAYOUT*/
        layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(scroll)
             .addGroup(layout.createSequentialGroup()
                .addComponent(selectButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(deleteButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(stitchButton, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
   
        layout.setVerticalGroup(layout.createSequentialGroup()
            .addComponent(scroll)
             .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                .addComponent(selectButton)
                .addComponent(deleteButton)
                .addComponent(stitchButton)));
    }
    
  
    @Override
    public void actionPerformed(ActionEvent e){
        int counter=0;
        String newPath;
        
        if(e.getSource() == selectButton){
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
                
                /* Printing the arraylist
                for(i=0;i<f.length;i++){
                    tmp = path.get(i);
                    tmp2 = filename.get(i);
                    
                    System.out.println(tmp2 + "   "+tmp);
                }*/
                deleteButton.setEnabled(true);
                stitchButton.setEnabled(true);
                selectButton.setText("Add Image");
        }
        
        else if(e.getSource() == deleteButton){     //choose and delete any of the selected images 
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
            
            confirmDelete = new JButton("Delete");
            confirmDelete.setBounds(50,320,100,40);
            confirmDelete.addActionListener(this);
            delFrame.add(confirmDelete);
            
        }
        
        else if(e.getSource() == stitchButton){
            directory = new File("/home/cjhay/Desktop/Image Stitcher"); //folder for holding the stitched images
           
            try{
                directory.mkdir();      //creating the needed directory
            }
           catch(Exception ex){
               System.out.println(ex);
           }
           
            for(i=0;i<(path.size()-1);i=i+2){       //loop for stitching the images
                newPath = Stitcher.initializeImages(path.get(i), path.get(i+1),counter);//sends the first 2 images
                counter++;
                System.out.println("New path:"+newPath);
                
                if(newPath.equalsIgnoreCase("Failed")){     //when the input images are not stitched
                    System.out.println("Feature Match Is Too Low");
                }
                
                else if(path.size()<=2){             //when the remaining images are already stitched   
                    System.out.println("Finished Stitching");
                    break;
                }
                
                else{
                    System.out.println("Size:"+path.size());
                    System.out.println("I:"+i);
                    path.remove(i);                                     //removes the input images after successfully stitched
                    path.remove(i+1);
                    System.out.println("Size remove:"+path.size());
                    path.add(newPath);          //adds the path of the created image to arraylist
                    i=0;
                    System.out.println("New I:"+i);
                }    
            }
            System.out.println("Exit Loop");
        }
        
        else if(e.getSource() == confirmDelete){        //confirms deletion of selected images
            for(i=0;i<filename.size();i++){
                if(checkboxes.get(i).getState()){
                    dtm.removeRow(i);
                    checkboxes.remove(i);
                    filename.remove(i);
                    path.remove(i);
                    i--;
                }
            }
            delFrame.dispose();
        }
    }
}
