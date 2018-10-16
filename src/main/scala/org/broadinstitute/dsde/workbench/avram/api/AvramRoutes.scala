package org.broadinstitute.dsde.workbench.avram.api

import com.google.api.server.spi.config.{Api, ApiMethod}
import java.util.logging.Logger
import javax.servlet.http.HttpServletRequest

import org.broadinstitute.dsde.workbench.avram.dataaccess.SamUserInfoResponse
import org.broadinstitute.dsde.workbench.avram.model.{DbPoolStats, Status}
import org.broadinstitute.dsde.workbench.avram.util.ErrorResponse


case class Pong()

/**
  * Illustration of business logic living outside of the endpoint class.
  */
object PongService {
  private val log = Logger.getLogger(getClass.getName)

  def pong(userInfo: SamUserInfoResponse): Either[ErrorResponse, Pong] = {
    log.info(userInfo.userEmail)
    log.info(userInfo.userSubjectId)
    Right(Pong())
  }
}

@Api(name = "avram", version = "v1", scopes = Array("https://www.googleapis.com/auth/userinfo.email"))
class AvramRoutes extends BaseEndpoint {

  @ApiMethod(name = "ping", httpMethod = "get", path = "ping")
  def ping: Pong = {
    Pong()
  }

  @ApiMethod(name = "authPing", httpMethod = "get", path = "authPing")
  def authPing(request: HttpServletRequest): Pong = {
    handleAuthenticatedRequest(request) { userInfo => PongService.pong(userInfo) }
  }

  @ApiMethod(name = "status", httpMethod = "get", path = "status")
  def status: Status = {
    //TODO: make this use HealthMoniter
    inTransaction(_.sqlDBStatus()).map(_ => Status("ok")).head
  }

  @ApiMethod(name = "dbPoolStats", httpMethod = "get", path = "dbPoolStats")
  def dbPoolStats: DbPoolStats = {
    getDbPoolStats
  }

}
