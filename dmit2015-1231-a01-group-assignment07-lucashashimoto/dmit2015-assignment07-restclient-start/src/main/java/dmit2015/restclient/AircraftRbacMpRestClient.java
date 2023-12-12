package dmit2015.restclient;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

/**
 * The baseUri for the web MpRestClient be set in either microprofile-config.properties (recommended)
 * or in this file using @RegisterRestClient(baseUri = "http://server/path").
 * <p>
 * To set the baseUri in microprofile-config.properties:
 * 1) Open src/main/resources/META-INF/microprofile-config.properties
 * 2) Add a key/value pair in the following format:
 * package-name.ClassName/mp-rest/url=baseUri
 * For example:
 * package-name:    dmit2015.restclient
 * ClassName:       AircraftRbacMpRestClient
 * baseUri:         http://localhost:8080/contextName
 * The key/value pair you need to add is:
 * <code>
 * dmit2015.restclient.AircraftRbacMpRestClient/mp-rest/url=http://localhost:8080/contextName
 * </code>
 * <p>
 * To use the client interface from an environment does support CDI, add @Inject and @RestClient before the field declaration such as:
 * <code>
 *
 * @Inject
 * @RestClient private AircraftRbacMpRestClient _todoitemMpRestClient;
 * </code>
 * <p>
 * To use the client interface from an environment that does not support CDI, you can use the RestClientBuilder class to programmatically build an instance as follows:
 * <code>
 * URI apiURI = new URI("http://sever/contextName");
 * AircraftRbacMpRestClient _todoitemMpRestClient = RestClientBuilder.newBuilder().baseUri(apiURi).build(AircraftMpRestClient.class);
 * </code>
 */
@RequestScoped
@RegisterRestClient(baseUri = "http://localhost:8181/restapi/AircraftDto")
public interface AircraftRbacMpRestClient {

    @POST
    Response create(AircraftRbac newAircraft, @HeaderParam("Authorization") String bearerAuth);

    @GET
    List<AircraftRbac> findAll();

    @GET
    @Path("/{id}")
    AircraftRbac findById(@PathParam("id") Long id);

    @PUT
    @Path("/{id}")
    AircraftRbac update(@PathParam("id") Long id, AircraftRbac updatedAircraft, @HeaderParam("Authorization") String bearerAuth);

    @DELETE
    @Path("/{id}")
    void delete(@PathParam("id") Long id, @HeaderParam("Authorization") String bearerAuth);

}