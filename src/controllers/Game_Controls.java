package controllers;
import java.util.Set;
import java.util.HashSet;
import java.util.Random;

import javax.swing.table.AbstractTableModel;

public class Game_Controls {
    private String secretWord;
    private String currentAttempt;
    private int maxAttempts;
    private char[][] matrix;
    private int failedAttempts;
   
    

    public Game_Controls(int wordLength) {
        Dictionnary dictionnary = new Dictionnary("../fichier_dictionaire_java.txt");
        secretWord = dictionnary.getRandomWord(wordLength);
        currentAttempt = initializeCurrentAttempt();
        maxAttempts = 6;
        failedAttempts = 0;
        initializeMatrix();
    }
   
    public void incrementFailedAttempts() {
        failedAttempts++;
    }

    public int getFailedAttempts() {
        return failedAttempts;
    }
    
    
    
    public int getCurrentAttemptIndex() {
        // Comptez le nombre d'essais déjà effectués
        int count = 0;
        for (char[] attempt : matrix) {
            if (!String.valueOf(attempt).trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    
    private String initializeCurrentAttempt() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < secretWord.length(); i++) {
            sb.append(" ");
        }
        return sb.toString();
    }
    public char[][] getMatrix() {
        return matrix;
    }
    private void initializeMatrix() {
        matrix = new char[maxAttempts][secretWord.length()];

        for (int i = 0; i < maxAttempts; i++) {
            for (int j = 0; j < secretWord.length(); j++) {
                matrix[i][j] = ' ';
            }
        }
    }
    
    
    public Set<Integer> getRandomIndicesForHints(int hintCount) {
        Random random = new Random();
        Set<Integer> indices = new HashSet<>();
        while (indices.size() < hintCount) {
            indices.add(random.nextInt(secretWord.length()));
        }
        return indices;
    }

    public boolean makeAttempt(String attempt) {
        if (attempt.length() != secretWord.length()) {
            return false;
        }

        boolean isCorrect = true;

        for (int i = 0; i < secretWord.length(); i++) {
            if (secretWord.charAt(i) != attempt.charAt(i)) {
                isCorrect = false; // Les caractères ne correspondent pas
                break;
            }
        }

        // Mettre à jour la matrice avec la tentative actuelle
        matrix[maxAttempts - getRemainingAttempts()] = attempt.toCharArray();

        // Mettre à jour la tentative actuelle seulement si le mot est correct
        if (isCorrect) {
            currentAttempt = attempt;
        }

        return isCorrect;
    }


    private void updateMatrix(String attempt) {
        for (int i = 0; i < attempt.length(); i++) {
            matrix[maxAttempts - getRemainingAttempts()][i] = attempt.charAt(i);
        }
    }

    public boolean isGameOver() {
        return getRemainingAttempts() <= 0 || isWordGuessed();
    }

    private boolean isWordGuessed() {
        return getCurrentAttempt().equals(getSecretWord());
    }


    public String getCurrentAttempt() {
        return currentAttempt;
    }
    public int getMaxAttempts() {
        return maxAttempts;
    }
    public int getRemainingAttempts() {
        return maxAttempts - countFailedAttempts();
    }

    private int countFailedAttempts() {
        int count = 0;
        for (int i = 0; i < maxAttempts; i++) {
            if (!String.valueOf(matrix[i]).trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    public String getSecretWord() {
        return secretWord;
    }

    public MatrixTableModel getTableModel() {
        return new MatrixTableModel();
    }

    public class MatrixTableModel extends AbstractTableModel {
        @Override
        public int getRowCount() {
            return maxAttempts;
        }
       

        @Override
        public int getColumnCount() {
            return secretWord.length();
        }

        @Override
        public Object getValueAt(int row, int col) {
            return matrix[row][col];
        }
    }
    public static void main(String[] args) {
        // Create a new Game_Controls object.
        Game_Controls game = new Game_Controls(10);

        // Display the secret word.
        System.out.println("The secret word is: " + game.getSecretWord());
    }

}