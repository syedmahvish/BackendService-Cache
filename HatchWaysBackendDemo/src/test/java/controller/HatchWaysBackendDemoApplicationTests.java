package controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pojo.PingResponse;
import pojo.UniquePostResponse;
import service.PostService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = HatchWaysBackendDemoApplication.class)
@WebAppConfiguration
class HatchWaysBackendDemoApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;
	private MockMvc mockMvc;
	@Autowired
	private PostController postController;
	@MockBean
	private PostService postService;

	protected String mapToJson(Object obj) throws JsonProcessingException {
		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.writeValueAsString(obj);
	}
	protected <T> T mapFromJson(String json, Class<T> clazz)
			throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper objectMapper = new ObjectMapper();
		return objectMapper.readValue(json, clazz);
	}

	@Test
	void contextLoads() {
		assertThat(postController).isNotNull();
	}

	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	public void shouldReturn200WhenSendingRequestToController() throws Exception {
		String uri = "/api/ping";
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(uri)
				.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();
		Assert.assertEquals(200, mvcResult.getResponse().getStatus());
	}


	@Test
	public void invalidTagsSendingRequestToController() throws Exception {
		String[] invalidUrlArray = new String[]{"/api/posts?tags",
												"/api/posts?tags=      "};
		for(String url : invalidUrlArray){

			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
					.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

			UniquePostResponse post = new UniquePostResponse();
			post.setPosts(null);
			post.setStatus(HttpStatus.BAD_REQUEST);
			post.setMessage("Tags parameter is required");
			String expected = mapToJson(post);

			String actual = mvcResult.getResponse().getContentAsString();

			System.out.println("url=" + url + " actual=" + actual);

			Assert.assertEquals(expected, actual);
		}
	}

	@Test
	public void invalidSortBySendingRequestToController() throws Exception {
		String[] invalidUrlArray = new String[]{"/api/posts?tags=health,tech&sortBy=####",
												"/api/posts?tags=health,tech&sortBy= \"",
												"/api/posts?tags=health,tech&sortBy=tags\"",
												"/api/posts?tags=health,tech&sortBy=author\"",
												"/api/posts?tags=health,tech&sortBy=authorId\""};
		for(String url : invalidUrlArray){
			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
					.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

			UniquePostResponse post = new UniquePostResponse();
			post.setPosts(null);
			post.setStatus(HttpStatus.BAD_REQUEST);
			post.setMessage("Sort parameter is invalid");
			String expected = mapToJson(post);

			String actual = mvcResult.getResponse().getContentAsString();
			Assert.assertEquals(expected, actual);
		}
	}

	@Test
	public void invalidDirectionSendingRequestToController() throws Exception {
		String[] invalidUrlArray = new String[]{"/api/posts?tags=health,tech&direction=####",
												"/api/posts?tags=health,tech&sortBy=likes&direction= \"",
												"/api/posts?tags=health,tech&direction=up\""};
		for(String url : invalidUrlArray){
			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
					.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

			UniquePostResponse post = new UniquePostResponse();
			post.setPosts(null);
			post.setStatus(HttpStatus.BAD_REQUEST);
			post.setMessage("Direction parameter is invalid");
			String expected = mapToJson(post);

			String actual = mvcResult.getResponse().getContentAsString();
			Assert.assertEquals(expected, actual);
		}
	}

	@Test
	public void invalidRequestSendingToController() throws Exception {
		String[] invalidUrlArray = new String[]{"/api/posts?tags=  &sortBy=  &direction=####",
													"/api/posts?tags=######,tech&sortBy=likes&direction= \"",
													"/api/posts?tags=health,tech&direction=down&sortBy=  \""};
		String[] errorMessageArray = new String[]{"Tags parameter is required",
													"Tags parameter is required",
													"Direction parameter is invalid"};


		for(int i = 0; i < invalidUrlArray.length; i++){
			String  url = invalidUrlArray[i];
			String errorMessage = errorMessageArray[i];

			MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(url)
					.accept(MediaType.APPLICATION_JSON_VALUE)).andReturn();

			UniquePostResponse post = new UniquePostResponse();
			post.setPosts(null);
			post.setStatus(HttpStatus.BAD_REQUEST);
			post.setMessage(errorMessage);
			String expected = mapToJson(post);

			String actual = mvcResult.getResponse().getContentAsString();
			Assert.assertEquals(expected, actual);
		}
	}

}
