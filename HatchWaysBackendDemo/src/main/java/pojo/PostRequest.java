package pojo;

import java.util.HashSet;
import java.util.Set;

public class PostRequest {
    private String[] tags;
    private String sortBy = "id";
    private String direction = "asc";
    private Set<String> acceptableSortByFields;
    private Set<String> acceptableDirections;

    public PostRequest(){
        setAcceptableDirections();
        setAcceptableSortByFields();
    }

    public Set<String> getAcceptableSortByFields() {
        return acceptableSortByFields;
    }

    public void setAcceptableSortByFields(Set<String> acceptableSortByFields) {
        this.acceptableSortByFields = acceptableSortByFields;
    }

    public Set<String> getAcceptableDirections() {
        return acceptableDirections;
    }

    public void setAcceptableDirections(Set<String> acceptableDirections) {
        this.acceptableDirections = acceptableDirections;
    }

    /**
     * Set acceptable sort by fields.
     */
    private void setAcceptableSortByFields(){
        acceptableSortByFields = new HashSet<>();
        acceptableSortByFields.add("id");
        acceptableSortByFields.add("reads");
        acceptableSortByFields.add("likes");
        acceptableSortByFields.add("popularity");
    }

    /**
     * Set acceptable directions field.
     */
    private void setAcceptableDirections(){
        acceptableDirections = new HashSet<>();
        acceptableDirections.add("desc");
        acceptableDirections.add("asc");
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
