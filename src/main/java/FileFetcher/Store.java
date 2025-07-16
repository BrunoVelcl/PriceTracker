package FileFetcher;

public enum Store{
    LIDL,
    KAUFLAND,
    SPAR,
    STUDENAC,
    PLODINE;

    public static Store fromOrdinal(int ordinal){
        return values()[ordinal];
    }
}

