package src;

import com.google.gson.Gson;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.variables.*;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.chocosolver.solver.search.strategy.Search;

import java.io.*;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;


public class Mainviz {

    public static String data;

    public static void main(String[] args) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMddHHmmss");
        // Données d'entrée au format JSON
        data = "./RAMI/data/test.json";

//        String data = "data/test.json"; //pour paul sinon ça marche pas
        // Lecture des doonées
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(data)) {
            Atom atom = gson.fromJson(reader, Atom.class);
            int nbrAtom=atom.getNbAtoms();

            Map<Integer, String> list_correspondance = AtomIndexer.getAtomIndices(data);

            // Création de la modélisation
            GraphModelisation_double_bonds mod = new GraphModelisation_double_bonds(atom);
            Model model = mod.getModel();
            System.out.println("Fin de Modélisation");

            // Résolution
            Solver solver = model.getSolver();

            Variable[] vars = model.getVars();

            // Recherche de toutes les solutions
            // On itère sur la variable de graphe
            List<Graph> listStruct = new ArrayList<>();

            model.getSolver().setSearch(Search.graphVarSearch((GraphVar) vars[1]));

            long startTime = System.currentTimeMillis();
            while (model.getSolver().solve()) {
                int[][] liaisons= getLiaisons(vars,nbrAtom);
                listStruct.add(GraphModelisation_double_bonds.translate((GraphVar) vars[1], atom.listTypes(),liaisons));

            }
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;

            if (listStruct.size() == 0) {
                System.out.println("Aucunes solutions trouvées");
            } else {
                for (int i = 0; i < listStruct.size(); i++) {
                    for (int j = i + 1; j < listStruct.size(); j++) {
                        Graph<GraphModelisation_double_bonds.Node, DefaultEdge> graph1 = listStruct.get(i);
                        Graph<GraphModelisation_double_bonds.Node, DefaultEdge> graph2 = listStruct.get(j);

                        VF2GraphIsomorphismInspector<GraphModelisation_double_bonds.Node, DefaultEdge> inspector =
                                new VF2GraphIsomorphismInspector<>(graph1, graph2, new Comparator<GraphModelisation_double_bonds.Node>() {
                                    @Override
                                    public int compare(GraphModelisation_double_bonds.Node v1, GraphModelisation_double_bonds.Node v2) {
                                        return v1.getType().equals(v2.getType()) ? 0 : -1;
                                    }
                                }, null);

                        if (inspector.isomorphismExists()) {
                            // Les graphes sont isomorphes; retirez un des graphes
                            listStruct.remove(j);
                            j--; // Ajustez l'indice après la suppression
                        }
                    }
                }
            }
            int i = 0;
            for (Graph g : listStruct) {
                System.out.println("Maintenant, " + listStruct.size() + " solutions");
                System.out.println("Temps d'exécution en millisecondes: " + duration);

                String graphe_visualized = GraphModelisation_double_bonds.getGraphViz(g);

                String graphe_visualizedPath = "./RAMI/graph_output/graph_" + i + "_" + LocalDateTime.now().format(formatter) + ".png";
                i += 1;
                try {
                    Graphviz.fromString(graphe_visualized)
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
    public static int[][] getLiaisons(Variable[] vars, int n) {
        int[][] liaisons = new int[n][n];

        for (Variable var : vars) {
            String name = var.getName();
            if (name.startsWith("Liaison")) {
                if (var.isInstantiated()) {
                    int value = ((IntVar) var).getValue(); // Cast en IntVar si c'est le type de vos variables
                    String[] parts = name.split("_");
                    int i = Integer.parseInt(parts[1]);
                    int j = Integer.parseInt(parts[2]);

                    liaisons[i][j] = value;
                    liaisons[j][i] = value; // Si la liaison est symétrique
                }
            }
        }

        return liaisons;
    }
}