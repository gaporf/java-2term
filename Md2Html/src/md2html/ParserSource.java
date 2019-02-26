package md2html;

import java.io.IOException;

public abstract class ParserSource {
    public static char END = '\0';

    protected char c;

    public abstract char readChar() throws IOException;

    public boolean hasNextLine() {
        return getChar() != END;
    }

    public String getLine() throws IOException {
        StringBuilder ans = new StringBuilder();
        while (getChar() != '\n' && getChar() != END) {
            ans.append(c);
            nextChar();
        }
        nextChar();
        return ans.toString();
    }

    private char getChar() {
        return c;
    }

    private void nextChar() throws IOException {
        c = readChar();
    }
}
