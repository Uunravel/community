package com.nowcoder.community.controller;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.service.ElasticsearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private UserService userService;

    @Autowired
    private LikeService likeService;

    //search?keyword
    @RequestMapping(path="/search", method = RequestMethod.GET)
    public String search(String keyword, Page page, Model model) throws IOException {
        //搜索帖子
        SearchHits<DiscussPost> searchResults = elasticsearchService.searchDiscussPost(keyword, page.getCurrent()-1, page.getLimit());
        // 聚合数据
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if(searchResults != null) {
            searchResults.forEach(hit -> {
                DiscussPost post = hit.getContent();
                Map<String, Object> map = new HashMap<>();
//                post.setTitle(hit.getHighlightField("title").get(0));
                hit.getHighlightField("title").forEach(ele->post.setTitle(ele));
//                post.setContent(hit.getHighlightField("content").get(0));
                hit.getHighlightField("content").forEach(ele->post.setTitle(ele));
//                discussPosts.add(post);
//                System.out.println(hit);
                // 帖子
                map.put("post", post);
                // 作者
                map.put("user", userService.findUserById(post.getUserId()));
                // 点赞数量
                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));

                discussPosts.add(map);
            });
//            for(Hit<DiscussPost> hit : searchResults.hits().hits()) {
//                DiscussPost post = hit.source();
//                Map<String, Object> map = new HashMap<>();
//                String postTitle = "";
//                for(Map.Entry<String, List<String>> ss : hit.highlight().entrySet()) {
//                    System.out.println(ss.getKey() + ss.getValue());
//                    postTitle = ss.getValue().get(0);
//                }
//                post.setTitle(postTitle);
//                // 帖子
//                map.put("post", post);
//                // 作者
//                map.put("user", userService.findUserById(post.getUserId()));
//                // 点赞数量
//                map.put("likeCount", likeService.findEntityLikeCount(ENTITY_TYPE_POST, post.getId()));
//
//                discussPosts.add(map);
//            }
        }
        model.addAttribute("discussPosts", discussPosts);
        model.addAttribute("keyword", keyword);

        // 分页信息
        page.setPath("/search?keyword=" + keyword);
        page.setRows(searchResults == null ? 0 : (int) searchResults.getTotalHits());
        return "site/search";
    }
}
