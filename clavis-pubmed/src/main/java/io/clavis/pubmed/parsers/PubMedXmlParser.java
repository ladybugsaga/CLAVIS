package io.clavis.pubmed.parsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.clavis.core.models.Author;
import io.clavis.core.models.Paper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for PubMed E-utilities XML and JSON responses.
 *
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class PubMedXmlParser {

    private static final Pattern PMID_PATTERN = Pattern.compile("<PMID[^>]*>(\\d+)</PMID>");
    private static final Pattern TITLE_PATTERN = Pattern.compile("<ArticleTitle>(.*?)</ArticleTitle>", Pattern.DOTALL);
    private static final Pattern ABSTRACT_PATTERN = Pattern.compile("<AbstractText[^>]*>(.*?)</AbstractText>", Pattern.DOTALL);
    private static final Pattern AUTHOR_PATTERN = Pattern.compile(
            "<Author[^>]*>.*?<LastName>(.*?)</LastName>.*?<ForeName>(.*?)</ForeName>.*?</Author>", Pattern.DOTALL);
    private static final Pattern JOURNAL_PATTERN = Pattern.compile("<Title>(.*?)</Title>", Pattern.DOTALL);
    private static final Pattern DOI_PATTERN = Pattern.compile(
            "<ArticleId IdType=\"doi\">(.*?)</ArticleId>", Pattern.DOTALL);
    private static final Pattern YEAR_PATTERN = Pattern.compile(
            "<PubDate>.*?<Year>(\\d{4})</Year>.*?</PubDate>", Pattern.DOTALL);
    private static final Pattern ARTICLE_PATTERN = Pattern.compile(
            "<PubmedArticle>(.*?)</PubmedArticle>", Pattern.DOTALL);

    /**
     * Parses PMIDs from an E-search JSON response.
     *
     * @param json the JSON response from esearch
     * @return list of PMIDs
     */
    public static List<String> parsePmidsFromJson(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonObject result = root.getAsJsonObject("esearchresult");
            if (result == null || !result.has("idlist")) {
                return Collections.emptyList();
            }
            JsonArray idList = result.getAsJsonArray("idlist");
            List<String> pmids = new ArrayList<>();
            for (JsonElement id : idList) {
                pmids.add(id.getAsString());
            }
            return pmids;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Parses related PMIDs from an E-link JSON response.
     *
     * @param json the JSON response from elink
     * @return list of related PMIDs
     */
    public static List<String> parseRelatedPmidsFromJson(String json) {
        try {
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();
            JsonArray linkSets = root.getAsJsonArray("linksets");
            if (linkSets == null || linkSets.isEmpty()) {
                return Collections.emptyList();
            }
            JsonObject linkSet = linkSets.get(0).getAsJsonObject();
            JsonArray linkSetDbs = linkSet.getAsJsonArray("linksetdbs");
            if (linkSetDbs == null || linkSetDbs.isEmpty()) {
                return Collections.emptyList();
            }
            JsonArray links = linkSetDbs.get(0).getAsJsonObject().getAsJsonArray("links");
            List<String> pmids = new ArrayList<>();
            for (JsonElement link : links) {
                pmids.add(link.getAsString());
            }
            return pmids;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Parses papers from an E-fetch XML response.
     *
     * @param xml the XML response from efetch
     * @return list of parsed papers
     */
    public List<Paper> parsePapers(String xml) {
        List<Paper> papers = new ArrayList<>();

        Matcher articleMatcher = ARTICLE_PATTERN.matcher(xml);
        while (articleMatcher.find()) {
            String articleXml = articleMatcher.group(1);
            Paper paper = parseArticle(articleXml);
            if (paper != null) {
                papers.add(paper);
            }
        }

        return papers;
    }

    private Paper parseArticle(String xml) {
        String pmid = extractFirst(PMID_PATTERN, xml);
        String title = extractFirst(TITLE_PATTERN, xml);

        if (pmid == null || title == null) {
            return null;
        }

        Paper.Builder builder = new Paper.Builder()
                .id(pmid)
                .source("pubmed")
                .title(cleanHtml(title))
                .url("https://pubmed.ncbi.nlm.nih.gov/" + pmid);

        String abstractText = extractFirst(ABSTRACT_PATTERN, xml);
        if (abstractText != null) {
            builder.abstractText(cleanHtml(abstractText));
        }

        String journal = extractFirst(JOURNAL_PATTERN, xml);
        if (journal != null) {
            builder.journal(cleanHtml(journal));
        }

        String doi = extractFirst(DOI_PATTERN, xml);
        if (doi != null) {
            builder.doi(doi);
        }

        String year = extractFirst(YEAR_PATTERN, xml);
        if (year != null) {
            builder.publicationDate(year);
        }

        Matcher authorMatcher = AUTHOR_PATTERN.matcher(xml);
        List<Author> authors = new ArrayList<>();
        while (authorMatcher.find()) {
            String lastName = authorMatcher.group(1);
            String foreName = authorMatcher.group(2);
            authors.add(new Author(foreName + " " + lastName));
        }
        builder.authors(authors);

        return builder.build();
    }

    private String extractFirst(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String cleanHtml(String text) {
        return text.replaceAll("<[^>]+>", "").trim();
    }
}
