# Robotic hoover navigation

### Overview

This is an interview assignment in the context of the hiring process for phinxt. The service navigates
an imaginary robotic hoover through an equally imaginary room.
The specifications are as follows:
* room dimensions as X and Y coordinates, identifying the top right corner of the room rectangle. The bottom left corner is the point of origin for our coordinate system and is defined by X:0 and Y:0
* an initial hoover position (input as X and Y position on our coordinate system)
* locations of patches of dirt - (input is a list of X and Y positions on our coordinate system)
* driving instructions as cardinal directions - in this context only W, E, N, S are allowed i.e. no SE, NW etc

### Tech stack

* Kotlin 1.9.25 
* Spring boot 3.3.4 
* Java 21 

No persistence layer was needed. Gradle was used as the build tool.

### How to run

In order to run the application, import it on Intellij IDEA (or the IDE of choice), create a Run Configuration and run it.  

Alternatively, from the command line, you can run it as follows:

`./gradlew bootRun`

Another way would be to create the jar and then run it directly with java 21:

`./gradlew bootJar` -- this will create the jar in build/libs

`java -jar build/libs/phinxt-assignement-1.0.0.jar`

All these ways will start the application locally. There is Swagger configured, so you can access it on port 8080:

[Swagger link](http://localhost:8080/swagger-ui/index.html)

Alternatively, a request to the navigate endpoint can be done as follows using curl:

`curl -X POST http://localhost:8080/api/v1/robotic-hoover/navigate 
  -H 'accept: */*' 
  -H 'Content-Type: application/json' 
  -d '{
        "roomSize": [5,5],
        "coords": [1,2],
        "patches": [
            [1,0],
            [2,2],
            [2,3]
         ],
        "instructions": "NNESEESWNWW"
      }`

### Notes

The service is implemented as a Spring boot web service with one POST endpoint, accessible at /api/v1/robotic-hoover/navigate. 
The input of the request is what is described on the assignment document. That is roomSize, coords, a list of 
patches and instructions for the hoover. 

#### Validation
Validations are done in order to ensure that the input is valid.  
Since the coordinates start from (0,0), all input coordinate points should be greater than 0. Further validation is being 
done to verify that the hoover and patches are within the room rectangle. The instructions are validated so that the input is within 'N', 'E', 'W', 'S'. 
The instructions can also be null or empty, since in this case the hoover just stays in place.
In a future version we could extend the service to also include other directions like NorthEast NE etc. Both upper and lower case characters work.

#### Project structure
There are two services and a controller on the project. The controller handles the validation, using the CoordinateValidationService 
and returns a 400 in case validation fails. On a successful validation, the navigation service is called and performs the necessary actions in order to calculate
the hoover position on the space, after all instructions are performed. A POST is used for the endpoint. In a future version we could have resources (hoovers) 
that are persisted and could be identified by their id or (unique) name for this endpoint.

#### Patches  
Patches are given by the user as a list and they are converted to a map on the HooverNavigationService. The key is the location on the grid. This is because
we constantly need to check if the hoover is on a patch and it is more efficient to have this lookup on a map instead of iterating a list over and over again.
Each time a patch is "cleaned", it is removed from the map, since we do not want to report cleaning it twice, on a subsequent pass over this point.

#### Testing  
There is an integration test for the Controller - using MockMvc to perform the actual requests. There is another integration test for the HooverNavigationService and there 
is a Unit test for the validation service. Multiple scenarios are being tested, including hitting a wall, running over a patch twice (it should be reported only once) etc
