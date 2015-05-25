package UI;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UI extends JPanel implements ActionListener {   
  private JFrame wframe = null;  //displays welcome
  private JFrame rframe = null;  //displays file read
  private JFrame oframe = null;  //displays output
  
  public static void main(String s[]) {
    UI sys = new UI();
  }
  
  public UI() {
//    welcomeFrame();
    showOutput("DIS B OUTPUT!");
  }
  
  public UI(String output) {
    //welcomeFrame();
    showOutput(output);
  }
  
  private void welcomeFrame() {
    wframe = new JFrame("Brother of AssemBOA");
    JPanel jp = new JPanel();
    JLabel title = new JLabel("THE B.O.A. PROJECT", SwingConstants.CENTER);
    title.setPreferredSize(new Dimension(290,50));
    JButton open = new JButton("Open New File");
    open.setBackground(Color.WHITE);
    open.setForeground(Color.BLACK);
    open.addActionListener(this);
    JButton exit = new JButton("Exit Program");
    exit.setBackground(Color.BLACK);
    exit.setForeground(Color.WHITE);
    exit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
     });

    jp.add(new JLabel(new ImageIcon("group.png")));
    jp.add(title, BorderLayout.PAGE_START);
    jp.add(open);
    jp.add(exit);
    wframe.addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    } );
    wframe.getContentPane().add(jp,"Center");
    wframe.setSize(300, 245);
    wframe.setResizable(false);
    wframe.setVisible(true);
    wframe.setLocationRelativeTo(null);
  }
  
  @Override
  public void actionPerformed(ActionEvent e) {
    JFileChooser chooser = new JFileChooser(); 
    chooser.setCurrentDirectory(new java.io.File("."));
    chooser.setDialogTitle("File Selection");
    if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
      wframe.dispose();
      if(rframe!=null) 
        rframe.dispose();
      showFile(chooser.getSelectedFile());
    }
  }
   
  @Override
  public Dimension getPreferredSize(){
    return new Dimension(500, 300);
  }

  private void showFile(final File file) {
    rframe = new JFrame("BOA Compiler");
    JPanel jp = new JPanel();
    final JTextArea ta = new JTextArea();
    ta.setLineWrap (true);
    ta.setWrapStyleWord(true);
    
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(file));
      String text = reader.readLine();
      while(null != text) {
        ta.append(text+"\n");
        text=reader.readLine();
      }
    } catch (FileNotFoundException e) { System.out.print(e); 
    } catch (IOException e) { System.out.print(e);
    } finally {
      try { if(reader != null) { reader.close(); }
      } catch (IOException e) { System.out.print(e); }
    }
    
    JButton open = new JButton("Open Another File");
    open.setBackground(Color.BLACK);
    open.setForeground(Color.WHITE);
    open.addActionListener(this);
    open.setSize(250,30);
    JButton send = new JButton("Save & Run Code");
    send.setSize(250,30);
    send.setBackground(Color.WHITE);
    send.setForeground(Color.BLACK);
    send.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        FileWriter fw;
        try {
          fw = new FileWriter(file.getAbsoluteFile(), false);
          ta.write(fw);
        } catch (IOException ex) {
          Logger.getLogger(UI.class.getName()).log(Level.SEVERE, null, ex);
        }
        runCode(file);
      }
    });
    JScrollPane scroll = new JScrollPane(ta, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
           , ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setPreferredSize(new Dimension(420,220));
    
    jp.add(scroll);
    jp.add(open);
    jp.add(send);
    jp.setVisible(true);
    rframe.addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    } );
    rframe.getContentPane().add(jp,"Center");
    rframe.setSize(500, 300);
    rframe.setResizable(false);
    rframe.setVisible(true);
    rframe.setLocationRelativeTo(null);
  }
  
  private void runCode(File file) {
    System.out.println("call other people's codes here");
    String in = JOptionPane.showInputDialog("Enter an integer:");
    System.out.println(in);
  }
  
  private void runCode(File file, String type) {
    System.out.println("call other people's codes here");
    String in = JOptionPane.showInputDialog("Enter a/an " + type + ": ");
    System.out.println(in);
  }
  
  private void showOutput(String s) {
    oframe = new JFrame("Code Output");
    JPanel jp = new JPanel();
    JTextArea ta = new JTextArea();
    ta.setLineWrap (true);
    ta.setWrapStyleWord(true);
    ta.setEditable(false);
    ta.setText(s);
    ta.append("\nCode Run Complete.");
    
    JScrollPane scroll = new JScrollPane(ta, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
           , ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    scroll.setPreferredSize(new Dimension(280,120));
    
    JButton ok = new JButton("OK");
    ok.setBackground(Color.BLACK);
    ok.setForeground(Color.WHITE);
    ok.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        oframe.dispose();
      }
    });
    ok.setSize(250,30);
    
    jp.add(scroll);
    jp.add(ok);
    
    oframe.addWindowListener( new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        System.exit(0);
      }
    } );
    oframe.getContentPane().add(jp,"Center");
    oframe.setSize(300, 200);
    oframe.setResizable(false);
    oframe.setVisible(true);
    oframe.setLocationRelativeTo(null);
  }
}