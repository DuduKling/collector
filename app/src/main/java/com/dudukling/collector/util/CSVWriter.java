package com.dudukling.collector.util;

import java.io.PrintWriter;
import java.io.Writer;

public class CSVWriter {

    private PrintWriter pw;

    private char separator;

    private char quotechar;

    private char escapechar;

    private String lineEnd;

    /** The character used for escaping quotes. */
    private static final char DEFAULT_ESCAPE_CHARACTER = '"';

    /** The default separator to use if none is supplied to the constructor. */
    private static final char DEFAULT_SEPARATOR = ',';

    /**
     * The default quote character to use if none is supplied to the
     * constructor.
     */
    private static final char DEFAULT_QUOTE_CHARACTER = '"';

    /** The quote constant to use when you wish to suppress all quoting. */
    private static final char NO_QUOTE_CHARACTER = '\u0000';

    /** The escape constant to use when you wish to suppress all escaping. */
    private static final char NO_ESCAPE_CHARACTER = '\u0000';

    /** Default line terminator uses platform encoding. */
    private static final String DEFAULT_LINE_END = "\n";

    /**
     * Constructs CSVWriter using a comma for the separator.
     *
     * @param writer
     *            the writer to an underlying CSV source.
     */
    public CSVWriter(Writer writer) {
        this(writer, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER,
                DEFAULT_ESCAPE_CHARACTER, DEFAULT_LINE_END);
    }

    /**
     * Constructs CSVWriter with supplied separator, quote char, escape char and line ending.
     *
     * @param writer
     *            the writer to an underlying CSV source.
     * @param separator
     *            the delimiter to use for separating entries
     * @param quotechar
     *            the character to use for quoted elements
     * @param escapechar
     *            the character to use for escaping quotechars or escapechars
     * @param lineEnd
     * 			  the line feed terminator to use
     */
    private CSVWriter(Writer writer, char separator, char quotechar, char escapechar, String lineEnd) {
        this.pw = new PrintWriter(writer);
        this.separator = separator;
        this.quotechar = quotechar;
        this.escapechar = escapechar;
        this.lineEnd = lineEnd;
    }

    public void writeNext(String[] nextLine) {

        if (nextLine == null)
            return;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < nextLine.length; i++) {

            if (i != 0) {
                sb.append(separator);
            }

            String nextElement = nextLine[i];
            if (nextElement == null)
                continue;
            if (quotechar !=  NO_QUOTE_CHARACTER)
                sb.append(quotechar);
            for (int j = 0; j < nextElement.length(); j++) {
                char nextChar = nextElement.charAt(j);
                if (escapechar != NO_ESCAPE_CHARACTER && nextChar == quotechar) {
                    sb.append(escapechar).append(nextChar);
                } else if (escapechar != NO_ESCAPE_CHARACTER && nextChar == escapechar) {
                    sb.append(escapechar).append(nextChar);
                } else {
                    sb.append(nextChar);
                }
            }
            if (quotechar != NO_QUOTE_CHARACTER)
                sb.append(quotechar);
        }

        sb.append(lineEnd);
        pw.write(sb.toString());

    }

    public void close(){
        pw.flush();
        pw.close();
    }
}
