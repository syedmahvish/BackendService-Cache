package controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pojo.*;
import service.PostService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@RestController
public class PostController {

    PostService postService = new PostService();

    /**
     * This method checks if server is available
     * @return PingResponse class with attributes HTTPStatus = OK,
     * success = true(boolean) and message = "Service is running".
     */
    @GetMapping("/api/ping")
    public ResponseEntity<PingResponse> getPing(){
        PingResponse pingResponse = new PingResponse();
        pingResponse.setSuccess(true);
        pingResponse.setStatus(HttpStatus.OK);
        pingResponse.setStatusMessage("Service is running");
        return new ResponseEntity<>(pingResponse, HttpStatus.OK);
    }

    /**
     * This method returns list of unique post for given post request.
     * @param postRequest : PostRequest class with attributes:
     *                    tags = to handle list of tags
     *                    sortBy = default is 'id' ,
     *                    Direction = default is 'asc'
     * @return If success, list of unique post
     *         else Error with HTTPStatus - BAD REQUEST and Message - Explaining error
     */
    @GetMapping("/api/posts")
    public ResponseEntity<UniquePostResponse> getPosts(PostRequest postRequest){
        UniquePostResponse uniquePostResponse = new UniquePostResponse();
        Post validatePostRequest = postService.validateRequestParameter(postRequest);

        if (validatePostRequest.getStatus() != null || validatePostRequest.getStatusMessage() != null) {
            uniquePostResponse.setStatus(validatePostRequest.getStatus());
            uniquePostResponse.setMessage(validatePostRequest.getStatusMessage());
            return new ResponseEntity<>(uniquePostResponse, HttpStatus.OK);
        }

        uniquePostResponse = postService.getPostServiceForRequest(postRequest);
        return new ResponseEntity<>(uniquePostResponse, HttpStatus.OK);
    }

}
