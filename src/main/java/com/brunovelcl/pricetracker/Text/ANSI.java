package com.brunovelcl.pricetracker.Text;

import java.io.IOException;

public abstract class ANSI {

    private static final String ESC = "\u001b";
    private static final String ESC_CTRL = "\u001b[";
    private static final char SGR = 'm'; //Set ANSI mode
    private static final char DELIMITER = ';';


    /**
     * Resets all applied attributes
     */
    public static void reset() {
        System.out.print(ESC_CTRL + "0" + SGR);
    }

    /**
     * Saves everything on screen
     */
    public static void saveScreen() {
        System.out.print(ESC_CTRL + "?47h");
    }

    /**
     * Restores saved screen
     */
    public static void restoreScreen() {
        System.out.print(ESC_CTRL + "?47l");
    }

    public static class Style {
        private static final char BOLD = '1';
        private static final char DIM = '2';
        private static final String RESET_BOLD_DIM = ESC_CTRL + "22" + SGR;
        private static final char ITALIC = '3';
        private static final String RESET_ITALIC = ESC_CTRL + "23" + SGR;
        private static final char UNDERLINE = '4';
        private static final String RESET_UNDERLINE = ESC_CTRL + "24" + SGR;
        private static final char BLINKING = '5';
        private static final String RESET_BLINKING = ESC_CTRL + "25" + SGR;
        private static final char INVERSE_REVERSE = '7';
        private static final String RESET_INVERSE_REVERSE = ESC_CTRL + "27" + SGR;
        private static final char HIDDEN_INVISIBLE = '8';
        private static final String RESET_HIDDEN_INVISIBLE = ESC_CTRL + "28" + SGR;
        private static final char STRIKETHROUGH = '9';
        private static final String RESET_STRIKETHROUGH = ESC_CTRL + "29" + SGR;


        /**
         * Returns a string with the bold attribute applied
         */
        public static String bold(String string) {
            return ESC_CTRL + BOLD + SGR + string + RESET_BOLD_DIM;
        }

        /**
         * Enables bold for all output, turn off with disableBoldAndDim() or ANSI.reset()
         */
        public static void enableBold() {
            System.out.print(ESC_CTRL + BOLD + SGR);
        }

        /**
         * Returns a string with the dim attribute applied.
         * Might not work on terminals without full ANSI support.
         */
        public static String dim(String string) {
            return ESC_CTRL + DIM + SGR + string + RESET_BOLD_DIM;
        }

        /**
         * Enables dim for all output, turn off with disableBoldAndDim() or ANSI.reset()
         * Might not work on terminals without full ANSI support.
         */
        public static void enableDim() {
            System.out.print(ESC_CTRL + DIM + SGR);
        }

        /**
         * Disables the bold and dim attributes
         */
        public static void disableBoldAndDim() {
            System.out.print(RESET_BOLD_DIM);
        }

        /**
         * Returns a string with the italic attribute applied
         */
        public static String italic(String string) {
            return ESC_CTRL + ITALIC + SGR + string + RESET_ITALIC;
        }

        /**
         * Enables italic for all output, turn off with disableItalic() or ANSI.reset()
         */
        public static void enableItalic() {
            System.out.print(ESC_CTRL + ITALIC + SGR);
        }

        /**
         * Disables the italic attribute
         */
        public static void disableItalic() {
            System.out.print(RESET_ITALIC);
        }

        /**
         * Returns a string with the underline attribute applied
         */
        public static String underline(String string) {
            return ESC_CTRL + UNDERLINE + SGR + string + RESET_UNDERLINE;
        }

        /**
         * Enables underline for all output, turn off with disableUnderline() or ANSI.reset()
         */
        public static void enableUnderline() {
            System.out.print(ESC_CTRL + UNDERLINE + SGR);
        }

        /**
         * Disables the underline attribute
         */
        public static void disableUnderline() {
            System.out.print(RESET_UNDERLINE);
        }

        /**
         * Returns a string with the blinking attribute applied
         * Might not work on terminals without full ANSI support.
         */
        public static String blinking(String string) {
            return ESC_CTRL + BLINKING + SGR + string + RESET_BLINKING;
        }

        /**
         * Enables blinking for all output, turn off with disableBoldAndDim() or ANSI.reset()
         * Might not work on terminals without full ANSI support.
         */
        public static void enableBlinking() {
            System.out.print(ESC_CTRL + BLINKING + SGR);
        }

        /**
         * Disables the blinking attribute
         */
        public static void disableBlinking() {
            System.out.print(RESET_BLINKING);
        }

        /**
         * Returns a string with the inverse/reverse attribute applied
         * Might not work on terminals without full ANSI support.
         */
        public static String inverseReverse(String string) {
            return ESC_CTRL + INVERSE_REVERSE + SGR + string + RESET_INVERSE_REVERSE;
        }

        /**
         * Enables inverse/reverse for all output, turn off with disableInverseReverse() or ANSI.reset()
         * Might not work on terminals without full ANSI support.
         */
        public static void enableInverseReverse() {
            System.out.print(ESC_CTRL + INVERSE_REVERSE + SGR);
        }

        /**
         * Disables the inverse/reverse attribute
         */
        public static void disableInverseReverse() {
            System.out.print(RESET_INVERSE_REVERSE);
        }

        /**
         * Returns a string with the hidden/invisible attribute applied
         * Might not work on terminals without full ANSI support.
         */
        public static String hiddenInvisible(String string) {
            return ESC_CTRL + HIDDEN_INVISIBLE + SGR + string + RESET_HIDDEN_INVISIBLE;
        }

        /**
         * Enables hidden/invisible for all output, turn off with disableHiddenInvisible() or ANSI.reset()
         * Might not work on terminals without full ANSI support.
         */
        public static void enableHiddenInvisible() {
            System.out.print(ESC_CTRL + HIDDEN_INVISIBLE + SGR);
        }

        /**
         * Disables the hidden/invisible attribute
         */
        public static void disableHiddenInvisible() {
            System.out.print(RESET_HIDDEN_INVISIBLE);
        }

        /**
         * Returns a string with the strikethrough attribute applied
         */
        public static String strikethrough(String string) {
            return ESC_CTRL + STRIKETHROUGH + SGR + string + RESET_STRIKETHROUGH;
        }

        /**
         * Enables strikethrough for all output, turn off with disableStrikethrough() or ANSI.reset()
         */
        public static void enableStrikethrough() {
            System.out.print(ESC_CTRL + STRIKETHROUGH + SGR);
        }

        /**
         * Disables the strikethrough attribute
         */
        public static void disableStrikethrough() {
            System.out.print(RESET_STRIKETHROUGH);
        }


    }

    public static class Cursor {

        /**
         * Moves cursor to "home" position (0,0) on screen.
         */
        public static void zero() {
            System.out.print(ESC_CTRL + 'H');
        }

        /**
         * Moves cursor to specified (row, column)
         */
        public static void moveTo(int row, int column) {
            System.out.print(ESC_CTRL + row + DELIMITER + column + 'H');
        }

        /**
         * Moves cursor up for a number of entered lines
         */
        public static void moveUp(int lines) {
            System.out.print(ESC_CTRL + lines + 'A');
        }

        /**
         * Moves cursor down for a number of lines
         */
        public static void moveDown(int lines) {
            System.out.print(ESC_CTRL + lines + 'B');
        }

        /**
         * Moves cursor right for a number of columns
         */
        public static void moveRight(int columns) {
            System.out.print(ESC_CTRL + columns + 'C');
        }

        /**
         * Moves cursor left for a number of columns
         */
        public static void moveLeft(int columns) {
            System.out.print(ESC_CTRL + columns + 'D');
        }

        /**
         * Moves cursor to the beginning of the next line for entered lines down
         */
        public static void moveDownStartOfLine(int lines) {
            System.out.print(ESC_CTRL + lines + 'E');
        }

        /**
         * Moves cursor to the beginning of the previous line for entered lines up
         */
        public static void moveUpStartOfLine(int lines) {
            System.out.print(ESC_CTRL + lines + 'F');
        }

        /**
         * Moves cursor to entered column
         */
        public static void moveToColumn(int column) {
            System.out.print(ESC_CTRL + column + 'G');
        }

        /**
         * Saves cursor position (DEC)
         */
        public static void savePositionDEC() {
            System.out.print(ESC + '7');
        }

        /**
         * Restores cursor position (DEC)
         */
        public static void restorePositionDEC() {
            System.out.print(ESC + '8');
        }

        /**
         * Saves cursor position (SCO)
         */
        public static void savePositionSCO() {
            System.out.print(ESC_CTRL + 's');
        }

        /**
         * Restores cursor position (SCO)
         */
        public static void restorePositionSCO() {
            System.out.print(ESC_CTRL + 'u');
        }

        /**
         * Makes cursor invisible
         */
        public static void invisible() {
            System.out.print(ESC_CTRL + "?25l");
        }

        /**
         * Makes cursor visible
         */
        public static void visible() {
            System.out.print(ESC_CTRL + "?25h");
        }

        /**
         * Return the cursors current row and column.
         * WARNING: This method will only work on Unix-like systems!
         */
        public static int[] getPosition() throws IOException, InterruptedException {
            StringBuilder sb = new StringBuilder();
            String[] start = {"/bin/sh", "-c", "stty -echo -icanon min 1 time 1 < /dev/tty"};
            String[] end = {"/bin/sh", "-c", "stty sane < /dev/tty"};
            String escCode = "\033[6n";
            int[] result = new int[2];

            Runtime.getRuntime().exec(start).waitFor();
            System.out.write(escCode.getBytes());
            System.out.flush();

            int cha = System.in.read();
            while (cha != -1) {
                sb.append((char) cha);
                if (cha == 'R') {
                    break;
                }
                cha = System.in.read();
            }
            System.out.println(sb.length());
            System.out.println(sb);
            Runtime.getRuntime().exec(end).waitFor();

            int endMessage = sb.length() - 1;
            int delimiter = -1;
            int startMessage = -1;
            for (int i = endMessage; i >= 0; i--) {
                if (sb.charAt(i) == ';') {
                    delimiter = i;
                }
                if (sb.charAt(i) == '[') {
                    startMessage = i + 1;
                    break;
                }
            }
            if (startMessage == -1 | delimiter == -1) {
                throw new IOException("Invalid terminal Response. Terminal might be unsupported.");
            }

            result[0] = Integer.parseInt(sb.substring(startMessage, delimiter));
            result[1] = Integer.parseInt(sb.substring(delimiter + 1, endMessage));
            return result;

        }

    }

    public static class Erase {

        public static void inDisplay() {
            System.out.println(ESC_CTRL + 'J');
        }

        /**
         * Erase from cursor to end of screen
         */
        public static void toEndOfScreen() {
            System.out.println(ESC_CTRL + "0J");
        }

        /**
         * Erase from cursor to beginning of screen
         */
        public static void toBeginningOfScreen() {
            System.out.println(ESC_CTRL + "1J");
        }

        /**
         * Erase everything currently displayed screen
         */
        public static void onScreen() {
            System.out.println(ESC_CTRL + "2J");
        }

        /**
         * Erase everything that is offscreen
         */
        public static void scrollbackBuffer() {
            System.out.println(ESC_CTRL + "3J");
        }

        public static void inLine() {
            System.out.println(ESC_CTRL + 'K');
        }

        /**
         * Erase from cursor to end of line
         */
        public static void toEndOfLine() {
            System.out.println(ESC_CTRL + "0K");
        }

        /**
         * Erase from start of line to cursor
         */
        public static void toStartOfLine() {
            System.out.println(ESC_CTRL + "1K");
        }

        public static void line() {
            System.out.println(ESC_CTRL + "2K");
        }

        /**
         * Completely delete everything from the terminal
         */
        public static void all() {
            onScreen();
            scrollbackBuffer();
        }
    }

    public static class Color {
        private static final char FOREGROUND = '3';
        private static final char FOREGROUND_BRIGHT = '9';
        private static final char BACKGROUND = '4';
        private static final String BACKGROUND_BRIGHT = "10";
        private static final String RESET_FOREGROUND = ESC_CTRL + FOREGROUND + BasicColor.DEFAULT.getCode() + SGR;
        private static final String RESET_BACKGROUND = ESC_CTRL + BACKGROUND + BasicColor.DEFAULT.getCode() + SGR;

        /**
         * Return a string with an applied rgb color
         */
        public static String colorString(String string, int r, int g, int b) {
            return ESC_CTRL + "38;2;" + r + DELIMITER + g + DELIMITER + b + SGR + string + RESET_FOREGROUND;
        }

        /**
         * enables a rgb background color, to turn it off use disableForeground() or ANSI.reset()
         */
        public static void enableColor(int r, int g, int b) {
            System.out.print(ESC_CTRL + "38;2;" + r + DELIMITER + g + DELIMITER + b + SGR);
        }

        /**
         * Returns a string with a rgb background.
         */
        public static String colorBackground(String string, int r, int g, int b) {
            return ESC_CTRL + "48;2;" + r + DELIMITER + g + DELIMITER + b + SGR + string + RESET_BACKGROUND;
        }

        /**
         * Enables a rgb background color, to turn it off use disableForeground() or ANSI.reset()
         */
        public static void enableBackground(int r, int g, int b) {
            System.out.print(ESC_CTRL + "48;2;" + r + DELIMITER + g + DELIMITER + b + SGR);
        }

        /**
         * Returns a string with an applied color, from the basic color palette
         */
        public static String basicString(String string, BasicColor color) {
            return ESC_CTRL + FOREGROUND + color.getCode() + SGR + string + RESET_FOREGROUND;
        }

        /**
         * Returns a string with an applied color, from the basic bright color palette
         */
        public static String brightString(String string, BasicColor color) {
            return ESC_CTRL + FOREGROUND_BRIGHT + color.getCode() + SGR + string + RESET_FOREGROUND;
        }

        /**
         * Returns a string with an applied background color, from the basic color palette
         */
        public static String basicBackground(String string, BasicColor color) {
            return ESC_CTRL + BACKGROUND + color.getCode() + SGR + string + RESET_BACKGROUND;
        }

        /**
         * Returns a string with an applied background color, from the basic bright color palette
         */
        public static String brightBackground(String string, BasicColor color) {
            return ESC_CTRL + BACKGROUND_BRIGHT + color.getCode() + SGR + string + RESET_BACKGROUND;
        }

        /**
         * Enables on a color from the basic color palette, to turn it off use disableForeground() or ANSI.reset()
         */
        public static void enableString(BasicColor color) {
            System.out.print(ESC_CTRL + FOREGROUND + color.getCode() + SGR);
        }

        /**
         * Enables on a color from the basic bright color palette, to turn it off use disableForeground() or ANSI.reset()
         */
        public static void enableBrightString(BasicColor color) {
            System.out.print(ESC_CTRL + FOREGROUND_BRIGHT + color.getCode() + SGR);
        }

        /**
         * Enables on a text background color from the basic color palette, to turn it off use disableBackground() or ANSI.reset()
         */
        public static void enableBackground(BasicColor color) {
            System.out.print(ESC_CTRL + BACKGROUND + color.getCode() + SGR);
        }

        /**
         * Enables on a text background color from the basic bright color palette, to turn it off use disableBackground() or ANSI.reset()
         */
        public static void enableBrightBackground(BasicColor color) {
            System.out.print(ESC_CTRL + BACKGROUND_BRIGHT + color.getCode() + SGR);
        }

        /**
         * Turns off the applied text color attribute
         */
        public static void disableForeground() {
            System.out.print(RESET_FOREGROUND);
        }

        /**
         * Turns off the applied text color background attribute
         */
        public static void disableBackground() {
            System.out.print(RESET_BACKGROUND);
        }
    }

    public enum BasicColor {
        BLACK('0'),
        RED('1'),
        GREEN('2'),
        YELLOW('3'),
        BLUE('4'),
        MAGENTA('5'),
        CYAN('6'),
        WHITE('7'),
        DEFAULT('9');

        private final char code;

        BasicColor(char code) {
            this.code = code;
        }

        public char getCode() {
            return this.code;
        }
    }

}
