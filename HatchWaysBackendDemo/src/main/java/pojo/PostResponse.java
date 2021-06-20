package pojo;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class PostResponse implements Serializable {
    private List<Post> posts;
    private Set<Post> uniquePosts;

    public Set<Post> getUniquePosts() {
        return uniquePosts;
    }

    public void setUniquePosts(Set<Post> uniquePosts) {
        this.uniquePosts = uniquePosts;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }
}
