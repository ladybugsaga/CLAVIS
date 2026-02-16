package io.clavis.core.models;

import java.util.List;

/**
 * Generic search result wrapper.
 *
 * @param <T> the type of items in the result
 * @author CLAVIS Team
 * @version 1.0.0
 * @since 2025-01-01
 */
public class SearchResult<T> {

    private final List<T> items;
    private final int totalCount;
    private final String query;
    private final String source;

    public SearchResult(List<T> items, int totalCount, String query, String source) {
        this.items = List.copyOf(items);
        this.totalCount = totalCount;
        this.query = query;
        this.source = source;
    }

    public List<T> getItems() {
        return items;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public String getQuery() {
        return query;
    }

    public String getSource() {
        return source;
    }

    public int getReturnedCount() {
        return items.size();
    }

    public boolean hasResults() {
        return !items.isEmpty();
    }

    @Override
    public String toString() {
        return String.format("SearchResult{source='%s', query='%s', returned=%d, total=%d}",
                source, query, items.size(), totalCount);
    }
}
