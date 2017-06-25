import com.datastax.driver.core.Cluster

sealed trait Event
object Click extends Event
object Impression extends Event

object Event {
  def apply(s: String) = s match {
    case "impression" => Impression
    case "click" => Click
    case _ => throw new IllegalArgumentException("No such event: " + s)
  }
}

trait Repository {
  case class Stats(users: Long, clicks: Long, impressions: Long)

  def insert(timestamp: Long, user: String, event: Event)
  def scan(timestamp: Long): Stats
}

class RepositoryImpl(host: String) extends Repository {
  val cluster = Cluster.builder.addContactPoint(host).build()

  val HOUR = 1000 * 60 * 60

  override def insert(timestamp: Long, user: String, eventType: Event): Unit = {
    val session = cluster.connect

    val eventName = eventType match {
      case Click => "clicks"
      case Impression => "impressions"
    }

    val cql =
      s"UPDATE spark.events " +
      s"SET ${eventName} = ${eventName} + 1 " +
      s"WHERE user_id = '${user}' AND hour_tag = ${timestamp / HOUR}"
    session.execute(cql)
    session.close
  }

  override def scan(timestamp: Long): Stats = {
    val session = cluster.connect
    val cql =
      s"SELECT count(user_id) as users, sum(impressions) as imps, sum(clicks) as clicks " +
      s"FROM spark.events " +
      s"WHERE hour_tag = ${timestamp / HOUR}"
    val rs = session.execute(cql)
    val row = rs.one()
    val users = row.get("users", classOf[Long])
    val imps = row.get("imps", classOf[Long])
    val clicks = row.get("clicks", classOf[Long])
    session.close
    Stats(users, clicks, imps)
  }
}

object Repository {
  def apply(): Repository = new RepositoryImpl("127.0.0.1")
}
