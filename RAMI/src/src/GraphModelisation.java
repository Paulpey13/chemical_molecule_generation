package src;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.variables.*;
import org.chocosolver.util.objects.graphs.GraphFactory;
import org.chocosolver.util.objects.graphs.UndirectedGraph;
import org.chocosolver.util.objects.setDataStructures.SetType;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import java.util.ArrayList;
import java.util.Map;

/**
 * La classe <code>GraphModelisation</code> fournit des méthodes pour la modélisation du problème de recherche de structure de graphe correspondant à une formule chimique données.
 * <p>
 * Cette classe permet de définir les différentes variables et contraintes permettant de définir une instance CSP correspondant au problème de génération de structure sous forme de graphe d'une molécule à partir de sa formule chimique.
 * Cette modélisation ne prend en compte que les liaisons simples entre atomes
 * </p>
 */
public class GraphModelisation {

    /**
     * Cette classe <code>Node</code> permet de modéliser un noeud du graphe en fonction de som numéro et de son étiquette (type d'atome).
     */
    static class Node {
        private int num;
        private String type;

        public Node(int num, String type) {
            this.num = num;
            this.type = type;
        }
        public int getNum(){
            return this.num;
        }
        public String getType(){
            return this.type;
        }
        @Override
        public String toString(){
            return "("+this.num+" ; "+this.type+")";
        }
    }
    Model model;

    /**
     * Permet la création d'une instance CSP correspondant à la génération de structure de graphe d'une molécule.
     * Cette modélisation prend en compte les liaisons simples, doubles, et triples.
     * @param atom la molécule en question
     */
    GraphModelisation(Atom atom){
        // List des valences de chaque atome
        Map<String, Integer> valenceMap = MoleculeUtils.VALENCE_MAP;

        // List des distances
        Map<String, BondDistance> distanceMap = MoleculeUtils.BOND_DISTANCES;

        //Creation d'un model
        model = new Model("Molecule Generation Problem");

        int n = atom.nbAtom(); // Nombre d'atomes de la molécule
        String[] types = atom.getTypes();

        // VARIABLES
        // An undirected graph
        UndirectedGraph LB = GraphFactory.makeStoredUndirectedGraph(model, n, SetType.BITSET, SetType.BITSET);
        // the last parameter indicates that a complete graph is required
        UndirectedGraph UB = GraphFactory.makeCompleteStoredUndirectedGraph(model, n, SetType.BITSET, SetType.BITSET, true);

        // On ajoute les liaisons pré-établit
        int[][] structure = atom.getStructure();
        int[] liaison;
        for(int i=0; i<structure.length; i++){
            liaison = structure[i];
            if(liaison[1] == Atom.SIMPLE_LIAISON){
                LB.addEdge(liaison[0], liaison[2]);
            }
        }

        UndirectedGraphVar g = model.graphVar("g", LB, UB);


        // CONTRAINTES

        // On définit le degré de chaque sommet
        IntVar[] degrees = new IntVar[n];
        int[] quantities = atom.getQuantities();
        String current_type = types[0];
        int c = 0;
        int indice_type = 0;
        for (int i = 0; i < n; i++) {
            if (c == quantities[indice_type]){
                c = 0;
                indice_type += 1;
            }
            String id = "id"+i;

            degrees[i] = model.intVar(id,valenceMap.get(types[indice_type]));
            c += 1;
        }
        model.degrees(g,degrees).post();

        // Contrainte de connexité
        model.connected(g).post();


    }

    /**
     * Permet de traduire les caractèristique d'une variable de graphe de notre modélisation en variable de graphe de la bibliothèqye JGraph.
     * @param g une variable de graphe de chocosolver
     * @param types le tableau des types de chaque sommet du graphe pour avoir leur étiquette.
     * @return Renvoie la correspondance du graphe de JGraph.
     */
    public static Graph translate(GraphVar g, String[] types){
        Graph<Node, DefaultEdge> transG = new SimpleGraph<>(DefaultEdge.class);

        int nbNodes = types.length;
        Node[] nodes = new Node[nbNodes];
        for(int i=0; i<nbNodes; i++){
            Node node = new Node(i, types[i]);
            nodes[i] = node;
            transG.addVertex(node);
        }

        for(int i=0; i<nbNodes; i++){
            for(int j=i+1; j< nbNodes; j++){
                if(g.getValue().containsEdge(i,j)){
                    transG.addEdge(nodes[i], nodes[j]);
                }
            }
        }
       return transG;
    }

    /**
     * Permet d'obtenir le format Graphviz pour l'affichage.
     * @param g un graphe de la bibliothèque JMol
     * @return Renvoie le format graphviz sous forme de chaine de caractère.
     */
    public static String getGraphViz(Graph g){
        String gv = "graph G{ \n";

        for(Object o : g.vertexSet()){
            Node n = (Node) o;
            gv += ""+n.getNum()+" [label=\""+n.getType()+"_"+n.getNum()+"\"]; \n";
        }
        gv +="\n";
        for(Object o : g.edgeSet()){
            DefaultEdge ed = (DefaultEdge) o;
            String edS = ed.toString();
            // Supprimer les caractères non nécessaires
            String cleanedInput = edS.replaceAll("[()]", "");

            // Séparer la chaîne en éléments
            String[] elements = cleanedInput.split(" ");

            // Créer une liste pour stocker les éléments
            ArrayList<String> filteredList = new ArrayList<>();
            for(String e : elements){
                if(e.compareTo(";") != 0 && e.compareTo(":") !=0){
                    filteredList.add(e);
                }
            }
            gv = gv+""+filteredList.get(0)+" -- "+filteredList.get(2)+"; \n";
        }
        gv = gv+"}\n";



        return gv;
    }


    public Model getModel() {
        return model;
    }
}
