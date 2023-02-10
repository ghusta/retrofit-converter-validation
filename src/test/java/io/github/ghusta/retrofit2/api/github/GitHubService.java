package io.github.ghusta.retrofit2.api.github;

import jakarta.validation.Valid;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

/**
 * From example : https://square.github.io/retrofit/
 */
public interface GitHubService {

    /**
     * Ref : https://docs.github.com/en/rest/repos/repos?#list-repositories-for-a-user
     */
    @GET("users/{user}/repos")
    Call<List<Repository>> listRepos(@Path("user") String user);

    /**
     * Ref : https://docs.github.com/en/rest/repos/repos?#create-a-repository-for-the-authenticated-user
     */
    @POST("users/repos")
    Call<Void> createRepo(@Valid @Body Repository repo);

}