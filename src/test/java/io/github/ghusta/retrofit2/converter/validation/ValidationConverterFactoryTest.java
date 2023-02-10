package io.github.ghusta.retrofit2.converter.validation;

import io.github.ghusta.retrofit2.api.github.GitHubService;
import io.github.ghusta.retrofit2.api.github.Repository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

class ValidationConverterFactoryTest {

    private static Retrofit retrofit;
    private static GitHubService gitHubService;

    @BeforeAll
    static void setUpGlobal() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.github.com/")
                .addConverterFactory(ValidationConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create())
                // .addConverterFactory(GsonConverterFactory.create())
                .build();

        System.out.println("List of Retrofit ConverterFactories : ");
        retrofit.converterFactories()
                .forEach(x -> System.out.println(" * " + x));

        gitHubService = retrofit.create(GitHubService.class);
    }

    @Test
    void shouldListRepos_returnOk() throws IOException {
        Response<List<Repository>> response = gitHubService.listRepos("github").execute();
        if (response.isSuccessful()) {
            List<Repository> repositories = response.body();
            assertThat(repositories)
                    .isNotEmpty()
                    .hasSizeGreaterThan(1);
        } else {
            fail();
        }
    }

    @Test
    void shouldCreateRepo_validateRequest() throws IOException {
        Repository newRepository = new Repository();
        newRepository.setName("");
        newRepository.setDescription("Hello");

        Exception exception = assertThrows(Exception.class, () -> {
            Response<Void> response = gitHubService.createRepo(newRepository).execute();

        });
        assertThat(exception).isInstanceOf(ConstraintViolationException.class);
        ConstraintViolationException constraintViolationException = (ConstraintViolationException) exception;
        assertThat(constraintViolationException.getConstraintViolations()).hasSize(1);
    }

}