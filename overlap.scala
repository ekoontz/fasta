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
    // If none, return 0
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

    val reads = read_input(args(0))
    val lefts = reads.keysIterator

    var prefix = Map[String,String]()
    var prefix_at = Map[String,Integer]()
    var prefix_label = Map[String,String]()

    while(lefts.hasNext) {
      var first_label = lefts.next
      val a = reads(first_label)
      val rights = reads.keysIterator

      while(rights.hasNext) {
        val second_label = rights.next
        val b = reads(second_label)

        val at = a_then_b(a,b)
        val show_this_many_chars = 10

        if (at > 0) {
          println(first_label + "@" + at + " is a prefix of " +
            second_label + ": " + 
            a.slice(at,at + show_this_many_chars) + "..." +
            a.slice(a.length - show_this_many_chars,a.length) + " == " +
            b.slice(0,show_this_many_chars) + "..." +
            b.slice(a.length - at - show_this_many_chars,
              (a.length - at)))
          prefix = prefix + (a -> b)
          prefix_at = prefix_at + (first_label -> at)
          prefix_label = prefix_label + (first_label -> second_label)
        }
      }
    }

    // find the first of the reads: the one which is not a
    // right side (a value of any key in prefixes)
    var first_in_string = prefix_label.keys.toSet
    var prefix_label_i = prefix_label.keys.iterator

    while(prefix_label_i.hasNext) {
      val left_label = prefix_label_i.next
      val right_label = prefix_label(left_label)
      first_in_string = first_in_string - right_label
    }

    if (first_in_string.size == 1) {
      var current_label = first_in_string.iterator.next
      while (current_label != null) {
        print(current_label)
        current_label = prefix_label.getOrElse(current_label,null)
        if (current_label != null) {
          print(" -> ")
        }
      }
      println("")
      current_label = first_in_string.iterator.next
      while (current_label != null) {
        val left = reads(current_label)
        val at = prefix_at.get(current_label)
        if (at == None) {
          print(left + "\n")
        } else {
          print(left.slice(0,at.get))
        }
        current_label = prefix_label.getOrElse(current_label,null)
      }
    } else {
      println(" Error: no unique first read.")
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
    var reads = Map[String,String]()
    var current_label = ""

    while(it.hasNext) {
      var line = it.next()
      line match {
        case delimiter_pattern(label) => {
          reads = reads + (label -> "")
          current_label = label
        }
        case _ => {
          reads = reads + (current_label -> (reads(current_label) + line))
        }
      }
    }

    return reads

  }

}





