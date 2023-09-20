package dev.nathanrsxtn.data.csv;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CSV<R> {
    private final Map<Class<?>, MethodHandle> parserMethodHandlesByType;
    private final Map<String, Setter<R>> settersByColumn;
    private final Supplier<R> generator;

    /**
     * Creates a new {@code CSV} instance for a CSV file.
     *
     * @param generator a parameterless supplier which produces a new object with fields for the
     *        matching columns in the CSV file. The first character of the column names from the header
     *        in the CSV file will be made lowercase and invalid characters will be removed when getting
     *        data.
     * @see #read(Path)
     */
    public CSV(final Supplier<R> generator) {
        final Class<?> recordClass = generator.get().getClass();
        this.generator = generator;
        this.parserMethodHandlesByType = new HashMap<>();
        this.settersByColumn = Arrays.stream(recordClass.getFields()).collect(Collectors.toUnmodifiableMap(Field::getName, field -> this.getParsingSetter(field.getType(), getVarHandle(field))));
    }

    /**
     * Reads and parses the contents of the CSV file, and returns a stream of elements of the previously
     * given class. Cells are parsed using the field's {@code valueOf(String)} function.
     *
     * @param reader the reader for a CSV file
     * @return the parsed data from the CSV file
     * @throws IOException IOException if an I/O error occurs opening the file
     */
    @SuppressWarnings("unchecked")
    public Stream<R> stream(final BufferedReader reader) throws IOException {
        final String[] splitHeader = splitString(headerSanitizer(reader.readLine()), ',');
        final Setter<R>[] fieldSetters = new Setter[splitHeader.length];
        for (int i = 0; i < splitHeader.length; i++) {
            final String fieldName = nameProcessor(splitHeader[i]);
            fieldSetters[i] = this.settersByColumn.get(fieldName);
        }
        return reader.lines().map(line -> deserializeRecordString(line, fieldSetters, generator.get())).onClose(asUncheckedRunnable(reader));
    }

    private Setter<R> getParsingSetter(final Class<?> type, final VarHandle varHandle) {
        if (type == boolean.class) return (final R obj, final String value) -> varHandle.set(obj, Boolean.parseBoolean(value));
        if (type == byte.class) return (final R obj, final String value) -> varHandle.set(obj, Byte.parseByte(value));
        if (type == short.class) return (final R obj, final String value) -> varHandle.set(obj, Short.parseShort(value));
        if (type == char.class) return (final R obj, final String value) -> varHandle.set(obj, value.charAt(0));
        if (type == int.class) return (final R obj, final String value) -> varHandle.set(obj, Integer.parseInt(value));
        if (type == long.class) return (final R obj, final String value) -> varHandle.set(obj, Long.parseLong(value));
        if (type == float.class) return (final R obj, final String value) -> varHandle.set(obj, Float.parseFloat(value));
        if (type == double.class) return (final R obj, final String value) -> varHandle.set(obj, Double.parseDouble(value));
        if (type == String.class || type.isAssignableFrom(String.class)) return varHandle::set;
        final MethodHandle parser = this.parserMethodHandlesByType.computeIfAbsent(type, CSV::getObjectParser);
        return parser == null ? Setter.empty() : (final R obj, final String value) -> varHandle.set(obj, parser.invoke(value));
    }

    @FunctionalInterface
    static interface Setter<R> {
        static <R> Setter<R> empty() {
            return (final R obj, final String value) -> {
            };
        }

        void parseAndSet(final R obj, final String value) throws Throwable;

        default void tryParseAndSet(final R obj, final String value) {
            try {
                this.parseAndSet(obj, value);
            } catch (final Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    static String headerSanitizer(final String header) {
        final StringBuilder result = new StringBuilder(header.length());
        for (int i = 0; i < header.length(); i++) {
            final char character = header.charAt(i);
            if (character == ',' || Character.isJavaIdentifierPart(character)) result.append(character);
        }
        return result.toString();
    }

    static String nameProcessor(final String name) {
        for (int i = 0; i < name.length(); i++) {
            final char character = Character.toLowerCase(name.charAt(i));
            if (Character.isJavaIdentifierStart(character)) return character + name.substring(i + 1);
        }
        return name;
    }

    static MethodHandle getObjectParser(final Class<?> type) {
        try {
            return MethodHandles.publicLookup().findStatic(type, "valueOf", MethodType.methodType(type, String.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            return null;
        }
    }

    static VarHandle getVarHandle(final Field field) {
        try {
            return MethodHandles.publicLookup().unreflectVarHandle(field);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see java.nio.file.Files#asUncheckedRunnable(Closeable)
     */
    static Runnable asUncheckedRunnable(final Closeable c) {
        return () -> {
            try {
                c.close();
            } catch (final IOException e) {
                throw new UncheckedIOException(e);
            }
        };
    }

    /**
     * Splits this string around instances of the given character.
     * 
     * Unlike {@link String#split(String)}, this method does not strip trailing empty substrings.
     * 
     * @param string The string to be split.
     * @param ch The delimiting character.
     * @return The method is returning an array of strings.
     */
    static String[] splitString(final String string, final char ch) {
        int off = 0;
        int next;
        final ArrayList<String> list = new ArrayList<>(string.length() / 2 + 1);
        while ((next = string.indexOf(ch, off)) != -1) {
            list.add(string.substring(off, next));
            off = next + 1;
        }
        // If no match was found, return this
        if (off == 0) return new String[] { string };

        // Add remaining segment
        final String remaining = string.substring(off);
        if (!remaining.isEmpty()) list.add(remaining);

        // Construct result
        final int resultSize = list.size();
        final String[] result = new String[resultSize];
        return list.toArray(result);
    }

    /**
     * The `deserializeRecordString` function takes a string representation of a record, an array of
     * setters, and an object, and parses the string to set the corresponding fields in the object using
     * the setters.
     * 
     * @param recordString A string representing a comma-separated record entry.
     * @param fieldParseSetters An array of Setter objects that define how each field in the record
     *        should be parsed and set in the object.
     * @param recordObject The object that will be populated with the parsed values.
     * @return The passed object.
     */
    static <R> R deserializeRecordString(final String recordString, final Setter<R>[] fieldParseSetters, final R recordObject) {
        final int recordStringLength = recordString.length();
        int fieldBeginIndex = 0, tryFieldEndFromIndex = 0, i = 0;
        while (tryFieldEndFromIndex < recordStringLength && i < fieldParseSetters.length) {
            final int tryFieldEndIndex = recordString.indexOf(',', tryFieldEndFromIndex);
            String field = recordString.substring(fieldBeginIndex, tryFieldEndIndex == -1 ? recordStringLength : tryFieldEndIndex).strip();
            if (!field.isEmpty() && (tryFieldEndFromIndex != fieldBeginIndex || field.charAt(0) == '"')) {
                if (countTrailing(field, '"') % 2 == 0) {
                    tryFieldEndFromIndex = tryFieldEndIndex + 1;
                    continue;
                } else field = unescapeQuotedField(field);
            }
            final Setter<R> setter = fieldParseSetters[i];
            if (setter != null && !field.isEmpty()) setter.tryParseAndSet(recordObject, field);
            tryFieldEndFromIndex = fieldBeginIndex = tryFieldEndIndex + 1;
            i++;
        }
        return recordObject;
    }

    static int countTrailing(final String str, final char c) {
        final int l = str.length();
        int count = 0;
        while (str.charAt(l - count - 1) == c && count < l)
            count++;
        return count;
    }

    static String unescapeQuotedField(final String string) {
        final int length = string.length();
        final char[] characters = new char[length - 2];
        boolean quoteOpen = false;
        int count = 0;
        for (int i = 1; i < length - 1; i++) {
            final char c = string.charAt(i);
            final boolean quote = c == '\"';
            if (!(quoteOpen && quote)) {
                characters[count] = c;
                count++;
            }
            quoteOpen = quote && !quoteOpen;
        }
        return new String(characters, 0, count);
    }
}
