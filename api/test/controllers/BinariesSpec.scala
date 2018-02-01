package controllers

import java.util.UUID

import play.api.test._
import util.{DependencySpec, MockDependencyClient}

class BinariesSpec extends DependencySpec with MockDependencyClient {

  import scala.concurrent.ExecutionContext.Implicits.global

  lazy val org = createOrganization()
  lazy val binary1 = createBinary(org)()
  lazy val binary2 = createBinary(org)()

  "GET /binaries by id" in new WithServer(port=port) {
    await(
      identifiedClient().binaries.get(id = Some(binary1.id))
    ).map(_.id) must contain theSameElementsAs  (
      Seq(binary1.id)
    )

    await(
      identifiedClient().binaries.get(id = Some(UUID.randomUUID.toString))
    ).map(_.id) must be(
      Nil
    )
  }

  "GET /binaries by name" in new WithServer(port=port) {
    await(
      identifiedClient().binaries.get(name = Some(binary1.name.toString))
    ).map(_.name) must contain theSameElementsAs (
      Seq(binary1.name)
    )

    await(
      identifiedClient().binaries.get(name = Some(binary1.name.toString.toUpperCase))
    ).map(_.name) must contain theSameElementsAs (
      Seq(binary1.name)
    )

    await(
      identifiedClient().binaries.get(name = Some(UUID.randomUUID.toString))
    ) must be(
      Nil
    )
  }

  "GET /binaries/:id" in new WithServer(port=port) {
    await(identifiedClient().binaries.getById(binary1.id)).id must be(binary1.id)
    await(identifiedClient().binaries.getById(binary2.id)).id must be(binary2.id)

    expectNotFound {
      identifiedClient().binaries.getById(UUID.randomUUID.toString)
    }
  }

  "POST /binaries" in new WithServer(port=port) {
    val form = createBinaryForm(org)
    val binary = await(identifiedClient().binaries.post(form))
    binary.name must be(form.name)
  }

  "POST /binaries validates duplicate name" in new WithServer(port=port) {
    expectErrors(
      identifiedClient().binaries.post(createBinaryForm(org).copy(name = binary1.name))
    ).errors.flatMap(_.messages) must contain theSameElementsAs (
      Seq("Binary with this name already exists")
    )
  }

  "DELETE /binaries" in new WithServer(port=port) {
    val binary = createBinary(org)()
    await(
      identifiedClient().binaries.deleteById(binary.id)
    ) must be(())

    expectNotFound(
      identifiedClient().binaries.getById(binary.id)
    )

    expectNotFound(
      identifiedClient().binaries.deleteById(binary.id)
    )
  }

}
