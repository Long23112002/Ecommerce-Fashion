package org.example.ecommercefashion.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoadMoreResponse<T> {

    private String next;

    private String previous;

    private Collection<T> results;

    public LoadMoreResponse<T> toLoadMore(String apiBase, Object id, int offset, int limit, int totalChats, List<T> response) {
        String next = generateNextLink(apiBase, id, offset, limit, totalChats);
        String previous = generatePreviousLink(apiBase, id, offset, limit);

        return LoadMoreResponse.<T>builder()
                .results(response)
                .next(next)
                .previous(previous)
                .build();
    }

    private String generateNextLink(String apiBase, Object id, int offset, int limit, int total) {
        total--;
        int nextIndex = limit + offset;
        if (nextIndex <= total) {
            int nextOffset = Math.min(nextIndex, total);
            int nextLimit = Math.min(limit, total - nextOffset + 1);
            return String.format("%s%s?offset=%d&limit=%d", apiBase, id.toString(), nextOffset, nextLimit);
        }
        return null;
    }

    private String generatePreviousLink(String apiBase, Object id, int offset, int limit) {
        if (offset > 0) {
            int previousOffset = Math.max(0, offset - limit);
            int previousLimit = Math.min(limit, offset);
            return String.format("%s%s?offset=%d&limit=%d", apiBase, id.toString(), previousOffset, previousLimit);
        }
        return null;
    }

}
