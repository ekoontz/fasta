object overlap {
  def usage() {
    println("usage: overlap <input file>")
  }

  def main(args: Array[String]): Unit = {
    if (args.length != 1) {
      usage()
      System.exit(1)
    }

    // 1. Load sequences from filename given in first supplied argument into
    // a map from labels (e.g. "Rosalind_1836") to sequences: strings made
    // of characters from the set: {A,C,G,T}.
    val sequences = read_input(args(0))

    // 2. Find a map from labels to a pair:[l,at], where _l_ is the
    //   overlapping label and _at_ is the position in the first label
    //   where the overlap starts.  Also, find the label of the
    //   leftmost sequence in the entire string from the above
    //   map. Because it's at the far left of the entire string, it
    //   has no counterpart which overlaps with it on its left side.
    val (overlaps,leftmost_label) = find_overlaps(sequences)

    // 3. Print out the entire string, starting with the left read and
    // working right to the end of the string.
    var current_label = leftmost_label
    while (current_label != null) {
      val left = sequences(current_label)
      val (at,next_label) = overlaps.getOrElse(current_label,(null,null))
      current_label = next_label
      if (next_label == null) {
        // We've reached the last read of the string: print the entire last read, not just a prefix of it.
        print(left)
      } else {
        // Print the left side of the current read that is not shared
        // by the current read on its right side with another read.
        print(left.slice(0,at))
      }
    }
    print("\n")
    System.exit(0)
  }

  /** Returns a map from read labels (e.g. "Rosalind_128") to the read
    * for that label: a string made of characters from the set:
    * {A,C,G,T}, by reading from a file named input_filename.
    */
  def read_input(input_filename: String): Map[String,String] = {
    System.err.println("reading from input file:" + input_filename)
    val contents = scala.io.Source.fromFile(input_filename).mkString
    System.err.println(" length of file:" + contents.length)

    // regex to extract label of the read.
    // e.g match ">Rosalind_1836" and extract "Rosalind_1836".
    val delimiter_pattern = """^>(.*)""".r 

    // we'll return this map after populating it with the input file's contents.
    var sequences = Map[String,String]()

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
            // done with the previous read, so insert it into the _sequences_ map.
            sequences += (current_label -> current_read)
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
    // insert the final read in the file in the returned map.
    sequences += (current_label -> current_read)

    return sequences
  }

  /** Return a map of overlapping read pairs: the key of the map is
    * the left side of the overlap, and the value is the right side
    * of the overlap. Return this map, and also the leftmost read, for which there
    * is no corresponding left counterpart: no other read overlaps it on its left side.
    */
  def find_overlaps(sequences:Map[String,String]):(Map[String,(Integer,String)],String) = {

    // This is the map that we'll return as the map of overlapping read pairs.
    var overlaps = Map[String,(Integer,String)]()

    // This is a subset of the sequences which have not yet been added
    // as right-hand members of read pairs. After finding each
    // right-hand side of a pair, we remove that found right-hand side
    // from this set. At the end, this subset will have a single
    // member, which will be the leftmost sequence, since it was not
    // yet added as a right-hand member of any overlap pair.
    var right_hand_candidates = sequences.keys.toSet

    val lefts = sequences.keysIterator
    while(lefts.hasNext) {
      var left_label = lefts.next
      val left_read = sequences(left_label)

      val (right_label,at) = find_overlap(left_read,sequences,right_hand_candidates)
      if (at != 0) {
        // Insert a pair in the overlaps map: right_label is the
        // right-hand member of the pair whose left-hand member is
        // left_label.
        overlaps += (left_label -> (at,right_label))

        // Now that right_label has been matched as the right-hand
        // member of a pair, eliminate it from consideration as a
        // right-hand member of any other pair.
        right_hand_candidates -= right_label
      }
    }
    // There should be a single label left in the set: if not, exit
    // with an error.
    if (right_hand_candidates.size != 1) {
      System.err.println(" Error: no unique first read: size: " + right_hand_candidates.size)
      System.exit(1)
    }
    return (overlaps,right_hand_candidates.iterator.next)
  }

  /** Given a read _left_read_, return the read label of the read, if any, that overlaps
    * on the right side of _left_read_, and the right side's overlap offset (an integer).
    */
  def find_overlap(left_read:String,sequences:Map[String,String],right_hand_candidates:Set[String])
      :(String,Integer) = {
    var rights = right_hand_candidates.iterator
    while(rights.hasNext) {
      val right_label = rights.next
      val right_read = sequences(right_label)
      val at = left_right_overlap(left_read,right_read)

      if (at != 0) {
        // We found the overlap: we can return at this point and avoid
        // the time cost of looking at any other sequences in _sequences_.
        return (right_label,at)
      }
    }
    // In this case, left_read is located at the far right end of the
    // entire string and therefore has no other read overlapping on
    // its right side.
    return ("",0)
  }

  /** Return i, if any, such that:
    *  - i > 0
    *  - i < left.length
    *  - the substring of _left_ which begins at i is equal
    *    (characterwise) to the substring of _right_ that begins at
    *    the beginning of _right_ and is as long as the aforementioned
    *   substring of _left_.
    *
    *  If no such i, return 0.
    */
  def left_right_overlap(left: String,right:String): Integer = {
    for(i <- 1 to (left.length / 2)) {
      if (left.slice(i,left.length) == right.slice(0,left.length - i)) {
        return i
      }
    }
    return 0
  }
}
