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

    public static String data;
    public static void main(String[] args) {

        // Données d'entrée au format JSON
        data = "./RAMI/data/test.json";

//        String data = "data/test.json"; //pour paul sinon ça marche pas
        // Lecture des doonées
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(data)) {
            Atom atom = gson.fromJson(reader, Atom.class);
            System.out.println(atom);

            // Création de la modélisation
            GraphModelisation mod = new GraphModelisation(atom);
            Model model = mod.getModel();
            System.out.println("Fin de Modélisation");

            // Résolution
            Solver solver = model.getSolver();
            Variable[] vars = model.getVars();


            // Recherche de toutes les solutions
            // On itère sur la variable de graphe

            model.getSolver().setSearch(Search.graphVarSearch((GraphVar) vars[0]));
           while (model.getSolver().solve()) {
               Modelisation mod2 = new Modelisation(atom, (GraphVar) vars[0], -1);
               Model model2 = mod2.getModel();

               if(model2.getSolver().solve()){
                   Variable[] vars2 = model2.getVars();
                   for(Variable v : vars2){
                       System.out.println(v);
                   }
                   // Génération du CML
                   GraphVar graphVar = (GraphVar) vars[0];
                   RealVar[] xs = mod2.getXs(); // Assurez-vous que Modelisation a des getters pour xs, ys, zs
                   RealVar[] ys = mod2.getYs();
                   RealVar[] zs = mod2.getZs();
                   String[] atomTypes = CML_generator.buildAtomTypesArray(atom);
                   CML_generator.generateCMLFiles(graphVar, atomTypes, xs, ys, zs,data);
               }
               else{
                   System.out.println("Pas de solution de coordonnées pour cette structure de graphe");
               }

           }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
