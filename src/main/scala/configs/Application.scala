package configs

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import endpoints.UserEndpoint
import repositories.UserRepository

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

object Application extends App with UserEndpoint {
  implicit val sys: ActorSystem = ActorSystem("akka-http-mongodb-microservice")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = sys.dispatcher

  val log = sys.log

  override val repository: UserRepository = new UserRepository(Mongo.userCollection)

  Http().bindAndHandle(userRoute, "0.0.0.0", 8080).onComplete {
    case Success(b) => log.info(s"application is up and running at ${b.localAddress.getHostName}:${b.localAddress.getPort}")
    case Failure(e) => log.error(s"could not start application: {}", e.getMessage)
  }
}