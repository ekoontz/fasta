object overlap {
  def usage() {
    println("usage: overlap <input file>")
  }

  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      usage()
      System.exit(1)
    }
    println("reading from input file:" + args(0));
    val contents = scala.io.Source.fromFile(args(0)).mkString
    println(" length of file" + contents.length)

    // split into strings
    val lines = contents.split("\n")

    val it = lines.iterator

    while(it.hasNext) {
      println("line: " + it.next())
    }

  }
}




