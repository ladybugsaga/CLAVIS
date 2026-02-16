package io.clavis.core.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Unified paper model used across all literature databases.
 *
 * <p>
 * Provides a common representation for papers from PubMed,
 * Europe PMC, Semantic Scholar, arXiv, etc.
 * </p>
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class Paper {

    private final String id;
    private final String source;
    private final String title;
    private final String abstractText;
    private final List<Author> authors;
    private final String journal;
    private final String publicationDate;
    private final String doi;
    private final List<String> keywords;
    private final int citationCount;
    private final String url;

    private Paper(Builder builder) {
        this.id = builder.id;
        this.source = builder.source;
        this.title = builder.title;
        this.abstractText = builder.abstractText;
        this.authors = List.copyOf(builder.authors);
        this.journal = builder.journal;
        this.publicationDate = builder.publicationDate;
        this.doi = builder.doi;
        this.keywords = List.copyOf(builder.keywords);
        this.citationCount = builder.citationCount;
        this.url = builder.url;
    }

    public String getId() {
        return id;
    }

    public String getSource() {
        return source;
    }

    public String getTitle() {
        return title;
    }

    public String getAbstractText() {
        return abstractText;
    }

    public List<Author> getAuthors() {
        return authors;
    }

    public String getJournal() {
        return journal;
    }

    public String getPublicationDate() {
        return publicationDate;
    }

    public String getDoi() {
        return doi;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public int getCitationCount() {
        return citationCount;
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
        Paper paper = (Paper) o;
        return Objects.equals(id, paper.id) && Objects.equals(source, paper.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, source);
    }

    @Override
    public String toString() {
        return String.format("Paper{id='%s', source='%s', title='%s'}", id, source, title);
    }

    public static class Builder {
        private String id;
        private String source;
        private String title;
        private String abstractText;
        private List<Author> authors = new ArrayList<>();
        private String journal;
        private String publicationDate;
        private String doi;
        private List<String> keywords = new ArrayList<>();
        private int citationCount;
        private String url;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder abstractText(String abstractText) {
            this.abstractText = abstractText;
            return this;
        }

        public Builder authors(List<Author> authors) {
            this.authors = authors;
            return this;
        }

        public Builder addAuthor(Author author) {
            this.authors.add(author);
            return this;
        }

        public Builder journal(String journal) {
            this.journal = journal;
            return this;
        }

        public Builder publicationDate(String publicationDate) {
            this.publicationDate = publicationDate;
            return this;
        }

        public Builder doi(String doi) {
            this.doi = doi;
            return this;
        }

        public Builder keywords(List<String> keywords) {
            this.keywords = keywords;
            return this;
        }

        public Builder citationCount(int citationCount) {
            this.citationCount = citationCount;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Paper build() {
            Objects.requireNonNull(id, "Paper ID is required");
            Objects.requireNonNull(title, "Paper title is required");
            return new Paper(this);
        }
    }
}
