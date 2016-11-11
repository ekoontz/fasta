object overlap {
  def usage() {
    println("usage: overlap <input file>")
  }

  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      usage()
      System.exit(1)
    }

    // Load reads from filename given in first supplied argument into
    // a map from labels (e.g. "Rosalind_1836") to reads: strings made
    // of characters from the set: {A,C,G,T}.
    val reads = read_input(args(0))

    // Create a map from labels to a pair:[l,at], where _l_ is the
    // overlapping label and _at_ is the position in the first label
    // where the overlap starts.
    val overlaps = find_overlaps(reads)

    // Find the first read in the string from the above map.
    var current_label = find_leftmost_read(overlaps)

    // Print out the entire string, starting with the first read.
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
    print("\n")
    System.exit(0)
  }

  // read input from file named _filename_ into a map from
  // a label (e.g. "Rosalind_1280") to a 'read':
  // a string made of characters from the set: {A,C,G,T}.
  def read_input(input_filename: String): Map[String,String] = {
    System.err.println("reading from input file:" + input_filename)
    val contents = scala.io.Source.fromFile(input_filename).mkString
    System.err.println(" length of file:" + contents.length)

    // regex to extract label of the read.
    // e.g match ">Rosalind_1836" and extract "Rosalind_1836".
    val delimiter_pattern = """^>(.*)""".r 

    // we'll return this map after populating it with the input file's contents.
    var reads = Map[String,String]()

    var current_label = ""
    var current_read = ""
    // read file contents one line at a time.
    val lines = contents.split("\n")
    val it = lines.iterator
    while(it.hasNext) {
      var line = it.next()
      line match {
        case delimiter_pattern(label) => {
          if (current_label != "") {
            // done with the previous read, so save it to the _reads_ map.
            reads += (current_label -> current_read)
          }
          current_label = label
          current_read = ""
        }
        case _ => {
          // not a delimiter: concatenate this line to the current label's read.
          current_read += line
        }
      }
    }
    // save the final read in the file in the returned map.
    reads += (current_label -> current_read)

    return reads
  }

  def find_overlaps(reads:Map[String,String]):Map[String,(Integer,String)] = {
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
    return overlaps
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
    // in this case, left_read is located at the end of the entire string and has no
    // string overlapping its right side.
    return (0,"")
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

  def find_leftmost_read(overlaps:Map[String,(Integer,String)]):String = {
    // find the leftmost of the reads: the one which does not overlap
    // any other read's right side right side.
    var first_in_string = overlaps.keys.toSet
    var overlap_i = overlaps.keys.iterator
    while(overlap_i.hasNext) {
      val left_label = overlap_i.next
      val (_,right_label) = overlaps(left_label)
      first_in_string = first_in_string - right_label
    }

    // there should be a single label left in the set: first_in_string.
    if (first_in_string.size != 1) {
      System.err.println(" Error: no unique first read.")
      System.exit(1)
    }
    return first_in_string.iterator.next
  }
}
