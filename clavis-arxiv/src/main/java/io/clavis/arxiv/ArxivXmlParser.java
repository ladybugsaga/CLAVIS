package io.clavis.arxiv;

import io.clavis.core.models.Author;
import io.clavis.core.models.Paper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Lightweight parser for arXiv Atom XML responses using regex.
 */
public class ArxivXmlParser {

    private static final Pattern ENTRY_PATTERN = Pattern.compile(
            "<entry>(.*?)</entry>", Pattern.DOTALL);
    private static final Pattern TITLE_PATTERN = Pattern.compile(
            "<title[^>]*>(.*?)</title>", Pattern.DOTALL);
    private static final Pattern SUMMARY_PATTERN = Pattern.compile(
            "<summary[^>]*>(.*?)</summary>", Pattern.DOTALL);
    private static final Pattern AUTHOR_NAME_PATTERN = Pattern.compile(
            "<author>\\s*<name>(.*?)</name>", Pattern.DOTALL);
    private static final Pattern PUBLISHED_PATTERN = Pattern.compile(
            "<published>(.*?)</published>");
    private static final Pattern ID_PATTERN = Pattern.compile(
            "<id>(.*?)</id>");
    private static final Pattern DOI_PATTERN = Pattern.compile(
            "<arxiv:doi[^>]*>(.*?)</arxiv:doi>");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile(
            "<category[^>]*term=\"([^\"]+)\"");
    private static final Pattern JOURNAL_REF_PATTERN = Pattern.compile(
            "<arxiv:journal_ref[^>]*>(.*?)</arxiv:journal_ref>", Pattern.DOTALL);
    private static final Pattern TOTAL_RESULTS_PATTERN = Pattern.compile(
            "<opensearch:totalResults[^>]*>(\\d+)</opensearch:totalResults>");

    /**
     * Parses the Atom XML feed into a list of papers.
     */
    public List<Paper> parseSearchResults(String xml) {
        List<Paper> papers = new ArrayList<>();
        Matcher entryMatcher = ENTRY_PATTERN.matcher(xml);
        while (entryMatcher.find()) {
            papers.add(parseEntry(entryMatcher.group(1)));
        }
        return papers;
    }

    /**
     * Extracts the total result count from the feed.
     */
    public int getTotalResults(String xml) {
        Matcher m = TOTAL_RESULTS_PATTERN.matcher(xml);
        return m.find() ? Integer.parseInt(m.group(1)) : 0;
    }

    private Paper parseEntry(String entry) {
        String rawId = extractFirst(ID_PATTERN, entry, "");
        // Extract arXiv ID from URL like http://arxiv.org/abs/2301.12345v1
        String arxivId = rawId.replaceAll(".*abs/", "").replaceAll("v\\d+$", "");

        String title = extractFirst(TITLE_PATTERN, entry, "").replaceAll("\\s+", " ").trim();
        String summary = extractFirst(SUMMARY_PATTERN, entry, "").replaceAll("\\s+", " ").trim();
        String published = extractFirst(PUBLISHED_PATTERN, entry, "");
        String doi = extractFirst(DOI_PATTERN, entry, "");
        String journalRef = extractFirst(JOURNAL_REF_PATTERN, entry, "").trim();

        // Parse authors
        List<Author> authors = new ArrayList<>();
        Matcher authorMatcher = AUTHOR_NAME_PATTERN.matcher(entry);
        while (authorMatcher.find()) {
            authors.add(new Author(authorMatcher.group(1).trim()));
        }

        // Parse categories
        List<String> categories = new ArrayList<>();
        Matcher catMatcher = CATEGORY_PATTERN.matcher(entry);
        while (catMatcher.find()) {
            categories.add(catMatcher.group(1));
        }

        Paper.Builder builder = new Paper.Builder()
                .id(arxivId)
                .source("arXiv")
                .title(title)
                .abstractText(summary)
                .authors(authors)
                .publicationDate(published.length() >= 10 ? published.substring(0, 10) : published)
                .doi(doi)
                .journal(journalRef)
                .url("https://arxiv.org/abs/" + arxivId)
                .keywords(categories);

        return builder.build();
    }

    private String extractFirst(Pattern pattern, String text, String defaultValue) {
        Matcher m = pattern.matcher(text);
        return m.find() ? m.group(1) : defaultValue;
    }
}
