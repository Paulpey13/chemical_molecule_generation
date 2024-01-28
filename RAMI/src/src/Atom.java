package src;
import java.util.Arrays;

/**
 * La classe <code>Atom</code> fournit des méthodes pour stocker et manipuler les formules chimies données en entrée
 * <p>
 * Une formule chimique est composée de plusieurs <code>types</code> d'atome, chacun en une certaine <code>quantities</code>.
 * De plus, on peut établir au préalable certaines liaisons entre certains atomes. Ces liaisons en question sont stockées dans <code>structure</code>
 * </p>
 *
 */
public class Atom {
    private String[] types; // Différents types des atomes présents dans la molécule
    private int[] quantities; // La quantités de chaque type d'atome présents

    private int[][] structure; // Stockage des liaisons pré-établit

    public static int SIMPLE_LIAISON =1;
    public static int DOUBLE_LIAISON =2;
    public static int TRIPLE_LIAISON =3;


    /**
     * Affiche la liste de chaque atome et leur type
     *
     * @return La chaine de caracère correspondante.
     */
    public String printListTypes(){
        String[] t = listTypes();
        String st = "";
        for(String s : t){
            st = st +" "+ s;
        }
        return st;
    }


    /**
     * Renvoie un tableau de la taille du nombre d'atome de la molécule et associe le type à chaque atome d'incide l'indice du tableau.
     *
     * @return Le tableau du type de chaque atome de la molécule
     */
    public String[] listTypes(){
        int n= this.nbAtom();
        String[] typesList = new String[n];
        String type;
        int maxIndex;
        for(int i =0; i<n; i++){
            maxIndex = 0;
            type = "None";
            for(int j = 0; j<this.quantities.length; j++){
                if(i < (maxIndex + this.quantities[j]) && type.compareTo("None") == 0){
                    type = this.types[j];
                }
                maxIndex += this.quantities[j];
            }
            typesList[i] = type;
            type = "None";

        }
        return typesList;
    }

    /**
     * Permet d'obtenir la formule chimique associé à la molécule
     * @return La chaine de caractères correspondant à la formule chimique
     */
    public String chimForm(){
        String form = "";
        for(int i = 0; i<quantities.length; i++){
            form = form +types[i]+quantities[i];
        }
        return form;
    }

    /**
     * Permet d'obtenir le nombre d'atome présent dans la molécule
     * @return Renvoie le nombre d'atome
     */
    public int nbAtom(){
        int sum = 0;
        for (int i = 0; i<quantities.length; i++) {
            sum += quantities[i];
        }
        return sum;
    }

    public int nbTypes(){
        return quantities.length;
    }

    public int[][] getStructure(){
        return structure;
    }

    public String[] getTypes(){
        return types;
    }

    public int[] getQuantities() {
        return quantities;
    }

    public void setQuantities(int[] quantities) {
        this.quantities = quantities;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    @Override
    public String toString() {
        return "Atom{" +
                "types=" + Arrays.toString(types) +
                ", quantities=" + Arrays.toString(quantities) +
                '}';
    }

    /**
     * Permet d'obtenir le nombre d'atome présent dans la molécule
     * @return Renvoie le nombre d'atome
     */
    public int getNbAtoms() {
        int n = 0;

        for (int q : quantities) {
            n += q;
        }
        return n;
    }

}