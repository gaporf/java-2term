package md2html;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Md2Html {
    private final ParserSource source;

    private int getLastIndex(StringBuilder stringBuilder) {
        return stringBuilder.length() - 1;
    }

    public Md2Html(final ParserSource source) {
        this.source = source;
    }

    public Md2Html(String fileNameInput) throws IOException {
        this.source = new FileSource(fileNameInput);
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Illegal num of arguments, it's necessary to enter 2 arguments");
        }
        try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(new File(args[1])), StandardCharsets.UTF_8)) {
            outputStreamWriter.write(new Md2Html(args[0]).parseText());
        } catch (IOException e) {
            System.err.println("Can't get access to file");
        }
    }

    public String parseText() throws IOException {
        boolean extra = false;
        StringBuilder ans = new StringBuilder(),
                currentParagraph = new StringBuilder();
        while (source.hasNextLine()) {
            String line = source.getLine();
            if (isEmpty(line)) {
                if (!extra && currentParagraph.length() > 0) {
                    currentParagraph.deleteCharAt(getLastIndex(currentParagraph));
                    ans.append(parseParagraph(currentParagraph.toString())).append("\n");
                    currentParagraph = new StringBuilder();
                }
                extra = true;
            } else {
                currentParagraph.append(line).append("\n");
                extra = false;
            }
        }
        if (!extra) {
            currentParagraph.deleteCharAt(getLastIndex(currentParagraph));
            ans.append(parseParagraph(currentParagraph.toString())).append("\n");
        }

        return ans.toString();
    }

    private String parsedLine;
    private int pos;

    private String parseParagraph(String paragraph) {
        this.parsedLine = paragraph;
        pos = 0;
        if (match('#')) {
            int level = 0;
            while (match('#')) {
                level++;
                nextChar();
            }
            if (Character.isWhitespace(getChar())) {
                nextChar();
                return "<h" + level + ">" + parseUntil('\0') + "</h" + level + ">";
            } else {
                StringBuilder begin = new StringBuilder();
                while (level-- > 0) {
                    begin.append('#');
                }
                return "<p>" + begin + parseUntil('\0') + "</p>";
            }
        } else {
            return "<p>" + parseUntil('\0') + "</p>";
        }
    }

    private StringBuilder parseUntil(char breakpoint) {
        StringBuilder parsedString = new StringBuilder();
        while (hasNext()) {
            if (match(breakpoint)) {
                break;
            } else if (match('<')) {
                parsedString.append("&lt;");
            } else if (match('>')) {
                parsedString.append("&gt;");
            } else if (match('&')) {
                parsedString.append("&amp;");
            } else if (match('\\')) {
                nextChar();
                parsedString.append(getChar());
            } else if (match('`')) {
                nextChar();
                StringBuilder middle = parseUntil('`');
                if (match('`')) {
                    parsedString.append("<code>").append(middle).append("</code>");
                } else {
                    parsedString.append('`').append(middle);
                }
            } else if (match('-')) {
                nextChar();
                if (match('-')) {
                    nextChar();
                    parsedString.append("<s>").append(parseUntil('-')).append("</s>");
                    nextChar();
                } else {
                    parsedString.append("-");
                    continue;
                }
            } else if (match('*')) {
                strong(parsedString, '*');
            } else if (match('_')) {
                strong(parsedString, '_');
            } else if (match('!')) {
                nextChar();
                if (match('[')) {
                    nextChar();
                    String image = parseWithout(']');
                    nextChar();
                    nextChar();
                    String link = parseWithout(')');
                    parsedString.append("<img alt=\'").append(image).append("\' src=\'").append(link).append("\'>");
                } else {
                    parsedString.append("!");
                    continue;
                }
            } else {
                parsedString.append(getChar());
            }
            nextChar();
        }
        return parsedString;
    }

    private void strong(StringBuilder parsedString, char breakpoint) {
        nextChar();
        if (match(breakpoint)) {
            nextChar();
            parsedString.append("<strong>").append(parseUntil(breakpoint)).append("</strong>");
            nextChar();
        } else {
            StringBuilder middle = parseUntil(breakpoint);
            if (match(breakpoint)) {
                parsedString.append("<em>").append(middle).append("</em>");
            } else {
                parsedString.append(breakpoint).append(middle);
            }
        }
    }

    private String parseWithout(char breakpoint) {
        StringBuilder cur = new StringBuilder();
        while (hasNext() && !match(breakpoint)) {
            cur.append(getChar());
            nextChar();
        }
        return cur.toString();
    }

    private boolean match(char c) {
        return hasNext() && parsedLine.charAt(pos) == c;
    }

    private boolean hasNext() {
        return parsedLine.length() > pos;
    }

    private char getChar() {
        return parsedLine.charAt(pos);
    }

    private void nextChar() {
        pos++;
    }

    private boolean isEmpty(String line) {
        return line.length() == 0;
    }
}
