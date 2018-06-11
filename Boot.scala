package org.broadinstitute.dsde.workbench.entityservice


import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import net.ceedubs.ficus.Ficus._

import scala.concurrent.{ExecutionContext, Future}

object Boot extends App with LazyLogging {

  private def startup(): Unit = {

    val config = ConfigFactory.load()

    // we need an ActorSystem to host our application in
    implicit val system = ActorSystem("entity-service")
    implicit val materializer = ActorMaterializer()
    import scala.concurrent.ExecutionContext.Implicits.global

    val entityServiceRoutes = new EntityServiceRoutes()

    Http().bindAndHandle(entityServiceRoutes.route, "0.0.0.0", 8080)
      .recover {
        case t: Throwable =>
          logger.error("FATAL - failure starting http server", t)
          throw t
      }
  }

  startup()
}
