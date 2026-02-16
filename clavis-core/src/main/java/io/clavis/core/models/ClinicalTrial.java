package io.clavis.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Unified clinical trial model.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class ClinicalTrial {

    private final String id;
    private final String title;
    private final String status;
    private final String phase;
    private final String studyType;
    private final String condition;
    private final List<String> interventions;
    private final String sponsor;
    private final String startDate;
    private final String completionDate;
    private final int enrollment;
    private final String url;

    private ClinicalTrial(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.status = builder.status;
        this.phase = builder.phase;
        this.studyType = builder.studyType;
        this.condition = builder.condition;
        this.interventions = List.copyOf(builder.interventions);
        this.sponsor = builder.sponsor;
        this.startDate = builder.startDate;
        this.completionDate = builder.completionDate;
        this.enrollment = builder.enrollment;
        this.url = builder.url;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getStatus() {
        return status;
    }

    public String getPhase() {
        return phase;
    }

    public String getStudyType() {
        return studyType;
    }

    public String getCondition() {
        return condition;
    }

    public List<String> getInterventions() {
        return interventions;
    }

    public String getSponsor() {
        return sponsor;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getCompletionDate() {
        return completionDate;
    }

    public int getEnrollment() {
        return enrollment;
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
        ClinicalTrial ct = (ClinicalTrial) o;
        return Objects.equals(id, ct.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("ClinicalTrial{id='%s', title='%s'}", id, title);
    }

    public static class Builder {
        private String id;
        private String title;
        private String status;
        private String phase;
        private String studyType;
        private String condition;
        private List<String> interventions = new ArrayList<>();
        private String sponsor;
        private String startDate;
        private String completionDate;
        private int enrollment;
        private String url;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder status(String status) {
            this.status = status;
            return this;
        }

        public Builder phase(String phase) {
            this.phase = phase;
            return this;
        }

        public Builder studyType(String studyType) {
            this.studyType = studyType;
            return this;
        }

        public Builder condition(String condition) {
            this.condition = condition;
            return this;
        }

        public Builder interventions(List<String> interventions) {
            this.interventions = interventions;
            return this;
        }

        public Builder sponsor(String sponsor) {
            this.sponsor = sponsor;
            return this;
        }

        public Builder startDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder completionDate(String completionDate) {
            this.completionDate = completionDate;
            return this;
        }

        public Builder enrollment(int enrollment) {
            this.enrollment = enrollment;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public ClinicalTrial build() {
            Objects.requireNonNull(id, "Trial ID is required");
            Objects.requireNonNull(title, "Trial title is required");
            return new ClinicalTrial(this);
        }
    }
}
