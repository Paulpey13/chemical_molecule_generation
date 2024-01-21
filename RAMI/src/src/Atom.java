package src;

import org.chocosolver.util.objects.graphs.UndirectedGraph;

import java.util.Arrays;

public class Atom {
    private String[] types;
    private int[] quantities;

    private int[][] structure;
    public static int SIMPLE_LIAISON = 0;
//    public static int DOUBLE_LIAISON = 1;


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
}