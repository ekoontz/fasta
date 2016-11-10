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
    val delimiter_pattern = """^>(.*)""".r
    var label2read = Map[String,String]()
    var current_label = ""

    while(it.hasNext) {
      var line = it.next()
      line match {
        case delimiter_pattern(label) => {
          label2read = label2read + (label -> "")
          current_label = label
        }
        case _ => {
          label2read = label2read + (current_label -> (label2read(current_label) + line))
        }
      }
    }

    val keys_of_reads = label2read.keysIterator
    while(keys_of_reads.hasNext) {
      val key = keys_of_reads.next()
      println("key: " + key)
      println("val:" + label2read(key))
    }
  }
}




