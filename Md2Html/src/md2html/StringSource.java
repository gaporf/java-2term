package md2html;

public class StringSource extends ParserSource {
    private String source;
    private int pos = 0;

    public StringSource(final String source) {
        this.source = source + END;
        c = readChar();
    }

    @Override
    public char readChar() {
        return source.charAt(pos++);
    }
}
