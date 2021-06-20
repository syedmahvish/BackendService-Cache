package pojo;

import org.springframework.http.HttpStatus;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Objects;

public class Post {
    private Integer id;
    private String author;
    private Integer authorId;
    private Integer likes;
    private Double popularity;
    private Integer reads;
    private String[] tags;
    private HttpStatus status;
    private String statusMessage;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public Integer getReads() {
        return reads;
    }

    public void setReads(Integer reads) {
        this.reads = reads;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Checks if two post are same or not on basis of each attribute.
     * @param o Other post object to be compare with current.
     * @return true if same else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;

        Arrays.sort(post.tags);
        Arrays.sort(this.tags);
        boolean tagsComparison =  Arrays.equals(post.getTags(), this.getTags());

        return Objects.equals(getId(), post.getId()) &&
                Objects.equals(getAuthor(), post.getAuthor()) &&
                Objects.equals(getAuthorId(), post.getAuthorId()) &&
                Objects.equals(getLikes(), post.getLikes()) &&
                Objects.equals(getPopularity(), post.getPopularity()) &&
                Objects.equals(getReads(), post.getReads()) &&
                tagsComparison;
    }

    /**
     * Checks if post are same or not
     * @return true if same else false
     */
    @Override
    public int hashCode() {
        int result = Objects.hash(getId(), getAuthor(), getAuthorId(), getLikes(), getPopularity(), getReads());
        result = 31 * result + Arrays.hashCode(getTags());
        return result;
    }
}
