package io.clavis.pubmed.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * PubMed-specific paper model with MeSH terms.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class PubMedPaper {

    private final String pmid;
    private final String title;
    private final String abstractText;
    private final List<String> authors;
    private final String journal;
    private final String publicationDate;
    private final String doi;
    private final List<String> meshTerms;
    private final int citationCount;

    private PubMedPaper(Builder builder) {
        this.pmid = builder.pmid;
        this.title = builder.title;
        this.abstractText = builder.abstractText;
        this.authors = List.copyOf(builder.authors);
        this.journal = builder.journal;
        this.publicationDate = builder.publicationDate;
        this.doi = builder.doi;
        this.meshTerms = List.copyOf(builder.meshTerms);
        this.citationCount = builder.citationCount;
    }

    public String getPmid() { return pmid; }
    public String getTitle() { return title; }
    public String getAbstractText() { return abstractText; }
    public List<String> getAuthors() { return authors; }
    public String getJournal() { return journal; }
    public String getPublicationDate() { return publicationDate; }
    public String getDoi() { return doi; }
    public List<String> getMeshTerms() { return meshTerms; }
    public int getCitationCount() { return citationCount; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PubMedPaper that = (PubMedPaper) o;
        return Objects.equals(pmid, that.pmid);
    }

    @Override
    public int hashCode() { return Objects.hash(pmid); }

    @Override
    public String toString() {
        return String.format("PubMedPaper{pmid='%s', title='%s'}", pmid, title);
    }

    public static class Builder {
        private String pmid;
        private String title;
        private String abstractText;
        private List<String> authors = new ArrayList<>();
        private String journal;
        private String publicationDate;
        private String doi;
        private List<String> meshTerms = new ArrayList<>();
        private int citationCount;

        public Builder pmid(String pmid) { this.pmid = pmid; return this; }
        public Builder title(String title) { this.title = title; return this; }
        public Builder abstractText(String text) { this.abstractText = text; return this; }
        public Builder authors(List<String> authors) { this.authors = authors; return this; }
        public Builder journal(String journal) { this.journal = journal; return this; }
        public Builder publicationDate(String date) { this.publicationDate = date; return this; }
        public Builder doi(String doi) { this.doi = doi; return this; }
        public Builder meshTerms(List<String> terms) { this.meshTerms = terms; return this; }
        public Builder citationCount(int count) { this.citationCount = count; return this; }

        public PubMedPaper build() {
            Objects.requireNonNull(pmid, "PMID is required");
            Objects.requireNonNull(title, "Title is required");
            return new PubMedPaper(this);
        }
    }
}
