package src.experiences;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExperienceMaker {
    public static void main(String[] args) {
        String[] configurations = {"H:8,C:3,O:2","H:2,O:1","H:3,C:1,O:1","H:6,C:2,O:1","H:6,C:2,O:2","H:8,C:3,O:2","H:2,O:2","C:1,O:2","C:1,H:1,N:1,O:1","H:6,C:1,O:2"} ;
        //LE H TOUJOURS AVANT LE C SINON CA MARCHE PAS
        InstanceMaker instanceMaker = new InstanceMaker();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
        StringBuilder experimentResults = new StringBuilder();
        String cmlOutputPath = "cml_output";

        for (String config : configurations) {
            long startTime = System.currentTimeMillis();
            String dateTimeString = LocalDateTime.now().format(formatter);
//            String outputPath = "input/instance_" + dateTimeString + ".json"; //Pour Paul
            String outputPath = "./RAMI/input/instance_" + dateTimeString + ".json";
            instanceMaker.createInstance(config, outputPath);

            // Compter les fichiers avant l'exécution de Main
            File cmlDirectory = new File(cmlOutputPath);
            String[] filesBefore = cmlDirectory.list();

            String[] mainExperienceArgs = {outputPath};
            src.MainSpace.main(mainExperienceArgs);  // Main doit être adapté pour retourner ou enregistrer le nombre de solutions et le temps d'exécution

            // Compter les fichiers après l'exécution de Main
            String[] filesAfter = cmlDirectory.list();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            // Calculer le nombre de solutions en fonction du nombre de fichiers générés
            int numberOfSolutions = filesAfter.length - filesBefore.length;

            experimentResults.append("Configuration: ").append(config)
                    .append(", Solutions: ").append(numberOfSolutions)
                    .append(", Execution Time: ").append(duration).append(" ms\n");

            try {
                Thread.sleep(500); //pour etre sur que y'a pas d'overwrite avec l'heure
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        String experimentOutputPath = "cml_output/experiment_" + LocalDateTime.now().format(formatter) + ".txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(experimentOutputPath))) {
            writer.write(experimentResults.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
