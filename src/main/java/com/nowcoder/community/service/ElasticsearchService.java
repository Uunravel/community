package com.nowcoder.community.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.nowcoder.community.dao.elasticsearch.DiscussPostRepository;
import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ElasticsearchService {

    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ElasticsearchClient client;

    public void saveDiscussPost(DiscussPost post) {
        discussPostRepository.save(post);
    }
    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    public SearchResponse<DiscussPost> searchDiscussPost_version1(String keyword, int current, int limit) throws IOException {

        return client.search(
                s->s.index("discusspost")
                        .query(q->q.term(t->t.field("title").value(v->v.stringValue(keyword))))
                        .highlight(q->q.fields("title", o->o.matchedFields("content").preTags("<em  class='key' style='color:red'>").postTags("</em>")))
                        .query(q->q.term(t->t.field("content").value(v->v.stringValue(keyword))))
                        .highlight(q->q.fields("title", o->o.matchedFields("content").preTags("<em  class='key' style='color:red'>").postTags("</em>")))   // <p class='key' style='color:red'>
                        .from(current)
                        .size(limit)
                        .sort(f->f.field(o->o.field("type").order(SortOrder.Desc)))
                        .sort(f->f.field(o->o.field("score").order(SortOrder.Desc)))
                        .sort(f->f.field(o->o.field("createTime").order(SortOrder.Desc))), DiscussPost.class);
    }

    public SearchHits<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {
        List<String> searchFiels = new ArrayList<>();
        searchFiels.add("title");
        searchFiels.add("content");
        List<HighlightField> highlightFields = new ArrayList<>();
        highlightFields.add(new HighlightField("title"));
        highlightFields.add(new HighlightField("content"));

        Query query = NativeQuery.builder()
                .withQuery(q -> q.multiMatch(t->t.fields(searchFiels).query(keyword)))
                .withHighlightQuery(
                        new HighlightQuery(
                                new Highlight(
                                        new HighlightParameters.HighlightParametersBuilder().withPreTags("<em>").withPostTags("</em>").build(), highlightFields), DiscussPost.class
                        )
                )
                .withPageable(Pageable.ofSize(10).withPage(0))
                .withSort(f->f.field(o->o.field("type").order(SortOrder.Desc)))
                .withSort(f->f.field(o->o.field("score").order(SortOrder.Desc)))
                .withSort(f->f.field(o->o.field("createTime").order(SortOrder.Desc)))
                .build();
        return elasticsearchTemplate.search(query, DiscussPost.class);
    }
}
