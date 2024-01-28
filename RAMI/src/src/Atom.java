package src;

import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.Variable;

import java.util.Arrays;

public class Atom {
    private String[] types;
    private int[] quantities;

    private int[][] structure;
    public static int SIMPLE_LIAISON = 0;
//    public static int DOUBLE_LIAISON = 1;

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
        }
        return typesList;
    }

    public String printListTypes(){
        String[] t = listTypes();
        String st = "";
        for(String s : t){
            st = st +" "+ s;
        }
        return st;
    }

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
    public int getNbAtoms() {
        int n = 0;

        for (int q : quantities) {
            n += q;
        }
        return n;
    }

}