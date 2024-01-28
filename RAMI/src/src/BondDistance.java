package src;

/**
 * Cette classe représente une distance de liaison entre deux atomes.
 */
public class BondDistance {
    private final int typeLiaison; // Type de liaison
    private final int minDistance; // Distance minimale
    private final int maxDistance; // Distance maximale

    /**
     * Constructeur de la classe BondDistance.
     *
     * @param minDistance La distance minimale.
     * @param maxDistance La distance maximale.
     * @param typeLiaison Le type de liaison.
     */
    public BondDistance(int minDistance, int maxDistance, int typeLiaison) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.typeLiaison = typeLiaison;
    }

    /**
     * Obtient la distance minimale.
     *
     * @return La distance minimale.
     */
    public int getMinDistance() {
        return minDistance;
    }

    /**
     * Obtient la distance maximale.
     *
     * @return La distance maximale.
     */
    public int getMaxDistance() {
        return maxDistance;
    }

    /**
     * Obtient le type de liaison.
     *
     * @return Le type de liaison.
     */
    public int getTypeLiaison() {
        return typeLiaison;
    }

    /**
     * Convertit l'objet en une chaîne de caractères pour l'affichage.
     *
     * @return Une chaîne de caractères représentant la distance de liaison.
     */
    @Override
    public String toString() {
        return "(" + minDistance + ", " + maxDistance + ") type : " + typeLiaison;
    }
}