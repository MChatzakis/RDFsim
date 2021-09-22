/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import raf.RandAccessFileAPI;

/**
 *
 * @author manos
 */
public class Embeddings2RafGUI extends JFrame {

    private int HEIGHT = 150;
    private int WIDTH = 400;

    private Container generalPanel = new Container();

    private JLabel inputFIleLabel = new JLabel("Input:");
    private JLabel outputFIleLabel = new JLabel("Output:");
    private JLabel endpointLabel = new JLabel("Endpoint:");
    private JLabel graphLabel = new JLabel("Graph:");

    private JTextField inputText = new JTextField("C:\\tmp\\custom_sets\\test1.txt");
    private JTextField outputText = new JTextField("C:\\tmp\\rdfsim\\rafs\\test1demo.txt");

    private JTextField graphText = new JTextField("http://dbpedia.org");
    private JTextField endpointText = new JTextField("https://dbpedia.org/sparql");

    private JButton submit = new JButton("Submit");

    public Embeddings2RafGUI() {
        setTitle("RDFsim Dataset Converter");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBounds(300, 90, WIDTH, HEIGHT);

        generalPanel = getContentPane();
        generalPanel.setLayout(new GridLayout(0, 2));

        generalPanel.add(inputFIleLabel);
        generalPanel.add(inputText);
        generalPanel.add(outputFIleLabel);
        generalPanel.add(outputText);

        generalPanel.add(endpointLabel);
        generalPanel.add(endpointText);
        generalPanel.add(graphLabel);
        generalPanel.add(graphText);

        generalPanel.add(submit);

        addFunc2Button(inputText, outputText, endpointText, graphText);

        setResizable(false);
        setVisible(true);
    }

    public void addFunc2Button(final JTextField in, final JTextField out, final JTextField end, final JTextField g) {
        submit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String inPath = in.getText();
                String outPath = out.getText();
                String endpoint = end.getText();
                String graph = g.getText();
                
                boolean usingGraph = true;

                if (graph == null || graph.isEmpty()) {
                    graph = null;
                    usingGraph = false;
                }

                try {
                    RandAccessFileAPI.createRAFfromCustomDataset(inPath, outPath, endpoint, graph, usingGraph);
                    JOptionPane.showMessageDialog(null, "RAF created in dir: " + outPath, "RAF output", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    Logger.getLogger(Embeddings2RafGUI.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Something Went Wrong, Try Again!", "RAF output", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
    }

    public static void main(String[] args) {
        new Embeddings2RafGUI();
    }

}
