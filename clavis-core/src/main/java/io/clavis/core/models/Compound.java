package io.clavis.core.models;

import java.util.Objects;

/**
 * Unified compound model for chemical databases.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class Compound {

    private final String id;
    private final String source;
    private final String name;
    private final String formula;
    private final double molecularWeight;
    private final String smiles;
    private final String inchiKey;

    public Compound(String id, String source, String name, String formula,
            double molecularWeight, String smiles, String inchiKey) {
        this.id = Objects.requireNonNull(id);
        this.source = source;
        this.name = Objects.requireNonNull(name);
        this.formula = formula;
        this.molecularWeight = molecularWeight;
        this.smiles = smiles;
        this.inchiKey = inchiKey;
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

    public String getFormula() {
        return formula;
    }

    public double getMolecularWeight() {
        return molecularWeight;
    }

    public String getSmiles() {
        return smiles;
    }

    public String getInchiKey() {
        return inchiKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Compound c = (Compound) o;
        return Objects.equals(id, c.id) && Objects.equals(source, c.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, source);
    }

    @Override
    public String toString() {
        return String.format("Compound{id='%s', name='%s'}", id, name);
    }
}
