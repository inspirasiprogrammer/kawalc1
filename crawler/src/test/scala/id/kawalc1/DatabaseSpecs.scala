package id.kawalc1

import com.typesafe.scalalogging.LazyLogging
import id.kawalc1.clients.JsonSupport
import id.kawalc1.database.TpsTables
import org.json4s.native.Serialization
import org.scalatest.{Matchers, WordSpec}
import slick.jdbc.H2Profile.api._

import scala.io.Source
import scala.language.reflectiveCalls

class DatabaseSpecs extends WordSpec with Matchers with FutureMatcher with LazyLogging with JsonSupport {
  "Database" should {
    "Store TPS results" in {
      val db        = Database.forConfig("tpsTestDatabase")
      val response  = Source.fromURL(getClass.getResource("/api/c/5.json")).mkString
      val kelurahan = Serialization.read[KelurahanOld](response)
      val tpses     = KelurahanOld.toTps(kelurahan)
      println(s"$tpses")
      val setup =
        DBIO.seq(TpsTables.tpsQuery.schema.drop, TpsTables.tpsQuery.schema.create, TpsTables.tpsQuery ++= tpses)
      try {
        db.run(setup).futureValue
        val tpses = db.run(TpsTables.tpsQuery.result).futureValue
      } finally db.close()
    }
  }
}
