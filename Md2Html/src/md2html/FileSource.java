package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileSource extends ParserSource {
    private final Reader reader;

    public FileSource(final String fileName) throws IOException {
        reader = new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8);
        c = readChar();
    }

    @Override
    public char readChar() throws IOException {
        final int read = reader.read();
        return read == -1 ? END : (char) read;
    }
}
