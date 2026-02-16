package io.clavis.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Unified drug model used across drug-related databases.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class Drug {

    private final String id;
    private final String source;
    private final String name;
    private final String genericName;
    private final String description;
    private final String drugType;
    private final List<String> categories;
    private final List<String> targets;
    private final String casNumber;
    private final String formula;

    private Drug(Builder builder) {
        this.id = builder.id;
        this.source = builder.source;
        this.name = builder.name;
        this.genericName = builder.genericName;
        this.description = builder.description;
        this.drugType = builder.drugType;
        this.categories = List.copyOf(builder.categories);
        this.targets = List.copyOf(builder.targets);
        this.casNumber = builder.casNumber;
        this.formula = builder.formula;
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

    public String getGenericName() {
        return genericName;
    }

    public String getDescription() {
        return description;
    }

    public String getDrugType() {
        return drugType;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<String> getTargets() {
        return targets;
    }

    public String getCasNumber() {
        return casNumber;
    }

    public String getFormula() {
        return formula;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Drug drug = (Drug) o;
        return Objects.equals(id, drug.id) && Objects.equals(source, drug.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, source);
    }

    @Override
    public String toString() {
        return String.format("Drug{id='%s', name='%s'}", id, name);
    }

    public static class Builder {
        private String id;
        private String source;
        private String name;
        private String genericName;
        private String description;
        private String drugType;
        private List<String> categories = new ArrayList<>();
        private List<String> targets = new ArrayList<>();
        private String casNumber;
        private String formula;

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

        public Builder genericName(String genericName) {
            this.genericName = genericName;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder drugType(String drugType) {
            this.drugType = drugType;
            return this;
        }

        public Builder categories(List<String> categories) {
            this.categories = categories;
            return this;
        }

        public Builder targets(List<String> targets) {
            this.targets = targets;
            return this;
        }

        public Builder casNumber(String casNumber) {
            this.casNumber = casNumber;
            return this;
        }

        public Builder formula(String formula) {
            this.formula = formula;
            return this;
        }

        public Drug build() {
            Objects.requireNonNull(id, "Drug ID is required");
            Objects.requireNonNull(name, "Drug name is required");
            return new Drug(this);
        }
    }
}
