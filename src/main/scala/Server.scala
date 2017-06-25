import java.util.concurrent.CountDownLatch

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._

class Server(repo: Repository, port: Int) {
  implicit val system = ActorSystem("data-service")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  val latch = new CountDownLatch(1)

  val route: Route =
    post {
      path("analytics" / Segment) { event =>
        parameters('timestamp, 'user) { (timestamp, user) =>
          repo.insert(timestamp.toLong, user, Event(event))
          complete(s"timestamp: $timestamp, user: $user, event: $event\n")
        }
      }
    } ~
    get {
      path("analytics") {
        parameters('timestamp) { timestamp =>
          val stats = repo.scan(timestamp.toLong)
          val output = s"unqiue_users,${stats.users}\nclicks,${stats.clicks}\nimpressions,${stats.impressions}\n"
          complete(output)
        }
      }
    } ~
    get {
      path("ping") {
        complete("pong")
      }
    } ~
    delete {
      path("shutdown") {
        latch.countDown()
        complete("shutdown\n")
      }
    }

  def start(): Unit = {
    val bindingFuture = Http().bindAndHandle(route, "localhost", port)

    latch.await() // block until server is shut-down

    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
