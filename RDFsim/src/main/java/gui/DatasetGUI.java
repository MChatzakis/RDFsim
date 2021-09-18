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
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import raf.RafApi;
import sparql.SPARQLQuery;

/**
 *
 * @author manos
 */
public class DatasetGUI extends JFrame {

    public static final int WIDTH = 1000;
    public static final int HEIGHT = 1000;

    private static Container generalPanel;
    private static Container parametersPanel;

    private static JLabel endpointLabel = new JLabel("Endpoint:");
    private static JTextField endpointText = new JTextField("https://dbpedia.org/sparql");

    private static JLabel queryLabel = new JLabel("Query:");
    private static JTextField queryText = new JTextField("select * where {?s ?p ?o . ?s a <http://dbpedia.org/class/yago/WikicatAncientGreekPhilosophers>. filter(isURI(?o))}");
    private static JLabel queryLimitLabel = new JLabel("Limit:");
    private static JTextField queryLimitText = new JTextField("40");
    private static JLabel queryOffsetLabel = new JLabel("Offset:");
    private static JTextField queryOffsetText = new JTextField("0");

    private static JLabel layersLabel = new JLabel("Layers");
    private static JTextField layersText = new JTextField("200");
    private static JLabel iterationsLabel = new JLabel("Iterations");
    private static JTextField iterationsText = new JTextField("10");
    private static JLabel windowSizeLabel = new JLabel("WindowSize");
    private static JTextField windowSizeText = new JTextField("3");
    private static JLabel seedLabel = new JLabel("Seed");
    private static JTextField seedText = new JTextField("42");
    private static JLabel minWordFreqLabel = new JLabel("MinWordFreq");
    private static JTextField minWordFreqText = new JTextField("10");

    private static JLabel stopWordsLabel = new JLabel("Stop Words");
    private static JTextField stopWordsText = new JTextField(".");

    private static JLabel countLabel = new JLabel("Count");
    private static JTextField countText = new JTextField("20");
    private static JLabel keepWordsStartingWithLabel = new JLabel("Keep Words Starting With");
    private static JTextField keepWordsStartingWithText = new JTextField("http://dbpedia.org/resource/");
    private static JLabel keepWordsNotStartingWithLabel = new JLabel("Keep Words Not Starting With");
    private static JTextField keepWordsNotStartingWithText = new JTextField("http://dbpedia.org/resource/Template,http://dbpedia.org/resource/Category,http://dbpedia.org/resource/?,http://dbpedia.org/resource/*,http://dbpedia.org/resource/-,http://dbpedia.org/resource/:,http://dbpedia.org/resource/%,http://dbpedia.org/resource/.");
    private static JLabel removeWordsContainingLabel = new JLabel("Remove Words Containing");
    private static JTextField removeWordsContainingText = new JTextField("?");

    private static JLabel vocabFilePathLabel = new JLabel("Vocabulary file savepath");
    private static JTextField vocabFilePathText = new JTextField("C:\\tmp\\vocab.rdf");
    private static JLabel vectorsFilePathLabel = new JLabel("Vectors file savepath");
    private static JTextField vectorsFilePathText = new JTextField("C:\\tmp\\vectors.vec");
    private static JLabel rafFilePathLabel = new JLabel("Raf file savepath");
    private static JTextField rafFilePathText = new JTextField("C:\\tmp\\raf.txt");

    private static JButton submitButton = new JButton("Submit");

    private static JTextArea console = new JTextArea("Information About the Dataset Creation will be shown here.");
    JScrollPane scroll = new JScrollPane(console, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    public DatasetGUI() {
        setTitle("RDFsim Dataset Creator");
        //setBounds(300, 90, WIDTH, HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        generalPanel = getContentPane();
        generalPanel.setLayout(new GridLayout(0, 1));

        parametersPanel = new Container();
        parametersPanel.setLayout(new GridLayout(0, 1));

        /* Adding endpoint form */
        //parametersPanel.add(new JLabel("Endpoint Settings"));
        Container endpointCont = new Container();
        endpointCont.setLayout(new GridLayout(0, 2));
        endpointCont.add(endpointLabel);
        endpointCont.add(endpointText);
        parametersPanel.add(endpointCont);

        /* Adding query form */
        //parametersPanel.add(new JLabel("Query Settings"));
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
        //parametersPanel.add(new JLabel("Embedding Settings"));
        Container w2vCont = new Container();
        w2vCont.setLayout(new GridLayout(0, 6));
        w2vCont.add(layersLabel);
        w2vCont.add(layersText);
        w2vCont.add(iterationsLabel);
        w2vCont.add(iterationsText);
        w2vCont.add(minWordFreqLabel);
        w2vCont.add(minWordFreqText);
        w2vCont.add(windowSizeLabel);
        w2vCont.add(windowSizeText);
        w2vCont.add(seedLabel);
        w2vCont.add(seedText);
        w2vCont.add(countLabel);
        w2vCont.add(countText);
        w2vCont.add(stopWordsLabel);
        w2vCont.add(stopWordsText);
        w2vCont.add(keepWordsStartingWithLabel);
        w2vCont.add(keepWordsStartingWithText);
        w2vCont.add(keepWordsNotStartingWithLabel);
        w2vCont.add(keepWordsNotStartingWithText);
        w2vCont.add(removeWordsContainingLabel);
        w2vCont.add(removeWordsContainingText);
        parametersPanel.add(w2vCont);

        /* Adding files form */
        //parametersPanel.add(new JLabel("File Settings"));
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
        generalPanel.add(scroll);

        addFunctionalityToButton(submitButton);

        setResizable(true);
        pack();
        setVisible(true);
    }

    public static void main(String[] args) {
        new DatasetGUI();
    }

    public static void addFunctionalityToButton(JButton button) {

        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                console.setText("");
                boolean formatURI = false;

                String endpoint = endpointText.getText();
                String query = queryText.getText();
                console.append("Endpoint:   " + endpoint + "\n");
                console.append("Query:  " + query + "\n");

                int limit = Integer.parseInt(queryLimitText.getText());
                int offset = Integer.parseInt(queryOffsetText.getText());
                console.append("Limit:  " + limit + "\n");
                console.append("Offset: " + offset + "\n");

                String vocabFilePath = vocabFilePathText.getText();
                String vectorFilePath = vectorsFilePathText.getText();
                String rafFilePath = rafFilePathText.getText();
                String rafFilePathPTR = rafFilePath.replace(".txt", "PTR.txt");
                console.append("vocabFilePath:  " + vocabFilePath + "\n");
                console.append("vectorFilePath: " + vectorFilePath + "\n");
                console.append("rafFilePath:    " + rafFilePath + "\n");
                console.append("rafFilePathPTR: " + rafFilePathPTR + "\n");

                int iterations = Integer.parseInt(iterationsText.getText());
                int layerSize = Integer.parseInt(layersText.getText());
                int windowSize = Integer.parseInt(windowSizeText.getText());
                int minWordFreq = Integer.parseInt(minWordFreqText.getText());
                int seed = Integer.parseInt(seedText.getText());
                int count = Integer.parseInt(countText.getText());
                console.append("iterations: " + iterations + "\n");
                console.append("layerSize:  " + layerSize + "\n");
                console.append("windowSize: " + windowSize + "\n");
                console.append("minWordFreq:    " + minWordFreq + "\n");
                console.append("seed:   " + seed + "\n");
                console.append("count:  " + count + "\n");

                Collection<String> stopWords = parseInput(stopWordsText.getText());
                Collection<String> keepWordsStartingWith = parseInput(keepWordsStartingWithText.getText());
                Collection<String> keepWordsNotStartingWith = parseInput(keepWordsNotStartingWithText.getText());
                Collection<String> removeWordsContaining = parseInput(removeWordsContainingText.getText());
                console.append("stopWords:  " + stopWords + "\n");
                console.append("keepWordsStartingWith:  " + keepWordsStartingWith + "\n");
                console.append("keepWordsNotStartingWith:   " + keepWordsNotStartingWith + "\n");
                console.append("removeWordsContaining:  " + removeWordsContaining + "\n");

                try {
                    createDataset(endpoint, query, limit, offset, vocabFilePath, vectorFilePath, rafFilePath, rafFilePathPTR, iterations, layerSize, windowSize, minWordFreq, seed, stopWords, formatURI, keepWordsStartingWith, keepWordsNotStartingWith, removeWordsContaining, count);
                    updateConsole("=========== Dataset Creation Completed Normally ===========");
                } catch (Exception ex) {
                    Logger.getLogger(DatasetGUI.class.getName()).log(Level.SEVERE, null, ex);
                    updateConsole("ERROR: SOMETHING WENT WRONG, TRY AGAIN...");
                }
            }
        });

    }

    public static void createDataset(String endpoint, String query, int limit, int offset,
            String vocabFilePath, String vectorFilePath, String rafFilePath, String rafFilePathPTR,
            int iterations, int layerSize, int windowSize, int minWordFreq, int seed, Collection<String> stopWords, boolean formatURI,
            Collection< String> keepWordsStartingWith, Collection<String> keepWordsNotStartingWith, Collection<String> removeWordsContaining,
            int count) throws IOException {

        updateConsole("=========== Dataset Creation Starting ===========");

        SPARQLQuery sq = new SPARQLQuery();
        sq.writeDataToFile(endpoint, query, limit, offset, vocabFilePath, formatURI, false);
        updateConsole(" === Vocabulary File Created: " + vocabFilePath);

        updateConsole("=========== Model Training Starting ===========");
        W2VApi vec = new W2VApi(minWordFreq, layerSize, seed, windowSize, iterations, new ArrayList<>(stopWords), vocabFilePath);
        vec.train();
        updateConsole(" === Model Trained");

        updateConsole("=========== Dataset Filtering Starting ===========");
        vec.filterVocab(keepWordsStartingWith, keepWordsNotStartingWith, removeWordsContaining);
        vec.saveVectorSpace(vectorFilePath);
        updateConsole(" === Vector File Created: " + vectorFilePath);

        updateConsole("=========== Random Access File Creation Starting ===========");
        vec.createRAF(rafFilePath, rafFilePathPTR, count);
        updateConsole(" === RAF File Created: " + rafFilePath);

        updateConsole("=========== Creating a file with RAF contents as txt in the given directory ===========");
        RafApi raf = new RafApi(rafFilePath);
        String rafContPath = rafFilePath.replace(".txt", "CONTENTS.txt");
        raf.vocabInfoToFile(rafContPath);
        updateConsole(" === RAF Contents File Created: " + rafContPath);
    }

    public static Collection<String> parseInput(String input) {

        if (input == null || input.equals("")) {
            return null;
        }

        String[] inputTokens = input.split(",");
        return Arrays.asList(inputTokens);
    }

    public static void updateConsole(String out) {
        console.append(out + "\n");
    }

    public static void clearConsole() {
        console.setText("");
    }
}
