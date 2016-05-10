package stevebullimore.pclock.display

import com.pi4j.io.spi.SpiChannel
import com.pi4j.io.spi.SpiDevice
import com.pi4j.io.spi.SpiFactory;

object SpiWriter {
  private val spiDevice0 = SpiFactory.getInstance(SpiChannel.CS0,
    SpiDevice.DEFAULT_SPI_SPEED, // default spi speed 1 MHz
    SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0
  private val spiDevice1 = SpiFactory.getInstance(SpiChannel.CS1,
    SpiDevice.DEFAULT_SPI_SPEED, // default spi speed 1 MHz
    SpiDevice.DEFAULT_SPI_MODE); // default spi mode 0

  def writePanel0(frame: Array[Byte]) = spiDevice0.write(frame, 0, frame.length)
  def writePanel1(frame: Array[Byte]) = spiDevice1.write(frame, 0, frame.length)
}