package akkahttprestservice

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import spray.json.DefaultJsonProtocol._

import scala.concurrent.ExecutionContext

object Server extends App {

  implicit val system: ActorSystem = ActorSystem("akka-http-rest-service")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContext = system.dispatcher

  val usersHandler = new UsersHandler()
  val routes = new Routes(usersHandler)
  val logger = Logging(system, getClass)

  Http().bindAndHandle(routes.users, "localhost", 8080)
}

class Routes(val usersHandler: UsersHandler) {

  val users: Route =
    logRequestResult("request-result") {
      get {
        pathPrefix("users" / Remaining) { userId =>
          usersHandler.findUser(userId)
        }
      }
    }
}

class UsersHandler() {

  private implicit val userFormat = jsonFormat4(User)

  def findUser(userId: String): Route = {
    val user = User("Jan", "Kowalski", 42, "1234")
    complete(user)
  }
}

final case class User(firstName: String, lastName: String, age: Int, id: String)
