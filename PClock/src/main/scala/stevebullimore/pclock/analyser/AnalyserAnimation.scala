package stevebullimore.pclock.analyser

import akka.actor.Actor
import ddf.minim.analysis.FFT
import ddf.minim.Minim
import stevebullimore.pclock.animsup.messages._
import stevebullimore.pclock.display.Digit3x5

class AnalyserAnimation extends Actor {
  import AnalyserAnimation._
  
  private val minim = new Minim(this);
  private val in = minim.getLineIn(Minim.STEREO, 1024);
  private val fft = new FFT(in.bufferSize(), in.sampleRate());
  fft.logAverages(lowOctaveCenterFreq, bandsPerOctave);

	override def receive = animate(Array.fill(48)(16), 0)

	private def animate(peaks: Array[Int], peakUpdate: Int): Receive = {
	  case AnimationInit(_, _) =>
      context.become(animate(Array.fill(48)(16), 0))
      sender() ! Frame(List())

    case Animate(time) =>
      fft.forward(in.mix)
      val levels = (0 to 47).map { band => Math.max(16 - (fft.getAvg(band) * ((band + 1).toFloat / 3)).toInt, 0) }
      val newPeaks = peaks.zipWithIndex.map { case (peak, band) => 
        if (levels(band) < peak) levels(band) 
        else if (peakUpdate == peakUpdateSpeed && peak < 16) peak + 1
        else peak
      }
      val seperator = if (time.getMillisOfSecond > 500) List((23, 1), (23, 3)) else List()
      
      sender() ! Frame(
          levels.zipWithIndex.foldLeft(List[(Int, Int)]()) { (pixels, level) => pixels ++ drawBand(level._1, level._2) } ++
          newPeaks.zipWithIndex.map { case (peak, band) => (band, peak) } ++
          Digit3x5.draw(15, 0, time.getHourOfDay / 10) ++
          Digit3x5.draw(19, 0, time.getHourOfDay % 10) ++
          Digit3x5.draw(25, 0, time.getMinuteOfHour / 10) ++
          Digit3x5.draw(29, 0, time.getMinuteOfHour % 10) ++
          seperator
          )
      
      context.become(animate(newPeaks, if (peakUpdate == peakUpdateSpeed) 0 else peakUpdate + 1))
	}
	
	private def drawBand(level: Int, band: Int): List[(Int, Int)] = {
	  def drawLine(x: Int, y: Int, pixels: List[(Int, Int)]): List[(Int, Int)] = {
	    if (y == 16) pixels
	    else drawLine(x, y + 1, (x, y) :: pixels)
	  }
	  drawLine(band, level, List())
	}
}

object AnalyserAnimation {
  val lowOctaveCenterFreq = 300
  val bandsPerOctave = 7
  val peakUpdateSpeed = 4
}