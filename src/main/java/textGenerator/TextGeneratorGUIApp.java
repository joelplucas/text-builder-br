package textGenerator;

import textGenerator.generator.LoadTrainedModel;
import textGenerator.training.BRCharacterIterator;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Container;
import java.awt.Color;
import java.io.IOException;
import javax.swing.*;

import static textGenerator.training.LSTMBRTextModelingTrainer.EXAMPLE_LENGTH;
import static textGenerator.training.LSTMBRTextModelingTrainer.MIN_BATCH_SIZE;

/**
 *
 * @author joelpl
 */
public class TextGeneratorGUIApp extends JFrame  implements ActionListener {

    private LoadTrainedModel drummondModel, pessoaModel, veredasModel;
    private JTextArea jtAreaOutput;
    
    public static void main(String[] args) throws Exception {
        String modelPath = args[0];
        int miniBatchSize = (args.length > 1) ? Integer.valueOf(args[1]) : MIN_BATCH_SIZE;
        int exampleLength = (args.length > 2) ? Integer.valueOf(args[2]) : EXAMPLE_LENGTH;

        TextGeneratorGUIApp gui = new TextGeneratorGUIApp();
        gui.loadModels(modelPath, miniBatchSize, exampleLength);
        gui.createAndShowGUI();
        gui.setVisible(true);
    }
    
    public TextGeneratorGUIApp() throws IOException {
        setBounds(200, 200, 600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(null);
        setTitle("BR Text Generator");
        setBackground(Color.DARK_GRAY);
    }
    
    public void createAndShowGUI() {
        JComboBox comboBox = getComboBox();
        jtAreaOutput = new JTextArea(20, 50);
        Font font = new Font("Verdana", Font.PLAIN, 14);
        jtAreaOutput.setFont(font);
        jtAreaOutput.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(jtAreaOutput,
                        JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                        JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        GridBagLayout gridBag = new GridBagLayout();
        Container contentPane = getContentPane();
        contentPane.setLayout(gridBag);
        GridBagConstraints gridCons1 = new GridBagConstraints();
        gridCons1.gridwidth = GridBagConstraints.REMAINDER;
        gridCons1.fill = GridBagConstraints.HORIZONTAL;
        contentPane.add(comboBox, gridCons1);
        GridBagConstraints gridCons2 = new GridBagConstraints();
        gridCons2.weightx = 1.0;
        gridCons2.weighty = 1.0;
        contentPane.add(scrollPane, gridCons2);

        pack();
    }
    
    private JComboBox getComboBox() {
        JComboBox<String> comboAuthors = new JComboBox<>();
        Font font = new Font("Verdana", Font.BOLD, 12);
        comboAuthors.setFont(font);
        //comboAuthors.setBackground(Color.LIGHT_GRAY);
        comboAuthors.setSize(200, 100);
        comboAuthors.addItem("Escolher Autor");
        comboAuthors.addItem("Drummond");
        comboAuthors.addItem("Fernando Pessoa");
        comboAuthors.addItem("Veredas");
        comboAuthors.addActionListener(this);
        return comboAuthors;
    }
    
    public void actionPerformed(ActionEvent event) {
        JComboBox<String> combo = (JComboBox<String>) event.getSource();
        String selectedAuthor = (String) combo.getSelectedItem();

        String text = null;
        if(selectedAuthor.equals("Drummond")) {
            text = drummondModel.getSample();

        } else if (selectedAuthor.equals("Fernando Pessoa")) {
            text = pessoaModel.getSample();

        } else if (selectedAuthor.equals("Veredas")) {
            text = veredasModel.getSample();
        }
        jtAreaOutput.setText(text);
    }

    private void loadModels(String authorsPath, int miniBatchSize, int exampleLength) throws Exception {
        BRCharacterIterator drummondIterator = new BRCharacterIterator(authorsPath+"drummond.txt", miniBatchSize, exampleLength);
        this.drummondModel = new LoadTrainedModel(drummondIterator, authorsPath+"drummond.model");

        BRCharacterIterator pessoaIterator = new  BRCharacterIterator(authorsPath+"pessoa.txt", miniBatchSize, exampleLength);
        this.pessoaModel = new LoadTrainedModel(pessoaIterator, authorsPath+"pessoa.model");

        BRCharacterIterator veredasIterator = new  BRCharacterIterator(authorsPath+"veredas.txt", miniBatchSize, exampleLength);
        this.veredasModel = new LoadTrainedModel(veredasIterator, authorsPath+"veredas.mondel");
    }
}