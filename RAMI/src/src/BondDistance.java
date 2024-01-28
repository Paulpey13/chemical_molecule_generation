package src;

public class BondDistance {
    private final int typeLiaison;
    private final int minDistance;
    private final int maxDistance;

    public BondDistance(int minDistance, int maxDistance, int typeLiaison) {
        this.minDistance = minDistance;
        this.maxDistance = maxDistance;
        this.typeLiaison = typeLiaison;
    }

    public int getMinDistance() {
        return minDistance;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public int getTypeLiaison() {
        return typeLiaison;
    }

    @Override
    public String toString() {
        return "(" + minDistance + ", " + maxDistance + ") type : "+ typeLiaison;
    }
}
