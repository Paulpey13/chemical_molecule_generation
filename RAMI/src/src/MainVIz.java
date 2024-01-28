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

public class MainVIz {
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
            GraphModelisationMulti mod = new GraphModelisationMulti(atom);
            Model model = mod.getModel();
            System.out.println("Fin de Modélisation");

            // Résolution
            Solver solver = model.getSolver();

            Variable[] vars = model.getVars();

            // Recherche de toutes les solutions
            // On itère sur la variable de graphe
            List<Graph> listStruct = new ArrayList<>();

            long startTime = System.currentTimeMillis();
            int index = atom.getNbAtoms()*(atom.getNbAtoms()-1)/2 +2;
            while (model.getSolver().solve()) {
                int[][] liaisons= GraphModelisationMulti.getLiaisons(vars,nbrAtom);
                listStruct.add(GraphModelisationMulti.translate((GraphVar) vars[index], atom.listTypes(),liaisons));
            }

            if (listStruct.size() == 0) {
                System.out.println("Aucunes solutions trouvées");
            } else {
                for (int i = 0; i < listStruct.size(); i++) {
                    for (int j = i + 1; j < listStruct.size(); j++) {
                        Graph<GraphModelisationMulti.Node, DefaultEdge> graph1 = listStruct.get(i);
                        Graph<GraphModelisationMulti.Node, DefaultEdge> graph2 = listStruct.get(j);

                        VF2GraphIsomorphismInspector<GraphModelisationMulti.Node, DefaultEdge> inspector;
                        inspector = new VF2GraphIsomorphismInspector<>(graph1, graph2, new Comparator<GraphModelisationMulti.Node>() {
                            @Override
                            public int compare(GraphModelisationMulti.Node v1, GraphModelisationMulti.Node v2) {
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
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            int i = 0;
            for (Graph g : listStruct) {
                System.out.println("Maintenant, " + listStruct.size() + " solutions");
                System.out.println("Temps d'exécution en millisecondes: " + duration);

                String graphe_visualized = GraphModelisationMulti.getGraphViz(g);

                String graphe_visualizedPath = "./RAMI/graph_output/graph_" + i + "_" + LocalDateTime.now().format(formatter) + ".png";
                i += 1;
                try {
                    Graphviz.fromString(graphe_visualized)
                            .render(Format.PNG)
                            .toFile(new File(graphe_visualizedPath));

                } catch (IOException e) {
//                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
//            e.printStackTrace();
        }

    }
}
