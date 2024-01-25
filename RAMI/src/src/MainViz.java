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
import java.util.Comparator;
import java.util.Map;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;
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
        data = "./RAMI/data/test.json";

//        String data = "data/test.json"; //pour paul sinon ça marche pas
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
            List<Graph> listStruct = new ArrayList<>();
            model.getSolver().setSearch(Search.graphVarSearch((GraphVar) vars[0]));
            while (model.getSolver().solve()) {
                listStruct.add(GraphModelisation.translate((GraphVar) vars[0], atom.listTypes()));

            }

            if(listStruct.size() == 0){
                System.out.println("Aucunes solutions trouvées");
            }else{
                for(int i=0; i<listStruct.size(); i++){
                    for(int j=i+1; j<listStruct.size(); j++){
                        Graph<GraphModelisation.Node, DefaultEdge> graph1 = listStruct.get(i);
                        Graph<GraphModelisation.Node, DefaultEdge> graph2 = listStruct.get(j);

                        VF2GraphIsomorphismInspector<GraphModelisation.Node, DefaultEdge> inspector =
                                new VF2GraphIsomorphismInspector<>(graph1, graph2, new Comparator<GraphModelisation.Node>() {
                                    @Override
                                    public int compare(GraphModelisation.Node v1, GraphModelisation.Node v2) {
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
            for(Graph g : listStruct){
                System.out.println("Maintenant, "+listStruct.size()+" solutions");
                System.out.println(g);
                System.out.println(GraphModelisation.getGraphViz(g));

                String graphe_visualized = GraphModelisation.getGraphViz(g);

                String graphe_visualizedPath = "./RAMI/graph_output/graph_"+i+"_"+ LocalDateTime.now().format(formatter) + ".png";
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
}