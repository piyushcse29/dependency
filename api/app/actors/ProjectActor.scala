package io.flow.dependency.actors

import io.flow.dependency.api.lib._
import io.flow.dependency.v0.models._
import io.flow.postgresql.Pager
import io.flow.play.actors.ErrorHandler
import io.flow.play.util.Config
import db._
import play.api.Logger
import play.libs.Akka
import akka.actor.{Actor, ActorSystem}
import io.flow.github.v0.models.HookForm
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext

object ProjectActor {

  trait Message

  object Messages {

    case object Deleted extends Message
    case object CreateHooks extends Message
    case object Sync extends Message
    case object SyncCompleted extends Message

    case class ProjectLibraryCreated(id: String) extends Message
    case class ProjectLibrarySync(id: String) extends Message
    case class ProjectLibraryDeleted(id: String, version: String) extends Message

    case class ProjectBinaryCreated(id: String) extends Message
    case class ProjectBinarySync(id: String) extends Message
    case class ProjectBinaryDeleted(id: String, version: String) extends Message

    case class LibrarySynced(id: String) extends Message
    case class BinarySynced(id: String) extends Message
  }

  trait Factory {
    def apply(projectId: String): Actor
  }
}

class ProjectActor @javax.inject.Inject() (
  config: Config,
  actorSystem: ActorSystem,
  projectsDao: ProjectsDao,
  tokensDao: TokensDao,
  syncsDao: SyncsDao,
  projectBinariesDao:ProjectBinariesDao,
  projectLibrariesDao: ProjectLibrariesDao,
  recommendationsDao: RecommendationsDao,
  librariesDao: LibrariesDao,
  binariesDao: BinariesDao,
  usersDao: UsersDao,
  resolversDao: ResolversDao,
  wsClient: WSClient,
  @com.google.inject.assistedinject.Assisted projectId: String
) extends Actor with ErrorHandler {

  lazy val SystemUser = usersDao.systemUser

  implicit val projectExecutionContext: ExecutionContext = actorSystem.dispatchers.lookup("project-actor-context")

  private[this] val HookBaseUrl = config.requiredString("dependency.api.host") + "/webhooks/github/"
  private[this] val HookName = "web"
  private[this] val HookEvents = Seq(io.flow.github.v0.models.HookEvent.Push)

  private[this] lazy val dataProject: Option[Project] = projectsDao.findById(Authorization.All, projectId)

  def receive = {

    case m @ ProjectActor.Messages.ProjectLibraryCreated(id) => withErrorHandler(m.toString) {
      syncProjectLibrary(id)
    }

    case m @ ProjectActor.Messages.ProjectLibrarySync(id) => withErrorHandler(m.toString) {
      syncProjectLibrary(id)
    }

    case m @ ProjectActor.Messages.ProjectBinaryCreated(id) => withErrorHandler(m.toString) {
      syncProjectBinary(id)
    }

    case m @ ProjectActor.Messages.ProjectBinarySync(id) => withErrorHandler(m.toString) {
      syncProjectBinary(id)
    }

    case m @ ProjectActor.Messages.LibrarySynced(id) => withErrorHandler(m.toString) {
      dataProject.foreach { project =>
        processPendingSync(project)
      }
    }

    case m @ ProjectActor.Messages.BinarySynced(id) => withErrorHandler(m.toString) {
      dataProject.foreach { project =>
        processPendingSync(project)
      }
    }

    case m @ ProjectActor.Messages.CreateHooks => withErrorHandler(m.toString) {
      dataProject.foreach { project =>
        GithubUtil.parseUri(project.uri) match {
          case Left(error) => {
            Logger.warn(s"Project id[${project.id}] name[${project.name}]: $error")
          }
          case Right(repo) => {
            tokensDao.getCleartextGithubOauthTokenByUserId(project.user.id) match {
              case None => {
                Logger.warn(s"No oauth token for user[${project.user.id}]")
              }

              case Some(token) => {
                val client = GithubHelper.apiClient(wsClient, token)

                client.hooks.get(repo.owner, repo.project).map { hooks =>
                  val targetUrl = HookBaseUrl + project.id
                  hooks.find(_.config.url == Some(targetUrl)).getOrElse {
                    client.hooks.post(
                      owner = repo.owner,
                      repo = repo.project,
                      hookForm = HookForm(
                        name = HookName,
                        config = io.flow.github.v0.models.HookConfig(
                          url = Some(targetUrl),
                          contentType = Some("json")
                        ),
                        events = HookEvents,
                        active = true
                      )
                    ).map { hook =>
                      println("  - Project[${project.id}] hook created: " + hook)
                    }.recover {
                      case e: Throwable => {
                        Logger.error("Project[${project.id}] Error creating hook: " + e)
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    case m @ ProjectActor.Messages.Sync => withErrorHandler(m.toString) {
      dataProject.foreach { project =>
        syncsDao.recordStarted(SystemUser, "project", project.id)

        val summary = projectsDao.toSummary(project)

        GithubDependencyProviderClient.instance(wsClient, config, tokensDao, summary, project.user).dependencies(project).map { dependencies =>
          println(s" - project[${project.id}] name[${project.name}] dependencies: $dependencies")

          dependencies.binaries.map { binaries =>
            val projectBinaries = binaries.map { form =>
              projectBinariesDao.upsert(project.user, form) match {
                case Left(errors) => {
                  Logger.error(s"Project[${project.name}] id[${project.id}] Error storing binary[$form]: " + errors.mkString(", "))
                  None
                }
                case Right(projectBinary) => {
                  Some(projectBinary)
                }
              }
            }
            projectBinariesDao.setIds(project.user, project.id, projectBinaries.flatten)
          }

          dependencies.librariesAndPlugins.map { libraries =>
            val projectLibraries = libraries.map { artifact =>
              projectLibrariesDao.upsert(
                project.user,
                artifact.toProjectLibraryForm(
                  crossBuildVersion = dependencies.crossBuildVersion()
                )
              ) match {
                case Left(errors) => {
                  Logger.error(s"Project[${project.name}] id[${project.id}] Error storing artifact[$artifact]: " + errors.mkString(", "))
                  None
                }
                case Right(library) => {
                  Some(library)
                }
              }
            }
            projectLibrariesDao.setIds(project.user, project.id, projectLibraries.flatten)
          }

          recommendationsDao.sync(SystemUser, project)

          processPendingSync(project)
        }.recover {
          case e => {
            e.printStackTrace(System.err)
            Logger.error(s"Error fetching dependencies for project[${project.id}] name[${project.name}]: $e")
          }
        }
      }
    }

    case m @ ProjectActor.Messages.Deleted => withErrorHandler(m.toString) {
      dataProject.foreach { project =>
        Pager.create { offset =>
          recommendationsDao.findAll(Authorization.All, projectId = Some(project.id), offset = offset)
        }.foreach { rec =>
          recommendationsDao.delete(SystemUser, rec)
        }
      }
      context.stop(self)
    }

    case m @ ProjectActor.Messages.ProjectLibraryDeleted(id, version) => withErrorHandler(m.toString) {
      dataProject.foreach { project =>
        recommendationsDao.findAll(
          Authorization.All,
          projectId = Some(project.id),
          `type` = Some(RecommendationType.Library),
          objectId = Some(id),
          fromVersion = Some(version)
        ).foreach { rec =>
          recommendationsDao.delete(SystemUser, rec)
        }

        processPendingSync(project)
      }
    }

    case m @ ProjectActor.Messages.ProjectBinaryDeleted(id, version) => withErrorHandler(m.toString) {
      dataProject.foreach { project =>
        recommendationsDao.findAll(
          Authorization.All,
          projectId = Some(project.id),
          `type` = Some(RecommendationType.Binary),
          objectId = Some(id),
          fromVersion = Some(version)
        ).foreach { rec =>
          recommendationsDao.delete(SystemUser, rec)
        }
        processPendingSync(project)
      }
    }

    case m: Any => logUnhandledMessage(m)
  }

  /**
    * Attempts to resolve the library. If successful, sets the
    * project_libraries.library_id
    */
  def syncProjectLibrary(id: String) {
    syncsDao.withStartedAndCompleted(SystemUser, "project_library", id) {
      dataProject.foreach { project =>
        projectLibrariesDao.findById(Authorization.All, id).map { projectLibrary =>
          resolveLibrary(projectLibrary).map { lib =>
            projectLibrariesDao.setLibrary(SystemUser, projectLibrary, lib)
          }
        }
        processPendingSync(project)
      }
    }
  }

  def syncProjectBinary(id: String) {
    syncsDao.withStartedAndCompleted(SystemUser, "project_binary", id) {
      dataProject.foreach { project =>
        projectBinariesDao.findById(Authorization.All, id).map { projectBinary =>
          resolveBinary(projectBinary).map { binary =>
            projectBinariesDao.setBinary(SystemUser, projectBinary, binary)
          }
        }
        processPendingSync(project)
      }
    }
  }

  def processPendingSync(project: Project) {
    dependenciesPendingCompletion(project) match {
      case Nil => {
        println(s" -- project[${project.name}] id[${project.id}] dependencies satisfied")
        recommendationsDao.sync(SystemUser, project)
        syncsDao.recordCompleted(SystemUser, "project", project.id)
      }
      case deps => {
        println(s" -- project[${project.name}] id[${project.id}] waiting on dependencies to sync: " + deps.mkString(", "))
      }
    }
  }

  // NB: We don't return ALL dependencies
  private[this] def dependenciesPendingCompletion(project: Project): Seq[String] = {
    projectLibrariesDao.findAll(
      Authorization.All,
      projectId = Some(project.id),
      isSynced = Some(false),
      limit = None
    ).map( lib => s"Library ${lib.groupId}.${lib.artifactId}" ) ++
    projectBinariesDao.findAll(
      Authorization.All,
      projectId = Some(project.id),
      isSynced = Some(false)
    ).map( bin => s"Binary ${bin.name}" )
  }

  private[this] def resolveLibrary(projectLibrary: ProjectLibrary): Option[Library] = {
    librariesDao.findByGroupIdAndArtifactId(Authorization.All, projectLibrary.groupId, projectLibrary.artifactId) match {
      case Some(lib) => {
        Some(lib)
      }
      case None => {
        DefaultLibraryArtifactProvider().resolve(
          resolversDao = resolversDao,
          organization = projectLibrary.project.organization,
          groupId = projectLibrary.groupId,
          artifactId = projectLibrary.artifactId
        ) match {
          case None => {
            None
          }
          case Some(resolution) => {
            librariesDao.upsert(
              SystemUser,
              form = LibraryForm(
                organizationId = projectLibrary.project.organization.id,
                groupId = projectLibrary.groupId,
                artifactId = projectLibrary.artifactId,
                resolverId = resolution.resolver.id
              )
            ) match {
              case Left(errors) => {
                Logger.error(s"Project[${projectLibrary.project.id}] name[${projectLibrary.project.name}] - error upserting library: " + errors.mkString(", "))
                None
              }
              case Right(library) => {
                Some(library)
              }
            }
          }
        }
      }
    }
  }

  private[this] def resolveBinary(projectBinary: ProjectBinary): Option[Binary] = {
    BinaryType(projectBinary.name) match {
      case BinaryType.Scala | BinaryType.Sbt => {
        binariesDao.upsert(
          SystemUser,
          BinaryForm(
            organizationId = projectBinary.project.organization.id,
            name = BinaryType(projectBinary.name)
          )
        ) match {
          case Left(errors) => {
            Logger.error(s"Project[${projectBinary.project.id}] name[${projectBinary.project.name}] - error upserting binary[$projectBinary]: " + errors.mkString(", "))
            None
          }
          case Right(binary) => {
            Some(binary)
          }
        }
      }
      case BinaryType.UNDEFINED(_) => {
        Logger.warn(s"Project[${projectBinary.id}] name[${projectBinary.name}] references an unknown binary[${projectBinary.name}]")
        None
      }
    }
  }

}
