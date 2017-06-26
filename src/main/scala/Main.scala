object Main {
  def main(args: Array[String]): Unit = {
    val repo = Repository()
    val server = new Server(repo, 8080)
    server.start()
  }
}
