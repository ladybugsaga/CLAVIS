package io.clavis.core.models;

import java.util.Objects;

/**
 * Represents a paper author.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class Author {

    private final String name;
    private final String affiliation;
    private final String orcid;

    public Author(String name, String affiliation, String orcid) {
        this.name = Objects.requireNonNull(name, "Author name is required");
        this.affiliation = affiliation;
        this.orcid = orcid;
    }

    public Author(String name) {
        this(name, null, null);
    }

    public String getName() {
        return name;
    }

    public String getAffiliation() {
        return affiliation;
    }

    public String getOrcid() {
        return orcid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Author author = (Author) o;
        return Objects.equals(name, author.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
