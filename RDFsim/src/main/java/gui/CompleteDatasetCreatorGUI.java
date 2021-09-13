/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import embeddings.W2VApi;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import sparql.SPARQLQuery;

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
    private static JTextField endpointText = new JTextField("https://dbpedia.org/sparql");

    private JLabel queryLabel = new JLabel("Query:");
    private static JTextField queryText = new JTextField("select * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}");
    private JLabel queryLimitLabel = new JLabel("Limit:");
    private static JTextField queryLimitText = new JTextField("1000");
    private JLabel queryOffsetLabel = new JLabel("Offset:");
    private static JTextField queryOffsetText = new JTextField("0");

    private JLabel layersLabel = new JLabel("Layers");
    private static JTextField layersText = new JTextField("200");
    private JLabel iterationsLabel = new JLabel("Iterations");
    private static JTextField iterationsText = new JTextField("10");
    private JLabel windowSizeLabel = new JLabel("Layers");
    private static JTextField windowSizeText = new JTextField("200");
    private JLabel seedLabel = new JLabel("Layers");
    private static JTextField seedText = new JTextField("200");
    private JLabel minWordFreqLabel = new JLabel("Layers");
    private static JTextField minWordFreqText = new JTextField("200");

    private JLabel vocabFilePathLabel = new JLabel("Vocabulary file savepath");
    private static JTextField vocabFilePathText = new JTextField("C:\\Users\\vocab.rdf");
    private JLabel vectorsFilePathLabel = new JLabel("Vectors file savepath");
    private static JTextField vectorsFilePathText = new JTextField("C:\\Users\\vectors.vec");
    private JLabel rafFilePathLabel = new JLabel("Rad file savepath");
    private static JTextField rafFilePathText = new JTextField("C:\\Users\\raf.txt");

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

    public static void addFunctionalityToButton(JButton button) {
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String endpoint = endpointText.getText();
                String query = queryText.getText();
                int limit = Integer.parseInt(queryLimitText.getText());
                int offset = Integer.parseInt(queryOffsetText.getText());
                
                String vocabFilePath = vocabFilePathText.getText();
                String vectorFilePath =vocabFilePathText.getText();
                String rafFilePath = vocabFilePathText.getText();
                String rafFilePathPTR = vocabFilePathText.getText();
                int iterations = 0;
                int layerSize = 0;
                int windowSize = 0;
                int minWordFreq = 0;
                int seed = 0;
                Collection<String> stopWords = null;
                boolean formatURI = false;
                Collection< String> keepWordsStartingWith = null;
                Collection<String> keepWordsNotStartingWith = null;
                Collection<String> removeWordsContaining = null;
                int count = 30;

                try {
                    createDataset(endpoint, query, limit, offset, vocabFilePath, vectorFilePath, rafFilePath, rafFilePathPTR, iterations, layerSize, windowSize, minWordFreq, seed, stopWords, formatURI, keepWordsStartingWith, keepWordsNotStartingWith, removeWordsContaining, count);
                } catch (IOException ex) {
                    Logger.getLogger(CompleteDatasetCreatorGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    public static void createDataset(String endpoint, String query, int limit, int offset,
            String vocabFilePath, String vectorFilePath, String rafFilePath, String rafFilePathPTR,
            int iterations, int layerSize, int windowSize, int minWordFreq, int seed, Collection<String> stopWords, boolean formatURI,
            Collection< String> keepWordsStartingWith, Collection<String> keepWordsNotStartingWith, Collection<String> removeWordsContaining,
            int count) throws IOException {

        SPARQLQuery sq = new SPARQLQuery();
        sq.writeDataToFile(endpoint, query, limit, offset, vocabFilePath, formatURI);

        W2VApi vec = new W2VApi(minWordFreq, layerSize, seed, windowSize, iterations, null, vocabFilePath);
        vec.train();
        vec.filterVocab(keepWordsStartingWith, keepWordsNotStartingWith, removeWordsContaining);
        vec.saveVectorSpace(vectorFilePath);
        vec.createRAF(rafFilePath, rafFilePathPTR, count);
    }

}
