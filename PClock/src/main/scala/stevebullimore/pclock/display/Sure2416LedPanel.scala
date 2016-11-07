package stevebullimore.pclock.display

import Sure2416LedPanel._

class Sure2416LedPanel(panelNum: Int) {
  private val xStart = panelNum * width;
  private val xEnd = xStart + width - 1;

  def computeFrame(pixels: List[(Int, Int)]): Array[Byte] = {
    val bytes: Array[Byte] = new Array[Byte](((width * height) / 8) + 2)
    bytes(0) = writeCommand
    pixels.foreach { case (x, y) => // side effects to the bytes() array..
      if (x >= xStart && x <= xEnd && y>=0 && y < height) {
        val bitOffset = 2 + y + ((x - xStart) * height)
        val byteIndex = 1 + bitOffset / 8
        val bitIndex = bitOffset % 8;
        bytes(byteIndex) = (bytes(byteIndex) | (0x80 >> bitIndex)).toByte
        // copy 1st set of pixels to end to cater for extra bits wrapping
        if (byteIndex == 1) 
          bytes(49) = (bytes(49) | (0x80 >> bitIndex)).toByte
      }
    }

    bytes
  }
}

object Sure2416LedPanel {
  val width = 24
  val height = 16
  val writeCommand: Byte = -96
}