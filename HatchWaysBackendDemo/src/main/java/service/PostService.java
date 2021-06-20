package service;

import org.apache.commons.collections4.map.PassiveExpiringMap;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;
import pojo.Post;
import pojo.PostRequest;
import pojo.PostResponse;
import pojo.UniquePostResponse;

import java.util.*;

public class PostService {
    RestTemplate restTemplate;
    PostResponse postResponse;
    UniquePostResponse uniquePostResponse;
    String sortBy;
    String direction;

    // Cache data for 1 day = 86400000 ms.
    // Can change value by setting value for timeToLiveMillis
    Map<String, List<Post>> tagsCache = new PassiveExpiringMap<>(86400000);

    /**
     * This method initialize global variables.
     * Checks if parameters are valid or not.
     * If valid proceed for next step else return.
     * Get list of post for each tag and remove duplicates and sort them.
     * @param postRequest consist of tags, sortby and direction parameters.
     * @return Unique post for given post request.
     */
    public UniquePostResponse getPostServiceForRequest(PostRequest postRequest) {
           initializeGlobalParameters();
           postResponse.setPosts(getPost(postRequest));;
           if(postResponse.getPosts().size() <= 0) return uniquePostResponse;
           removeDuplicatePost();
           getUniquePostFromPostResponse();
           sortPostResponse();
           return uniquePostResponse;
    }

    public void populateCache(String key, List<Post> value) {
        tagsCache.put(key, value);
    }
    /**
     * It validates given post request have valid paramters or not.
     * For tags it checks if  given tag is valid string
     * For sortby it checks if its value is from acceptable sortby list.
     * For direction it checks if its value is from acceptable direction list.
     * @param postRequest consist of tags, sortby and direction parameters.
     * @return Post with status message and code for invalid parameters.
     */
    public Post validateRequestParameter(PostRequest postRequest) {
           Post post = new Post();
           List<String> validTags = validateTags(postRequest.getTags());

            if(postRequest.getTags() == null || postRequest.getTags().length == 0 || validTags == null || validTags.size() == 0) {
                post.setStatus(HttpStatus.BAD_REQUEST);
                post.setStatusMessage("Tags parameter is required");
                return post;
            }

           if(!postRequest.getAcceptableDirections().contains(postRequest.getDirection())){
               post.setStatus(HttpStatus.BAD_REQUEST);
               post.setStatusMessage("Direction parameter is invalid");
               return post;
           }

           if(!postRequest.getAcceptableSortByFields().contains(postRequest.getSortBy())){
               post.setStatus(HttpStatus.BAD_REQUEST);
               post.setStatusMessage("Sort parameter is invalid");
               return post;
           }

           return post;
    }

    /**
     * Checks all provided tags in post requests ar valid or not.
     * @param tagsArray String array consist of tags
     * @return list of valid tags.
     */
    private List<String> validateTags(String[] tagsArray){
        if(tagsArray == null || tagsArray.length == 0) return null;

        List<String> outputTags = new ArrayList<>();
        for(String  str : tagsArray){
            if(isValidString(str)){
                outputTags.add(str);
            }
        }
        return outputTags;
    }

    /**
     * Checks if given string is valid or not
     * @param str tag string
     * @return true if valid else false.
     */
    private boolean isValidString(String str){
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        return true;
    }

    /**
     * Initialize all global parameters.
     */
    private void initializeGlobalParameters(){
        restTemplate = new RestTemplate();
        postResponse = new PostResponse();
        uniquePostResponse =  new UniquePostResponse();
        sortBy = "";
        direction = "";
    }


    /**
     * It makes a API call for each tag.
     * Cache them in map.
     * If call to same tag is made, it checks in cache if present return it.
     * @param postRequest consist of tags, sortby and direction parameters.
     */
    public List<Post> getPost(PostRequest postRequest) {
        List<Post> postList = new ArrayList<>();
        sortBy = postRequest.getSortBy();
        direction = postRequest.getDirection();

        for(String tag : postRequest.getTags()){
            if (tagsCache.containsKey(tag)) {
//                System.out.println("Reading from cache for tag : " + tag);
                postList.addAll(tagsCache.get(tag));
            } else {
                postList.addAll(getPostForTag(tag));
            }
        }
        return postList;

    }

    /**
     * This method is async to achieve concurrency in application.
     * It allows method to make multiple calls to tags simulatneously.
     * Once get response, cache them.
     * @param tag string for which to make API call.
     * @return List of post for given tag.
     */
    @Async
    private List<Post> getPostForTag(String tag){
        String url =
                "https://api.hatchways.io/assessment/blog/posts?tag=" + tag;
        PostResponse tempPostResponse =  restTemplate.getForObject(url, PostResponse.class);
        tagsCache.put(tag, tempPostResponse.getPosts());
        return tempPostResponse.getPosts();
    }

    /**
     * Removes duplicate entry from post list using set.
     */
    private void removeDuplicatePost(){
        if(postResponse.getPosts().size() <= 0) return;
        Set<Post> uniquePost = new HashSet<>();

        for(Post post : postResponse.getPosts()){
            uniquePost.add(post);
        }
        postResponse.setUniquePosts(uniquePost);
    }

    /**
     * Convert Post set into uniquePostResponse list.
     */
    private void getUniquePostFromPostResponse(){
        Iterator ite = (Iterator) postResponse.getUniquePosts().iterator();
        List<Post> uniquePostList = new ArrayList<>();
        while (ite.hasNext()){
            uniquePostList.add((Post) ite.next());
        }
        uniquePostResponse.setPosts(uniquePostList);
    }

    /**
     * Sort unique post list according to sort by parameter
     * Place them in ascending or descending direction based on direction parameter.
     */
    private void sortPostResponse(){
        switch (sortBy){
            case "id":
                if(direction.equals("desc"))
                    uniquePostResponse.getPosts().sort(Comparator.comparing(Post::getId).reversed());
                else
                    uniquePostResponse.getPosts().sort(Comparator.comparing(Post::getId));
                break;
            case "reads":
                if(direction.equals("desc"))
                    uniquePostResponse.getPosts().sort(Comparator.comparing(Post::getReads).reversed());
                else
                    uniquePostResponse.getPosts().sort(Comparator.comparing(Post::getReads));
                break;
            case "likes":
                if(direction.equals("desc"))
                    uniquePostResponse.getPosts().sort(Comparator.comparing(Post::getLikes).reversed());
                else
                    uniquePostResponse.getPosts().sort(Comparator.comparing(Post::getLikes));
                break;
            case "popularity":
                if(direction.equals("desc"))
                    uniquePostResponse.getPosts().sort(Comparator.comparing(Post::getPopularity).reversed());
                else
                    uniquePostResponse.getPosts().sort(Comparator.comparing(Post::getPopularity));
                break;
        }
    }

}
