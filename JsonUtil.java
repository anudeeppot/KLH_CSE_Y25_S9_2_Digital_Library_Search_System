/**
 * JsonUtil.java — Minimal, safe JSON builder using Java standard library only.
 *
 * Correctly escapes double-quotes, backslashes, newlines, carriage-returns,
 * tabs, and all other control characters before inserting them into JSON.
 *
 * Usage pattern:
 *   JsonUtil.obj()
 *       .put("key", "value")
 *       .put("num", 42)
 *       .build()
 *   => {"key":"value","num":42}
 */
public final class JsonUtil {

    private JsonUtil() {}

    // ------------------------------------------------------------------
    // String escaping
    // ------------------------------------------------------------------

    public static String escape(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder(s.length() + 4);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /** Wraps an escaped string in double-quotes for JSON. */
    public static String str(String s) {
        if (s == null) return "null";
        return "\"" + escape(s) + "\"";
    }

    // ------------------------------------------------------------------
    // Simple fluent object builder
    // ------------------------------------------------------------------

    public static ObjectBuilder obj() { return new ObjectBuilder(); }

    public static final class ObjectBuilder {
        private final StringBuilder sb = new StringBuilder("{");
        private boolean first = true;

        private ObjectBuilder comma() {
            if (!first) sb.append(',');
            first = false;
            return this;
        }

        public ObjectBuilder put(String key, String value) {
            comma(); sb.append(str(key)).append(':').append(str(value));
            return this;
        }

        public ObjectBuilder put(String key, int value) {
            comma(); sb.append(str(key)).append(':').append(value);
            return this;
        }

        public ObjectBuilder put(String key, long value) {
            comma(); sb.append(str(key)).append(':').append(value);
            return this;
        }

        public ObjectBuilder put(String key, boolean value) {
            comma(); sb.append(str(key)).append(':').append(value);
            return this;
        }

        /** Insert a pre-built JSON fragment (array or object) verbatim. */
        public ObjectBuilder putRaw(String key, String jsonFragment) {
            comma(); sb.append(str(key)).append(':').append(jsonFragment);
            return this;
        }

        public String build() { return sb.append('}').toString(); }
    }

    // ------------------------------------------------------------------
    // Simple array builder
    // ------------------------------------------------------------------

    public static ArrayBuilder arr() { return new ArrayBuilder(); }

    public static final class ArrayBuilder {
        private final StringBuilder sb = new StringBuilder("[");
        private boolean first = true;

        private ArrayBuilder comma() {
            if (!first) sb.append(',');
            first = false;
            return this;
        }

        public ArrayBuilder add(String value) {
            comma(); sb.append(str(value));
            return this;
        }

        public ArrayBuilder add(int value) {
            comma(); sb.append(value);
            return this;
        }

        /** Add a pre-built JSON fragment (array or object) verbatim. */
        public ArrayBuilder addRaw(String jsonFragment) {
            comma(); sb.append(jsonFragment);
            return this;
        }

        public String build() { return sb.append(']').toString(); }
    }

    // ------------------------------------------------------------------
    // Convenience helpers
    // ------------------------------------------------------------------

    /** Build {"error":"message"} with a safe 400-style error body. */
    public static String error(String message) {
        return obj().put("error", message).build();
    }
}
