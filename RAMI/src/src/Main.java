package src;

import com.google.gson.Gson;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.*;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import com.google.gson.Gson;
import org.chocosolver.solver.search.strategy.Search;

import java.io.FileReader;
import java.io.IOException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import src.Atom;
import src.Modelisation;

import static org.chocosolver.solver.search.strategy.Search.*;


public class Main {

    public static void main(String[] args) {

        // Données d'entrée au format JSON
        String data = "data/test.json";
//        String data = "data/test.json"; //pour paul sinon ça marche pas
        // Lecture des doonées
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(data)) {
            Atom atom = gson.fromJson(reader, Atom.class);
            System.out.println(atom);

            // Création de la modélisation
            Modelisation mod = new Modelisation(atom);
            Model model = mod.getModel();
            System.out.println("Fin de Modélisation");

            // Résolution
            Solver solver = model.getSolver();
            Variable[] var = model.getVars();

            // Définition de la stratégie de recherche
//            model.getSolver().setSearch(Search.graphVarSearch((GraphVar) var[0]));
//            model.getSolver().setSearch(Search.inputOrderLBSearch(var[0]);
            Solution solution = model.getSolver().findSolution();
//            List<Solution> sols = solver.findAllSolutions();
            if (solution != null) {
                // ON affiche le graphe
                System.out.println("Solutions");
                solver.printShortStatistics();
//                solver.showSolutions();
//                System.out.println(var[0]);
                for(int i = 1; i<10; i++){
                    System.out.println(var[i].asRealVar());
                }


                Variable[] vars = model.getVars();
                GraphVar graphVar = (GraphVar) vars[0];
                String[] atomTypes = CML_generator.buildAtomTypesArray(atom);
                CML_generator.generateCMLFiles(graphVar, atomTypes);

            } else {
                System.out.println("Aucune solution trouvée.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}