package dmit2015.resource;

import common.validator.BeanValidator;
import dmit2015.dto.AircraftMapper;
import dmit2015.entity.Aircraft;
import dmit2015.dto.AircraftDto;
import dmit2015.repository.AircraftRepository;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.OptimisticLockException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.ClaimValue;
import org.eclipse.microprofile.jwt.Claims;

import java.net.URI;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
@Path("AircraftDto")                // All methods in this class are associated this URL path
@Consumes(MediaType.APPLICATION_JSON)
// All methods in this class expects method parameters to contain data in JSON format
@Produces(MediaType.APPLICATION_JSON)    // All methods in this class returns data in JSON format
public class AircraftDtoResource {

    @Inject
    @Claim(standard = Claims.upn)   // The username for the user.
    private ClaimValue<Optional<String>> optionalUsername;

    @Inject
    @Claim(standard = Claims.groups)    // The roles that the subject is a member of.
    private ClaimValue<Set<String>> optionalGroups;
    @Inject
    private UriInfo uriInfo;

    @Inject
    private AircraftRepository _aircraftRepository;

    @GET    // This method only accepts HTTP GET requests.
    public Response listAircrafts() {
        return Response.ok(
                _aircraftRepository
                        .findAll()
                        .stream()
                        .map(AircraftMapper.INSTANCE::toDto)
                        .collect(Collectors.toList())
        ).build();
    }

    @Path("{id}")
    @GET    // This method only accepts HTTP GET requests.
    public Response findAircraftById(@PathParam("id") Long id) {
        Aircraft existingAircraft = _aircraftRepository.findById(id).orElseThrow(NotFoundException::new);

        AircraftDto dto = AircraftMapper.INSTANCE.toDto(existingAircraft);

        return Response.ok(dto).build();
    }

    @RolesAllowed({"Administration","Finance","Executive"})
    @POST    // This method only accepts HTTP POST requests.
    public Response addAircraft(AircraftDto dto, @Context UriInfo uriInfo) {
        Aircraft newAircraft = AircraftMapper.INSTANCE.toEntity(dto);

        String errorMessage = BeanValidator.validateBean(newAircraft);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        _aircraftRepository.add(newAircraft);

        URI aircraftsUri = uriInfo.getAbsolutePathBuilder().path(newAircraft.getId().toString()).build();
        return Response.created(aircraftsUri).build();
    }

    @RolesAllowed({"Administration","Executive"})
    @PUT            // This method only accepts HTTP PUT requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response updateAircraft(@PathParam("id") Long id, AircraftDto dto) {
        if (!id.equals(dto.getId())) {
            throw new BadRequestException();
        }

        Aircraft existingAircraft = _aircraftRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

        Aircraft updatedAircraft = AircraftMapper.INSTANCE.toEntity(dto);

        String errorMessage = BeanValidator.validateBean(updatedAircraft);
        if (errorMessage != null) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(errorMessage)
                    .build();
        }

        // TODO: copy properties from the updated entity to the existing entity such as copy the version property shown below
        existingAircraft.setVersion(updatedAircraft.getVersion());
        existingAircraft.setModel(updatedAircraft.getModel());
        existingAircraft.setManufacturer(updatedAircraft.getManufacturer());
        existingAircraft.setTailNumber(updatedAircraft.getTailNumber());

        try {
            _aircraftRepository.update(existingAircraft);
        } catch (OptimisticLockException ex) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("The data you are trying to update has changed since your last read request.")
                    .build();
        } catch (Exception ex) {
            // Return an HTTP status of "500 Internal Server Error" containing the exception message
            return Response.
                    serverError()
                    .entity(ex.getMessage())
                    .build();
        }

        // Returns an HTTP status "200 OK" and include in the body of the response the object that was updated
        return Response.ok(existingAircraft).build();
    }

    @RolesAllowed({"Executive"})
    @DELETE            // This method only accepts HTTP DELETE requests.
    @Path("{id}")    // This method accepts a path parameter and gives it a name of id
    public Response deleteAircraft(@PathParam("id") Long id) {

        Aircraft existingAircraft = _aircraftRepository
                .findById(id)
                .orElseThrow(NotFoundException::new);

        try {
            _aircraftRepository.delete(existingAircraft);    // Removes the Aircraft from being persisted
        } catch (Exception ex) {
            // Return a HTTP status of "500 Internal Server Error" containing the exception message
            return Response
                    .serverError()
                    .encoding(ex.getMessage())
                    .build();
        }

        // Returns an HTTP status "204 No Content" to indicate the resource was deleted
        return Response.noContent().build();

    }

}