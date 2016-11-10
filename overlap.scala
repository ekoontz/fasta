object overlap {
  def usage() {
    println("usage: overlap <input file>")
  }

  def a_then_b(a: String,b:String): Integer = {
    // Find i, if any, such that:
    // - i > 0
    // - i < a.length
    // - a[i,a.length] == b[0,a.length]
    //
    for(i <- 1 to (a.length / 2)) {
      if (a.slice(i,a.length) == b.slice(0,a.length - i)) {
        return i
      }
    }
    return 0
  }

  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      usage()
      System.exit(1)
    }

    val label2read = read_input(args(0))

    val lefts = label2read.keysIterator

    while(lefts.hasNext) {
      var first_label = lefts.next()
      val rights = label2read.keysIterator

      while(rights.hasNext) {
        val second_label = rights.next()
        val a = label2read(first_label)
        val b = label2read(second_label)

        val second_as_b =
          a_then_b(a,b)

        if (second_as_b > 0) {
          println(first_label + "@" + second_as_b + " is a prefix of " +
            second_label + ":" + 
            a.slice(second_as_b,second_as_b + 5) + " ... " +
            a.slice(a.length - 5,a.length) + " == " +
            b.slice(0,5) + " ... " +
            b.slice(a.length - second_as_b - 5,
              (a.length - second_as_b)))
        }
      }
    }
  }

  // read input from file named _filename_ into a map from
  // a label (e.g. "Rosalind_1280") to a 'read':
  // a string made of characters from the set: {A,C,G,T}.
  def read_input(input_filename: String): Map[String,String] = {
    println("reading from input file:" + input_filename)
    val contents = scala.io.Source.fromFile(input_filename).mkString
    println(" length of file:" + contents.length)

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

    return label2read

  }

}





