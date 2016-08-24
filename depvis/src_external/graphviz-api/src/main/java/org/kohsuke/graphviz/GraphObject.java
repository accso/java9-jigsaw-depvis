package org.kohsuke.graphviz;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Base class of graph related objects.
 *
 * <p>
 * This class maintains a list of attributes that decorates a graph object.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class GraphObject<T extends GraphObject<T>> {
    private Style style;
    private final LinkedHashMap<String,String> attributes
            = new LinkedHashMap<String,String>();
    /*package*/ String id;

    public String getId() {
		return id;
	}
    
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GraphObject other = (GraphObject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
    
    /**
     * Explicitly set the ID of this object. 
     */
    public final T id(String id) {
        this.id = id.replace(".", "_").replace(" ", "_");
        return self();
    }

    /**
     * Sets the style of this object to the given one.
     */
    public final T style(Style s) {
        this.style = s;
        return self();
    }

    /**
     * Gets the style of this object.
     */
    public Style style() {
        return style;
    }

    /**
     * Adds an attribute.
     */
    public final T attr(String name, String value) {
        attributes.put(name,value);
        return self();
    }

    /**
     * Adds an attribute.
     */
    public final <V> T attr(Attribute<V> attribute, V value) {
        return attr(attribute.name,attribute.toString(value));
    }

    /**
     * Gets the effective value of the attribute.
     */
    public final String attr(String name) {
        if(style!=null) {
            String v = style.attr(name);
            if(v!=null)     return v;
        }
        return attributes.get(name);
    }

    /**
     * Gets the effective value of the attribute.
     */
    public final <V> V attr(Attribute<V> attribute) {
        return attribute.fromString(attr(attribute.name));
    }

    /**
     * Returns 'this' in a type-safe fashion.
     */
    abstract T self();

    /**
     * Gets the snapshot of all effective attributes, after
     * inheriting all values from the style.
     */
    public Map<String,String> getEffectiveAttributes() {
        if(style==null)
            return new LinkedHashMap<String, String>(attributes);

        Map<String,String> r = style.getEffectiveAttributes();
        r.putAll(attributes);
        return r;
    }

    /**
     * Writes the list of attributes in the dot '[....]' format.
     */
    final void writeAttributes(Printer w) {
        Map<String,String> m = getEffectiveAttributes();
        if(!m.isEmpty()) {
            w.print('[');
            boolean hasHtmlLabel = m.containsKey("html");

            boolean first = true;
            for (Entry<String, String> e : m.entrySet()) {
                if(hasHtmlLabel && e.getKey().equals("label"))
                    continue;

                if(!first)   w.print(',');
                else        first=false;
                if(hasHtmlLabel && e.getKey().equals("html")) {
                    w.print("label=<");
                    w.print(e.getValue());
                    w.print('>');
                } else {
                    w.print(e.getKey());
                    w.print('=');
                    w.print(escape(e.getValue()));
                }
            }
            w.print(']');
        }
    }

    protected static String escape(String value) {
        StringBuilder buf = new StringBuilder("\"");
        for(int i=0; i<value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
            case '"':
                buf.append("\\\"");
                break;
            case '\n':
                buf.append("\\n");
                break;
            default:
                buf.append(ch);
            }
        }
        buf.append('"');
        return buf.toString();
    }
}
