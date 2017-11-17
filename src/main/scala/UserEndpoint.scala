import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Location
import akka.http.scaladsl.server.Directives._
import akka.stream.Materializer

import scala.concurrent.Future
import de.heikoseeberger.akkahttpjackson.JacksonSupport._

import scala.concurrent.ExecutionContext

trait UserEndpoint {
  implicit val mat: Materializer
  implicit val ec: ExecutionContext

  val repository: UserRepository

  val userRoute = {
    pathPrefix("api" / "users") {
      get {
        path(Segment).as(FindByIdRequest) { request =>
          complete {
            repository
              .findById(request.id)
              .map { optionalUser => optionalUser.map { _.asResource } }
              .flatMap {
                case None => Future.successful(HttpResponse(status = StatusCodes.NotFound))
                case Some(user) => Marshal(user).to[ResponseEntity].map { e => HttpResponse(entity = e) }
              }
          }
        }
      } ~ post {
        entity(as[UserResource]) { user =>
          complete {
            repository
              .save(user.asDomain)
              .map { id =>
                HttpResponse(status = StatusCodes.Created, headers = List(Location(s"/api/users/$id")))
              }
          }
        }
      }
    }
  }
}