/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author manos
 */
public class CompleteDatasetCreatorGUI extends JFrame {

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 900;

    private Container generalPanel;
    private Container parametersPanel;
    private JLabel title;

    private JLabel endpointLabel = new JLabel("Endpoint:");
    private JTextField endpointText = new JTextField("https://dbpedia.org/sparql");

    private JLabel queryLabel = new JLabel("Query:");
    private JTextField queryText = new JTextField("select * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}");
    private JLabel queryLimitLabel = new JLabel("Limit:");
    private JTextField queryLimitText = new JTextField("1000");
    private JLabel queryOffsetLabel = new JLabel("Offset:");
    private JTextField queryOffsetText = new JTextField("0");

    private JLabel layersLabel = new JLabel("Layers");
    private JTextField layersText = new JTextField("200");
    private JLabel iterationsLabel = new JLabel("Iterations");
    private JTextField iterationsText = new JTextField("10");
    private JLabel windowSizeLabel = new JLabel("Layers");
    private JTextField windowSizeText = new JTextField("200");
    private JLabel seedLabel = new JLabel("Layers");
    private JTextField seedText = new JTextField("200");
    private JLabel minWordFreqLabel = new JLabel("Layers");
    private JTextField minWordFreqText = new JTextField("200");
   
    
    private JLabel vocabFilePathLabel = new JLabel("Vocabulary file savepath");
    private JTextField vocabFilePathText = new JTextField("C:\\Users\\vocab.rdf");
    private JLabel vectorsFilePathLabel = new JLabel("Vectors file savepath");
    private JTextField vectorsFilePathText = new JTextField("C:\\Users\\vectors.vec");
    private JLabel rafFilePathLabel = new JLabel("Rad file savepath");
    private JTextField rafFilePathText = new JTextField("C:\\Users\\raf.txt");
    
    private JButton submitButton = new JButton("Submit");    

    public CompleteDatasetCreatorGUI() {
        setTitle("RDFsim Dataset Creator");
        setBounds(300, 90, WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        generalPanel = getContentPane();
        generalPanel.setLayout(new GridLayout(0, 1));

        parametersPanel = new Container();
        parametersPanel.setLayout(new GridLayout(0, 1));

        /* Adding endpoint form */
        parametersPanel.add(new JLabel("Endpoint Settings"));
        Container endpointCont = new Container();
        endpointCont.setLayout(new GridLayout(0, 2));
        endpointCont.add(endpointLabel);
        endpointCont.add(endpointText);
        parametersPanel.add(endpointCont);

        /* Adding query form */
        parametersPanel.add(new JLabel("Query Settings"));
        Container queryCont = new Container();
        queryCont.setLayout(new GridLayout(0, 6));
        queryCont.add(queryLabel);
        queryCont.add(queryText);
        queryCont.add(queryLimitLabel);
        queryCont.add(queryLimitText);
        queryCont.add(queryOffsetLabel);
        queryCont.add(queryOffsetText);
        parametersPanel.add(queryCont);

        /* Adding word2vec form */
        parametersPanel.add(new JLabel("Embedding Settings"));
        Container w2vCont = new Container();
        w2vCont.setLayout(new GridLayout(0, 6));
        w2vCont.add(layersLabel);
        w2vCont.add(layersText);
        w2vCont.add(iterationsLabel);
        w2vCont.add(iterationsText);
        w2vCont.add(minWordFreqLabel);
        w2vCont.add(minWordFreqText);
        
        parametersPanel.add(w2vCont);
        
        /* Adding files form */
        parametersPanel.add(new JLabel("File Settings"));
        Container filepathsCont = new Container();
        filepathsCont.setLayout(new GridLayout(0, 2));
        filepathsCont.add(vocabFilePathLabel);
        filepathsCont.add(vocabFilePathText);
        filepathsCont.add(vectorsFilePathLabel);
        filepathsCont.add(vectorsFilePathText);
        filepathsCont.add(rafFilePathLabel);
        filepathsCont.add(rafFilePathText);
        parametersPanel.add(filepathsCont);
        parametersPanel.add(submitButton);
        
        generalPanel.add(parametersPanel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new CompleteDatasetCreatorGUI();
    }

}
