package Text;

public class TextColor {
    private static final String DEFAULT = "\u001b[97m";
    private static final String RED = "\u001b[91m";
    private static final String GREEN = "\u001b[32m";
    private static final String BLUE = "\u001b[34m";
    private static final String YELLOW = "\u001b[33m";
    private static final String GRAY = "\u001b[37m";

    public static String red(String input){
        return RED + input + DEFAULT;
    }

    public static String green(String input){
        return GREEN + input + DEFAULT;
    }

    public static String blue(String input){
        return BLUE + input + DEFAULT;
    }

    public static String yellow(String input){
        return YELLOW + input + DEFAULT;
    }

    public static String gray(String input){
        return GRAY + input + DEFAULT;
    }

}

