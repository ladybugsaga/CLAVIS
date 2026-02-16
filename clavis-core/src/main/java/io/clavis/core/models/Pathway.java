package io.clavis.core.models;

import java.util.Objects;

/**
 * Unified pathway model used across pathway databases.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class Pathway {

    private final String id;
    private final String source;
    private final String name;
    private final String organism;
    private final String description;
    private final String url;

    public Pathway(String id, String source, String name, String organism,
            String description, String url) {
        this.id = Objects.requireNonNull(id);
        this.source = source;
        this.name = Objects.requireNonNull(name);
        this.organism = organism;
        this.description = description;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public String getOrganism() {
        return organism;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Pathway p = (Pathway) o;
        return Objects.equals(id, p.id) && Objects.equals(source, p.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, source);
    }

    @Override
    public String toString() {
        return String.format("Pathway{id='%s', name='%s'}", id, name);
    }
}
