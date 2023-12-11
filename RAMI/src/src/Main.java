package src;

import com.google.gson.Gson;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.GraphVar;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import com.google.gson.Gson;

import java.io.FileReader;
import java.io.IOException;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.List;
import src.Atom;
import src.Modelisation;


public class Main {

    public static void main(String[] args) {

        // Données d'entrée au format JSON
        String data = "./RAMI/data/test.json";
//        String data = "data/test.json"; //pour paul sinon ça marche pas
        // Lecture des doonées
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(data)) {
            Atom atom = gson.fromJson(reader, Atom.class);
            System.out.println(atom);

            // Création de la modélisation
            Modelisation mod = new Modelisation(atom);
            Model model = mod.getModel();

            // Résolution
            Solver solver = model.getSolver();
            Variable[] var = model.getVars();

            Solution solution = model.getSolver().findSolution();
            if (solution != null) {
<<<<<<< HEAD
                System.out.println(solution);

=======
                // ON affiche le graphe
                System.out.println(var[0]);
>>>>>>> cce14e552a929d255ed95578299f012b8fcd0e20
            } else {
                System.out.println("Aucune solution trouvée.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }



    }
}
