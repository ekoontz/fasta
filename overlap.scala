object overlap {
  def usage() {
    println("usage: overlap <input file>")
  }

  def left_right_overlap(left: String,right:String): Integer = {
    // Find i, if any, such that:
    // - i > 0
    // - i < left.length
    // - the substring of _left_ which begins at i is equal
    //   (characterwise) to the substring of _right_ that begins at at
    //   the beginning of _right_ and is as long as the aforementioned
    //   substring of _left_.  If no such i, return 0.
    // TODO: return Option[None] as is the Scala
    // convention for e.g. Map.get()
    for(i <- 1 to (left.length / 2)) {
      if (left.slice(i,left.length) == right.slice(0,left.length - i)) {
        return i
      }
    }
    return 0
  }

  def find_overlap(left_read:String,reads:Map[String,String]):(Integer,String) = {
    val rights = reads.keysIterator
    while(rights.hasNext) {
      val right_label = rights.next
      val right_read = reads(right_label)

      val at = left_right_overlap(left_read,right_read)

      if (at > 0) {
        // we found the overlap.
        return (at,right_label)
      }
    }
    return (0,"") // TODO: throw exception
  }

  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      usage()
      System.exit(1)
    }

    // load reads into a map from labels (e.g. "Rosalind_1836") to 
    // reads: strings made of characters from the set: {A,C,G,T}.
    val reads = read_input(args(0))
    var overlaps = Map[String,(Integer,String)]()

    val lefts = reads.keysIterator
    while(lefts.hasNext) {
      var left_label = lefts.next
      val left_read = reads(left_label)

      val (at,right_label) = find_overlap(left_read,reads)
      if (at != 0) {
        overlaps += (left_label -> (at,right_label))
      }
    }

    // find the first of the reads: the one which is not a
    // right side (a value of any key in prefixes)
    var first_in_string = overlaps.keys.toSet
    var overlap_i = overlaps.keys.iterator
    while(overlap_i.hasNext) {
      val left_label = overlap_i.next
      val (_,right_label) = overlaps(left_label)
      first_in_string = first_in_string - right_label
    }

    // there should be a single label left in the set: first_in_string.
    if (first_in_string.size == 1) {
      var current_label = first_in_string.iterator.next
      while (current_label != null) {
        val left = reads(current_label)
        val (at,next_label) = overlaps.getOrElse(current_label,(null,null))
        current_label = next_label
        if (next_label == null) {
          // we've reached the last read of the string: print the entire last read, not just a prefix of it.
          print(left)
        } else {
          print(left.slice(0,at))
        }
      }
      // at the end of the string, so print a carriage return
      print("\n")
    } else {
      System.err.println(" Error: no unique first read.")
    }
  }

  // read input from file named _filename_ into a map from
  // a label (e.g. "Rosalind_1280") to a 'read':
  // a string made of characters from the set: {A,C,G,T}.
  def read_input(input_filename: String): Map[String,String] = {
    System.err.println("reading from input file:" + input_filename)
    val contents = scala.io.Source.fromFile(input_filename).mkString
    System.err.println(" length of file:" + contents.length)

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

  def log_finding(first_label:String, right_label:String,
    a:String, b:String, at: Integer) {
    val show_this_many_chars = 10
    System.err.println(first_label + "@" + at + " is a prefix of " +
      right_label + ": " +
      a.slice(at,at + show_this_many_chars) + "..." +
      a.slice(a.length - show_this_many_chars,a.length) + " == " +
      b.slice(0,show_this_many_chars) + "..." +
      b.slice(a.length - at - show_this_many_chars,
        (a.length - at)))
  }
}
