package graphics;

import controllers.Game_Controls;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;
import java.util.stream.IntStream;

public class MotusFrame extends JFrame {
    private Game_Controls gameControls;
    private JLabel[][] matrixLabels;
    private JTextField lengthField;
    private JTextField guessField;
    private boolean wordSubmitted = false;
    private Timer timer;
    private int timeLeftInSeconds = 180;
    private JLabel timerLabel;// Temps en secondes

    public MotusFrame() {
    	 JPanel panel = new JPanel();
         panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
         panel.add(new JLabel("Choisir le nombre de caractères pour le mot à deviner :"));
         
         // JComboBox pour les options de longueur de mot
         Integer[] wordLengthOptions = IntStream.rangeClosed(7, 15).boxed().toArray(Integer[]::new);
         JComboBox<Integer> wordLengthChoices = new JComboBox<>(wordLengthOptions);
         wordLengthChoices.setSelectedIndex(0); // Sélectionnez par défaut la première option (7)
         panel.add(wordLengthChoices);

         // Afficher le JOptionPane avec le panel contenant le JComboBox
         int result = JOptionPane.showConfirmDialog(
             null,
             panel,
             "Motus - Longueur du mot",
             JOptionPane.OK_CANCEL_OPTION,
             JOptionPane.QUESTION_MESSAGE
         );

         // Vérifier si l'utilisateur a appuyé sur OK
         if (result == JOptionPane.OK_OPTION) {
             int wordLength = (int) wordLengthChoices.getSelectedItem();
             gameControls = new Game_Controls(wordLength); // Initialisez Game_Controls ici
             initializeUI();
             timeLeftInSeconds = 180; // 3 minutes
             startTimer();
         } else {
             System.exit(0); // L'utilisateur a annulé, fermer l'application
         }
     }
    

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Motus");
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel principal pour la grille de lettres et la saisie utilisateur
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // Création de la grille pour les tentatives de l'utilisateur
        JPanel gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(gameControls.getMaxAttempts(), gameControls.getSecretWord().length(), 5, 5));
        matrixLabels = new JLabel[gameControls.getMaxAttempts()][gameControls.getSecretWord().length()];

        for (int i = 0; i < gameControls.getMaxAttempts(); i++) {
            for (int j = 0; j < gameControls.getSecretWord().length(); j++) {
                matrixLabels[i][j] = new JLabel(" ", SwingConstants.CENTER);
                matrixLabels[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                matrixLabels[i][j].setOpaque(true);
                matrixLabels[i][j].setBackground(Color.WHITE);
                gridPanel.add(matrixLabels[i][j]);
            }
        }
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        Set<Integer> hintIndices = gameControls.getRandomIndicesForHints(2);
        for (int index : hintIndices) {
            matrixLabels[0][index].setText(String.valueOf(gameControls.getSecretWord().charAt(index)));
        }

        // Ajouter le panel pour les propositions
        JPanel guessPanel = new JPanel(new FlowLayout());
        guessPanel.setBorder(BorderFactory.createTitledBorder("Votre proposition :"));
        guessField = new JTextField(20);
        guessPanel.add(guessField);
        mainPanel.add(guessPanel, BorderLayout.SOUTH);

        // Ajouter le panel principal au frame
        add(mainPanel, BorderLayout.CENTER);

        // Panel de droite pour le timer et les boutons
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

timerLabel = new JLabel(getFormattedTime(timeLeftInSeconds), SwingConstants.CENTER);
timerLabel.setBorder(BorderFactory.createTitledBorder("Temps restant :"));

// Assurez-vous que le label a suffisamment de place pour afficher le texte complet
timerLabel.setMinimumSize(new Dimension(200, timerLabel.getPreferredSize().height));
timerLabel.setPreferredSize(new Dimension(200, timerLabel.getPreferredSize().height));
rightPanel.add(timerLabel, BorderLayout.NORTH); 

        JButton restartButton = new JButton("Recommencer");
        JButton quitButton = new JButton("Quitter");

        rightPanel.add(timerLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        rightPanel.add(restartButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(quitButton);
        guessField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processGuess(e);
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        // Ajouter le panel de droite au frame
        add(rightPanel, BorderLayout.EAST);

        // Gérer les actions de l'utilisateur
        guessField.addActionListener(this::processGuess);
        restartButton.addActionListener(e -> restartGame());
        quitButton.addActionListener(e -> System.exit(0));

        // Finaliser l'interface utilisateur
        pack(); // Ajuste la fenêtre pour accommoder tous les composants
        setVisible(true);
        System.out.println("The secret word is: " + gameControls.getSecretWord());
    }

    
    
    private void startGame() {
        int wordLength = Integer.parseInt(lengthField.getText());
        gameControls = new Game_Controls(wordLength);

        JPanel matrixPanel = new JPanel(new GridLayout(gameControls.getRemainingAttempts(), wordLength));
        matrixLabels = new JLabel[gameControls.getRemainingAttempts()][wordLength];

        for (int i = 0; i < gameControls.getRemainingAttempts(); i++) {
            for (int j = 0; j < wordLength; j++) {
                matrixLabels[i][j] = new JLabel("", SwingConstants.CENTER);
                matrixLabels[i][j].setOpaque(true);
                matrixLabels[i][j].setBackground(Color.WHITE);
                matrixLabels[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK)); // Ajout de la bordure
                matrixPanel.add(matrixLabels[i][j]);
            }
        }

        add(new JScrollPane(matrixPanel), BorderLayout.CENTER);
        timerLabel = new JLabel(getFormattedTime(timeLeftInSeconds), SwingConstants.CENTER);
        add(timerLabel, BorderLayout.EAST);
        
        guessField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	processGuess(e);
            }
        });

        startTimer(); // Démarrage du timer

        revalidate();
        repaint();
        setVisible(true);

        System.out.println("The secret word is: " + gameControls.getSecretWord());
    }
    
    private void restartGame() {
        // Réinitialiser le temps
        timeLeftInSeconds = 3 * 60; // Réinitialiser à 3 minutes, par exemple

        // Réinitialiser le mot secret et la grille
        gameControls = new Game_Controls(gameControls.getSecretWord().length());
        for (int i = 0; i < matrixLabels.length; i++) {
            for (int j = 0; j < matrixLabels[i].length; j++) {
                matrixLabels[i][j].setText("");
                matrixLabels[i][j].setBackground(Color.WHITE);
            }
        }
        
        

        // Réactiver le champ de saisie si nécessaire
        guessField.setEnabled(true);
        guessField.setText("");

        // Redémarrer le timer
        if (timer != null) {
            timer.stop();
        }
        startTimer();

        // Mise à jour de l'affichage du timer
        updateTimerLabel();

        // Mettre à jour l'affichage de la grille si nécessaire
        updateMatrix();
        Set<Integer> hintIndices = gameControls.getRandomIndicesForHints(2);
        for (int index : hintIndices) {
            matrixLabels[0][index].setText(String.valueOf(gameControls.getSecretWord().charAt(index)));
        }
        System.out.println("The secret word is: " + gameControls.getSecretWord());
    }

   

    private void startTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeLeftInSeconds--;
                if (timeLeftInSeconds <= 0) {
                    handleTimerEnd();
                    timer.stop();
                }
                updateTimerLabel(); // Mettre à jour l'affichage du timer chaque seconde
            }
        });
        timer.start();
    }
    
    private void processGuess(ActionEvent e) {
        String guess = guessField.getText().trim();
        if (guess.length() > gameControls.getSecretWord().length()) {
            JOptionPane.showMessageDialog(this, "La longueur du mot proposé ne correspond pas.", "Erreur", JOptionPane.ERROR_MESSAGE);
            guessField.setText("");
            
            
        }

        boolean isCorrect = gameControls.makeAttempt(guess);
        updateMatrix();

        if (isCorrect) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Félicitations! Vous avez trouvé le mot.", "Bravo!", JOptionPane.INFORMATION_MESSAGE);
        } else if (gameControls.isGameOver()) {
            timer.stop();
            JOptionPane.showMessageDialog(this, "Nombre de tentatives épuisé. Le jeu est terminé.", "Fin du jeu!", JOptionPane.WARNING_MESSAGE);
            System.out.println("Game over! The word was: " + gameControls.getSecretWord());
            guessField.setEnabled(false);
        } else if (timeLeftInSeconds <= 0) {
            handleTimerEnd();
        } else {
            StringBuilder notInWord = new StringBuilder();
            for (char c : guess.toCharArray()) {
                if (!gameControls.getSecretWord().contains(String.valueOf(c))) {
                    notInWord.append(c).append(", ");
                }
            }
            if (notInWord.length() > 0) {
                notInWord.setLength(notInWord.length() - 2); // Enlever la dernière virgule et espace
                JOptionPane.showMessageDialog(this, "Le mot ne contient pas les lettres suivantes: " + notInWord, "Indice", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        guessField.setText(""); // Réinitialiser le champ de texte
    }

    private void handleTimerEnd() {
        JOptionPane.showMessageDialog(this, "Temps écoulé! Le jeu est terminé.", "Fin du temps!", JOptionPane.WARNING_MESSAGE);
        guessField.setEnabled(false);
        // Autres actions à effectuer lorsque le temps est écoulé
        timer.stop(); // Arrêter le timer lorsque le temps est écoulé
    }

   
    private void updateTimerLabel() {
        timerLabel.setText(getFormattedTime(timeLeftInSeconds));
    }

    // Méthode pour obtenir le temps formaté en minutes et secondes
    private String getFormattedTime(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
    private void updateMatrix() {
        char[][] matrix = gameControls.getMatrix();

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                char guessedChar = matrix[i][j];
                char secretChar = gameControls.getSecretWord().charAt(j);

                if (guessedChar == ' ') {
                    matrixLabels[i][j].setBackground(Color.WHITE);
                    matrixLabels[i][j].setText(""); // Laisser la cellule vide
                } else {
                    if (guessedChar == secretChar) {
                        matrixLabels[i][j].setBackground(Color.GREEN);
                    } else if (gameControls.getSecretWord().contains(String.valueOf(guessedChar))) {
                        matrixLabels[i][j].setBackground(Color.ORANGE);
                    } else {
                        matrixLabels[i][j].setBackground(Color.RED);
                    }
                    matrixLabels[i][j].setText(String.valueOf(guessedChar));
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MotusFrame();
            }
        });
    }
}


