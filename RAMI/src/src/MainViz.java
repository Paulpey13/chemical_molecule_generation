package src;

import com.google.gson.Gson;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.*;
import org.chocosolver.util.objects.graphs.GraphFactory;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import com.google.gson.Gson;
import org.chocosolver.solver.search.strategy.Search;

import java.io.*;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import src.Atom;
import src.Modelisation;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.chocosolver.solver.search.strategy.Search.*;


public class MainViz {

    public static String data;
    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
        // Données d'entrée au format JSON
        //data = "./RAMI/data/test.json";

        String data = "data/test.json"; //pour paul sinon ça marche pas
        // Lecture des doonées
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(data)) {
            Atom atom = gson.fromJson(reader, Atom.class);

            Map<Integer, String> list_correspondance = AtomIndexer.getAtomIndices(data);

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
                String graphe_visualized=((GraphVar<?>) vars[0]).getValue().graphVizExport();

                String renamed_graph=AtomIndexer.convertGraphVizOutput(graphe_visualized, list_correspondance);
                String graphe_visualizedPath = "graph_output/graph_" + LocalDateTime.now().format(formatter) + ".png";

                try {
                    Graphviz.fromString(renamed_graph)
                            .render(Format.PNG)
                            .toFile(new File(graphe_visualizedPath));

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}