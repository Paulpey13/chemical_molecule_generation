package src;

import com.google.gson.Gson;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.GraphVar;
import org.chocosolver.solver.variables.RealVar;
import org.chocosolver.solver.variables.Variable;
import org.jgrapht.Graph;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MainSpace {

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

            model.getSolver().setSearch(Search.graphVarSearch((GraphVar) vars[1]));

            long startTime = System.currentTimeMillis();
            while (model.getSolver().solve()) {
                boolean new_g = true;
                int[][] liaisons= GraphModelisationMulti.getLiaisons(vars,nbrAtom);
                Graph<GraphModelisationMulti.Node, DefaultEdge> graph1 = GraphModelisationMulti.translate((GraphVar) vars[1], atom.listTypes(),liaisons);

                for (int j = 1; j < listStruct.size(); j++) {
                    Graph<GraphModelisationMulti.Node, DefaultEdge> graph2 = listStruct.get(j);

                    VF2GraphIsomorphismInspector<GraphModelisationMulti.Node, DefaultEdge> inspector =
                            new VF2GraphIsomorphismInspector<>(graph1, graph2, new Comparator<GraphModelisationMulti.Node>() {
                                @Override
                                public int compare(GraphModelisationMulti.Node v1, GraphModelisationMulti.Node v2) {
                                    return v1.getType().equals(v2.getType()) ? 0 : -1;
                                }
                            }, null);

                    if(inspector.isomorphismExists()){
                        new_g = false ;
                    }
                }

                if (new_g) {
                    // Si c'est pas un isomère
                    // On crée la visualisation 2D de la structure du graohe
                    String graphe_visualized = GraphModelisationMulti.getGraphViz(graph1);
                    String graphe_visualizedPath = "./RAMI/graph_output/graph_" + listStruct.size() + "_" + LocalDateTime.now().format(formatter) + ".png";
                    try {
                        Graphviz.fromString(graphe_visualized)
                                .render(Format.PNG)
                                .toFile(new File(graphe_visualizedPath));

                    } catch (IOException e) {
//                    e.printStackTrace();
                    }

                    // On ajoute le graphe à la liste
                    listStruct.add(graph1);

                    // On crée la modélisation pour chercher un placement dans l'espace
                    Modelisation mod2 = new Modelisation(atom, liaisons,0);
                    Model model2 = mod2.getModel();

                    if(model2.getSolver().solve()){
                        // Si il existe une solution, on crée le CML correspondant
                        Variable[] vars2 = model2.getVars();

                        // Génération du CML
                        GraphVar graphVar = (GraphVar) vars[1];
                        RealVar[] xs = mod2.getXs(); // Assurez-vous que Modelisation a des getters pour xs, ys, zs
                        RealVar[] ys = mod2.getYs();
                        RealVar[] zs = mod2.getZs();

                        String[] atomTypes = CML_generator.buildAtomTypesArray(atom);
                        CML_generator.generateCMLFiles(graphVar, atomTypes, xs, ys, zs,data, liaisons);
                    }
                    else{
                        System.out.println("Pas de solution de coordonnées pour cette structure de graphe");
                    }

                }else {
//                    System.out.println("ISOMERE");
                }

            }

            System.out.println(listStruct.size());
        } catch (IOException e) {
//            e.printStackTrace();
        }

    }
}