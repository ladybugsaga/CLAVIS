package io.clavis.core.models;

import java.util.Objects;

/**
 * Unified protein model used across protein databases.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class Protein {

    private final String id;
    private final String source;
    private final String name;
    private final String geneName;
    private final String organism;
    private final String sequence;
    private final int sequenceLength;
    private final String function;

    private Protein(Builder builder) {
        this.id = builder.id;
        this.source = builder.source;
        this.name = builder.name;
        this.geneName = builder.geneName;
        this.organism = builder.organism;
        this.sequence = builder.sequence;
        this.sequenceLength = builder.sequenceLength;
        this.function = builder.function;
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

    public String getGeneName() {
        return geneName;
    }

    public String getOrganism() {
        return organism;
    }

    public String getSequence() {
        return sequence;
    }

    public int getSequenceLength() {
        return sequenceLength;
    }

    public String getFunction() {
        return function;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Protein p = (Protein) o;
        return Objects.equals(id, p.id) && Objects.equals(source, p.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, source);
    }

    @Override
    public String toString() {
        return String.format("Protein{id='%s', name='%s'}", id, name);
    }

    public static class Builder {
        private String id;
        private String source;
        private String name;
        private String geneName;
        private String organism;
        private String sequence;
        private int sequenceLength;
        private String function;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder geneName(String geneName) {
            this.geneName = geneName;
            return this;
        }

        public Builder organism(String organism) {
            this.organism = organism;
            return this;
        }

        public Builder sequence(String sequence) {
            this.sequence = sequence;
            return this;
        }

        public Builder sequenceLength(int len) {
            this.sequenceLength = len;
            return this;
        }

        public Builder function(String function) {
            this.function = function;
            return this;
        }

        public Protein build() {
            Objects.requireNonNull(id, "Protein ID is required");
            Objects.requireNonNull(name, "Protein name is required");
            return new Protein(this);
        }
    }
}
