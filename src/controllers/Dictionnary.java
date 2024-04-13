package controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dictionnary {
    private List<String> words;

    public Dictionnary(String filePath) {
        words = new ArrayList<>();
        loadWordsFromFile(filePath);
    }

    private void loadWordsFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                words.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRandomWord(int wordLength) {
        if (words.isEmpty()) {
            throw new IllegalStateException("The dictionary is empty. Please load words before getting a random word.");
        }

        List<String> filteredWords = new ArrayList<>();

        for (String word : words) {
            if (word.length() == wordLength) {
                filteredWords.add(word);
            }
        }

        if (filteredWords.isEmpty()) {
            System.out.println("No words of the specified length found in the dictionary.");
            return null; // Retourner null si aucun mot de la longueur spécifiée n'est trouvé
        }

        Random random = new Random();
        int index = random.nextInt(filteredWords.size());
        return filteredWords.get(index);
    }
}