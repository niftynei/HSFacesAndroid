package knaps.hacker.school.networking;

import java.util.List;

import knaps.hacker.school.models.Batch;
import knaps.hacker.school.models.Student;
import retrofit.RequestInterceptor;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by lisaneigut on 6 Apr 2014.
 */
public interface HSApiService {

    @GET("/api/v1/people/{person_id}")
    Student getStudent(@Path("person_id") long studentId);

    @GET("/api/v1/people/me")
    Student getMe();

    @GET("/api/v1/batches")
    List<Batch> getBatches();

    @GET("/api/v1/batches/{batch_id}")
    Batch getBatch(@Path("batch_id") long batchId);

    @GET("/api/v1/batches/{batch_id}/people")
    List<Student> getPeopleInBatch(@Path("batch_id") long batchId);

}
