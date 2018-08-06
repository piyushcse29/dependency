/**
 * Generated by API Builder - https://www.apibuilder.io
 * Service version: 0.0.8
 * apibuilder 0.14.3 app.apibuilder.io/flow/github-oauth/0.0.8/play_2_6_client
 */
package io.flow.github.oauth.v0.models {

  final case class AccessToken(
    accessToken: String,
    scope: String,
    tokenType: io.flow.github.oauth.v0.models.TokenType
  )

  final case class AccessTokenForm(
    clientId: String,
    clientSecret: String,
    code: String,
    redirectUri: _root_.scala.Option[String] = None,
    state: _root_.scala.Option[String] = None
  )

  sealed trait TokenType extends _root_.scala.Product with _root_.scala.Serializable

  object TokenType {

    case object Bearer extends TokenType { override def toString = "bearer" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    final case class UNDEFINED(override val toString: String) extends TokenType

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all: scala.List[TokenType] = scala.List(Bearer)

    private[this]
    val byName: Map[String, TokenType] = all.map(x => x.toString.toLowerCase -> x).toMap

    def apply(value: String): TokenType = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): _root_.scala.Option[TokenType] = byName.get(value.toLowerCase)

  }

}

package io.flow.github.oauth.v0.models {

  package object json {
    import play.api.libs.json.__
    import play.api.libs.json.JsString
    import play.api.libs.json.Writes
    import play.api.libs.functional.syntax._
    import io.flow.github.oauth.v0.models.json._

    private[v0] implicit val jsonReadsUUID = __.read[String].map(java.util.UUID.fromString)

    private[v0] implicit val jsonWritesUUID = new Writes[java.util.UUID] {
      def writes(x: java.util.UUID) = JsString(x.toString)
    }

    private[v0] implicit val jsonReadsJodaDateTime = __.read[String].map { str =>
      import org.joda.time.format.ISODateTimeFormat.dateTimeParser
      dateTimeParser.parseDateTime(str)
    }

    private[v0] implicit val jsonWritesJodaDateTime = new Writes[org.joda.time.DateTime] {
      def writes(x: org.joda.time.DateTime) = {
        import org.joda.time.format.ISODateTimeFormat.dateTime
        val str = dateTime.print(x)
        JsString(str)
      }
    }

    private[v0] implicit val jsonReadsJodaLocalDate = __.read[String].map { str =>
      import org.joda.time.format.ISODateTimeFormat.dateParser
      dateParser.parseLocalDate(str)
    }

    private[v0] implicit val jsonWritesJodaLocalDate = new Writes[org.joda.time.LocalDate] {
      def writes(x: org.joda.time.LocalDate) = {
        import org.joda.time.format.ISODateTimeFormat.date
        val str = date.print(x)
        JsString(str)
      }
    }

    implicit val jsonReadsGithubOauthTokenType = new play.api.libs.json.Reads[io.flow.github.oauth.v0.models.TokenType] {
      def reads(js: play.api.libs.json.JsValue): play.api.libs.json.JsResult[io.flow.github.oauth.v0.models.TokenType] = {
        js match {
          case v: play.api.libs.json.JsString => play.api.libs.json.JsSuccess(io.flow.github.oauth.v0.models.TokenType(v.value))
          case _ => {
            (js \ "value").validate[String] match {
              case play.api.libs.json.JsSuccess(v, _) => play.api.libs.json.JsSuccess(io.flow.github.oauth.v0.models.TokenType(v))
              case err: play.api.libs.json.JsError => err
            }
          }
        }
      }
    }

    def jsonWritesGithubOauthTokenType(obj: io.flow.github.oauth.v0.models.TokenType) = {
      play.api.libs.json.JsString(obj.toString)
    }

    def jsObjectTokenType(obj: io.flow.github.oauth.v0.models.TokenType) = {
      play.api.libs.json.Json.obj("value" -> play.api.libs.json.JsString(obj.toString))
    }

    implicit def jsonWritesGithubOauthTokenType: play.api.libs.json.Writes[TokenType] = {
      new play.api.libs.json.Writes[io.flow.github.oauth.v0.models.TokenType] {
        def writes(obj: io.flow.github.oauth.v0.models.TokenType) = {
          jsonWritesGithubOauthTokenType(obj)
        }
      }
    }

    implicit def jsonReadsGithubOauthAccessToken: play.api.libs.json.Reads[AccessToken] = {
      for {
        accessToken <- (__ \ "access_token").read[String]
        scope <- (__ \ "scope").read[String]
        tokenType <- (__ \ "token_type").read[io.flow.github.oauth.v0.models.TokenType]
      } yield AccessToken(accessToken, scope, tokenType)
    }

    def jsObjectAccessToken(obj: io.flow.github.oauth.v0.models.AccessToken): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "access_token" -> play.api.libs.json.JsString(obj.accessToken),
        "scope" -> play.api.libs.json.JsString(obj.scope),
        "token_type" -> play.api.libs.json.JsString(obj.tokenType.toString)
      )
    }

    implicit def jsonWritesGithubOauthAccessToken: play.api.libs.json.Writes[AccessToken] = {
      new play.api.libs.json.Writes[io.flow.github.oauth.v0.models.AccessToken] {
        def writes(obj: io.flow.github.oauth.v0.models.AccessToken) = {
          jsObjectAccessToken(obj)
        }
      }
    }

    implicit def jsonReadsGithubOauthAccessTokenForm: play.api.libs.json.Reads[AccessTokenForm] = {
      for {
        clientId <- (__ \ "client_id").read[String]
        clientSecret <- (__ \ "client_secret").read[String]
        code <- (__ \ "code").read[String]
        redirectUri <- (__ \ "redirect_uri").readNullable[String]
        state <- (__ \ "state").readNullable[String]
      } yield AccessTokenForm(clientId, clientSecret, code, redirectUri, state)
    }

    def jsObjectAccessTokenForm(obj: io.flow.github.oauth.v0.models.AccessTokenForm): play.api.libs.json.JsObject = {
      play.api.libs.json.Json.obj(
        "client_id" -> play.api.libs.json.JsString(obj.clientId),
        "client_secret" -> play.api.libs.json.JsString(obj.clientSecret),
        "code" -> play.api.libs.json.JsString(obj.code)
      ) ++ (obj.redirectUri match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("redirect_uri" -> play.api.libs.json.JsString(x))
      }) ++
      (obj.state match {
        case None => play.api.libs.json.Json.obj()
        case Some(x) => play.api.libs.json.Json.obj("state" -> play.api.libs.json.JsString(x))
      })
    }

    implicit def jsonWritesGithubOauthAccessTokenForm: play.api.libs.json.Writes[AccessTokenForm] = {
      new play.api.libs.json.Writes[io.flow.github.oauth.v0.models.AccessTokenForm] {
        def writes(obj: io.flow.github.oauth.v0.models.AccessTokenForm) = {
          jsObjectAccessTokenForm(obj)
        }
      }
    }
  }
}

package io.flow.github.oauth.v0 {

  object Bindables {

    import play.api.mvc.{PathBindable, QueryStringBindable}

    // import models directly for backwards compatibility with prior versions of the generator
    import Core._
    import Models._

    object Core {
      implicit def pathBindableDateTimeIso8601(implicit stringBinder: QueryStringBindable[String]): PathBindable[_root_.org.joda.time.DateTime] = ApibuilderPathBindable(ApibuilderTypes.dateTimeIso8601)
      implicit def queryStringBindableDateTimeIso8601(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[_root_.org.joda.time.DateTime] = ApibuilderQueryStringBindable(ApibuilderTypes.dateTimeIso8601)

      implicit def pathBindableDateIso8601(implicit stringBinder: QueryStringBindable[String]): PathBindable[_root_.org.joda.time.LocalDate] = ApibuilderPathBindable(ApibuilderTypes.dateIso8601)
      implicit def queryStringBindableDateIso8601(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[_root_.org.joda.time.LocalDate] = ApibuilderQueryStringBindable(ApibuilderTypes.dateIso8601)
    }

    object Models {
      import io.flow.github.oauth.v0.models._

      val tokenTypeConverter: ApibuilderTypeConverter[io.flow.github.oauth.v0.models.TokenType] = new ApibuilderTypeConverter[io.flow.github.oauth.v0.models.TokenType] {
        override def convert(value: String): io.flow.github.oauth.v0.models.TokenType = io.flow.github.oauth.v0.models.TokenType(value)
        override def convert(value: io.flow.github.oauth.v0.models.TokenType): String = value.toString
        override def example: io.flow.github.oauth.v0.models.TokenType = io.flow.github.oauth.v0.models.TokenType.Bearer
        override def validValues: Seq[io.flow.github.oauth.v0.models.TokenType] = io.flow.github.oauth.v0.models.TokenType.all
      }
      implicit def pathBindableTokenType(implicit stringBinder: QueryStringBindable[String]): PathBindable[io.flow.github.oauth.v0.models.TokenType] = ApibuilderPathBindable(tokenTypeConverter)
      implicit def queryStringBindableTokenType(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[io.flow.github.oauth.v0.models.TokenType] = ApibuilderQueryStringBindable(tokenTypeConverter)
    }

    trait ApibuilderTypeConverter[T] {

      def convert(value: String): T

      def convert(value: T): String

      def example: T

      def validValues: Seq[T] = Nil

      def errorMessage(key: String, value: String, ex: java.lang.Exception): String = {
        val base = s"Invalid value '$value' for parameter '$key'. "
        validValues.toList match {
          case Nil => base + "Ex: " + convert(example)
          case values => base + ". Valid values are: " + values.mkString("'", "', '", "'")
        }
      }
    }

    object ApibuilderTypes {
      import org.joda.time.{format, DateTime, LocalDate}

      val dateTimeIso8601: ApibuilderTypeConverter[DateTime] = new ApibuilderTypeConverter[DateTime] {
        override def convert(value: String): DateTime = format.ISODateTimeFormat.dateTimeParser.parseDateTime(value)
        override def convert(value: DateTime): String = format.ISODateTimeFormat.dateTime.print(value)
        override def example: DateTime = DateTime.now
      }

      val dateIso8601: ApibuilderTypeConverter[LocalDate] = new ApibuilderTypeConverter[LocalDate] {
        override def convert(value: String): LocalDate = format.ISODateTimeFormat.yearMonthDay.parseLocalDate(value)
        override def convert(value: LocalDate): String = value.toString
        override def example: LocalDate = LocalDate.now
      }

    }

    final case class ApibuilderQueryStringBindable[T](
      converters: ApibuilderTypeConverter[T]
    ) extends QueryStringBindable[T] {

      override def bind(key: String, params: Map[String, Seq[String]]): _root_.scala.Option[_root_.scala.Either[String, T]] = {
        params.getOrElse(key, Nil).headOption.map { v =>
          try {
            Right(
              converters.convert(v)
            )
          } catch {
            case ex: java.lang.Exception => Left(
              converters.errorMessage(key, v, ex)
            )
          }
        }
      }

      override def unbind(key: String, value: T): String = {
        s"$key=${converters.convert(value)}"
      }
    }

    final case class ApibuilderPathBindable[T](
      converters: ApibuilderTypeConverter[T]
    ) extends PathBindable[T] {

      override def bind(key: String, value: String): _root_.scala.Either[String, T] = {
        try {
          Right(
            converters.convert(value)
          )
        } catch {
          case ex: java.lang.Exception => Left(
            converters.errorMessage(key, value, ex)
          )
        }
      }

      override def unbind(key: String, value: T): String = {
        converters.convert(value)
      }
    }

  }

}


package io.flow.github.oauth.v0 {

  object Constants {

    val BaseUrl = "https://github.com"
    val Namespace = "io.flow.github.oauth.v0"
    val UserAgent = "apibuilder 0.14.3 app.apibuilder.io/flow/github-oauth/0.0.8/play_2_6_client"
    val Version = "0.0.8"
    val VersionMajor = 0

  }

  class Client(
    ws: play.api.libs.ws.WSClient,
    val baseUrl: String = "https://github.com",
    auth: scala.Option[io.flow.github.oauth.v0.Authorization] = None,
    defaultHeaders: Seq[(String, String)] = Nil
  ) extends interfaces.Client {
    import io.flow.github.oauth.v0.models.json._

    private[this] val logger = play.api.Logger("io.flow.github.oauth.v0.Client")

    logger.info(s"Initializing io.flow.github.oauth.v0.Client for url $baseUrl")

    def accessTokens: AccessTokens = AccessTokens

    object AccessTokens extends AccessTokens {
      override def postAccessToken(
        accessTokenForm: io.flow.github.oauth.v0.models.AccessTokenForm,
        requestHeaders: Seq[(String, String)] = Nil
      )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.github.oauth.v0.models.AccessToken] = {
        val payload = play.api.libs.json.Json.toJson(accessTokenForm)
        println(s"payload: $payload")

        _executeRequest("POST", s"/login/oauth/access_token", body = Some(payload), requestHeaders = requestHeaders).map {
          case r if r.status == 200 => _root_.io.flow.github.oauth.v0.Client.parseJson("io.flow.github.oauth.v0.models.AccessToken", r, _.validate[io.flow.github.oauth.v0.models.AccessToken])
          case r if r.status == 401 => throw io.flow.github.oauth.v0.errors.UnitResponse(r.status)
          case r => throw io.flow.github.oauth.v0.errors.FailedRequest(r.status, s"Unsupported response code[${r.status}]. Expected: 200, 401")
        }
      }
    }

    def _requestHolder(path: String): play.api.libs.ws.WSRequest = {

      val holder = ws.url(baseUrl + path).addHttpHeaders(
        "User-Agent" -> Constants.UserAgent,
        "X-Apidoc-Version" -> Constants.Version,
        "X-Apidoc-Version-Major" -> Constants.VersionMajor.toString
      ).addHttpHeaders(defaultHeaders : _*)
      auth.fold(holder) {
        case Authorization.Basic(username, password) => {
          holder.withAuth(username, password.getOrElse(""), play.api.libs.ws.WSAuthScheme.BASIC)
        }
        case a => sys.error("Invalid authorization scheme[" + a.getClass + "]")
      }
    }

    def _logRequest(method: String, req: play.api.libs.ws.WSRequest)(implicit ec: scala.concurrent.ExecutionContext): play.api.libs.ws.WSRequest = {
      val queryComponents = for {
        (name, values) <- req.queryString
        value <- values
      } yield s"$name=$value"
      val url = s"${req.url}${queryComponents.mkString("?", "&", "")}"
      auth.fold(logger.info(s"curl -X $method $url")) { _ =>
        logger.info(s"curl -X $method -u '[REDACTED]:' $url")
      }
      req
    }

    def _executeRequest(
      method: String,
      path: String,
      queryParameters: Seq[(String, String)] = Nil,
      requestHeaders: Seq[(String, String)] = Nil,
      body: Option[play.api.libs.json.JsValue] = None
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[play.api.libs.ws.WSResponse] = {
      method.toUpperCase match {
        case "GET" => {
          _logRequest("GET", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).get()
        }
        case "POST" => {
          _logRequest("POST", _requestHolder(path).addHttpHeaders(_withJsonContentType(requestHeaders):_*).addQueryStringParameters(queryParameters:_*)).post(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "PUT" => {
          _logRequest("PUT", _requestHolder(path).addHttpHeaders(_withJsonContentType(requestHeaders):_*).addQueryStringParameters(queryParameters:_*)).put(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "PATCH" => {
          _logRequest("PATCH", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).patch(body.getOrElse(play.api.libs.json.Json.obj()))
        }
        case "DELETE" => {
          _logRequest("DELETE", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).delete()
        }
         case "HEAD" => {
          _logRequest("HEAD", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).head()
        }
         case "OPTIONS" => {
          _logRequest("OPTIONS", _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*)).options()
        }
        case _ => {
          _logRequest(method, _requestHolder(path).addHttpHeaders(requestHeaders:_*).addQueryStringParameters(queryParameters:_*))
          sys.error("Unsupported method[%s]".format(method))
        }
      }
    }

    /**
     * Adds a Content-Type: application/json header unless the specified requestHeaders
     * already contain a Content-Type header
     */
    def _withJsonContentType(headers: Seq[(String, String)]): Seq[(String, String)] = {
      headers.find { _._1.toUpperCase == "CONTENT-TYPE" } match {
        case None => headers ++ Seq(("Content-Type" -> "application/json; charset=UTF-8"))
        case Some(_) => headers
      }
    }

  }

  object Client {

    def parseJson[T](
      className: String,
      r: play.api.libs.ws.WSResponse,
      f: (play.api.libs.json.JsValue => play.api.libs.json.JsResult[T])
    ): T = {
      f(play.api.libs.json.Json.parse(r.body)) match {
        case play.api.libs.json.JsSuccess(x, _) => x
        case play.api.libs.json.JsError(errors) => {
          throw io.flow.github.oauth.v0.errors.FailedRequest(r.status, s"Invalid json for class[" + className + "]: " + errors.mkString(" "))
        }
      }
    }

  }

  sealed trait Authorization extends _root_.scala.Product with _root_.scala.Serializable
  object Authorization {
    final case class Basic(username: String, password: Option[String] = None) extends Authorization
  }

  package interfaces {

    trait Client {
      def baseUrl: String
      def accessTokens: io.flow.github.oauth.v0.AccessTokens
    }

  }

  trait AccessTokens {
    def postAccessToken(
      accessTokenForm: io.flow.github.oauth.v0.models.AccessTokenForm,
      requestHeaders: Seq[(String, String)] = Nil
    )(implicit ec: scala.concurrent.ExecutionContext): scala.concurrent.Future[io.flow.github.oauth.v0.models.AccessToken]
  }

  package errors {

    import io.flow.github.oauth.v0.models.json._

    final case class UnitResponse(status: Int) extends Exception(s"HTTP $status")

    final case class FailedRequest(responseCode: Int, message: String, requestUri: Option[_root_.java.net.URI] = None) extends _root_.java.lang.Exception(s"HTTP $responseCode: $message")

  }

}