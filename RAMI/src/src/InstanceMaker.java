package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class InstanceMaker {
    public static void main(String[] args) {
        // Ceci est la chaîne d'entrée, tu peux la modifier pour tester avec différents atomes.
        String input = "O:1,H:2,C:1";
        createInstance(input);
    }

    private static void createInstance(String input) {
        // Sépare la chaîne d'entrée en atomes
        String[] atoms = input.split(",");

        // Initialise les StringBuilder pour les types et les quantités
        StringBuilder typesBuilder = new StringBuilder();
        StringBuilder quantitiesBuilder = new StringBuilder();

        // Analyse chaque atome pour extraire le type et la quantité
        for (int i = 0; i < atoms.length; i++) {
            String[] parts = atoms[i].split(":");
            // Type d'atome
            typesBuilder.append("\"").append(parts[0]).append("\"");
            // Quantité
            quantitiesBuilder.append(parts[1]);

            if (i < atoms.length - 1) { // Si ce n'est pas le dernier élément, ajoute une virgule
                typesBuilder.append(", ");
                quantitiesBuilder.append(", ");
            }
        }

        // Construit la chaîne JSON avec le formatage approprié
        String jsonString = "{\n" +
                "\t\"types\": [" + typesBuilder.toString() + "],\n" +
                "\t\"quantities\": [" + quantitiesBuilder.toString() + "]\n" +
                "}";

        // Écrit la chaîne JSON formatée dans un fichier
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("instance.json"))) {
            writer.write(jsonString);
        } catch (IOException e) {
            // Affiche l'erreur en cas de problème lors de l'écriture dans le fichier
            e.printStackTrace();
        }
    }
}


