# PClock
A clock in scala to run on RPI driving Sure2416 LED panels via SPI

This code is inteded to be run on a Rasberry Pi to drive two Sure 2416 LED panels mounted side by side giving a display area of 48x16 pixels. It uses the pi4j java library to drive the Raspberry Pi SPI interface.

The IO connections from RPI GPIO to the Sure LED panels are as follows:

          RPI -> Sure 
    26 (CE0)  -> 2 (CS2)
    24 (CE1)  -> 3 (CS3)
    23 (SCLK) -> 5 (WR)
    19 (MOSI) -> 7 (DATA)
     6 (GND)  -> 8 (GND)
     
On the left hand LED panel, set the DIP switches so that CS3 is enabled. On the right hand LED panel set the DIP switches so that CS2 is enabled.

The clock software is written in Scala using Akka asyncronous operation and Spray for HTTP support.
There are a set of different time animations as well as a scrolling text function which can display an adhoc message or an RSS feed.

An HTTP REST interface allows control of the clock for selecting the current time animation and controlling the message/RSS display.

It provides a set of time animations which include:

1. Pong Clock (POST animationc/0)
  * A game of pong where the computer plays itself such that the score represents the current time. 
  * Each minute the right player wins advancing the minutes value. 
  * Each hour the left player wins advancing the hours value and the right player score resets to zero.
  * The speed of the ball increases during each minute of play giving a visual indication the time within the minute.

2. Simple time (POST animationc/2)
  * The current time (hours, minutes, seconds) is displayed with each change of digit scrolling up from below

3. Time and date (POST animationc/3)
  * As per simple time, but the date (day, month, year) is displayed beneath

4. Bars (POST animationc/4)
  * Three horizantal bars represent percentage of the current hour in the day, minute in the hour and second in the minute
  
5. Unix time (POST animationc/5)
  * Displays the current 32 bit unix time in decimal as the top number and the below the number of seconds remaining until the 2038 problem.
  
6. Tetris (POST animationc/6)
  * Displays the time as hours & minutes digits. 
  * Each digit update is drawn by falling tetris pieces to construct the digit

7. Blocks (POST animationc/7)
  * Displays the time as a series of blocks in an analogue clock's hours positions. 
  * Each full block represents a full hour.
  * Each block is made up of 12 pixels, so each pixel represents 5 minutes past the hour.
  
8. Blank display (POST animationc/1)
  * Displays nothing. Used for turning the clock off (see below)

9. Audio Spectrum Analyser (POST animationc/8)
  * Displays a 48 band spectrum analyser using the sound input of the PI's default sound module
  * Since the PI has no audio input a device needs to be added - for example a USB audio unit, or USB microphone etc.
  * Uses the ddf minim audio java library to perform the FFT.

10. Conways game of life (POST animation/9)
  * Displays time in top left corner with the rest of the area showing evolving cells of Conways game of life.
  * The display seeds with a random pattern every 15 seconds.
  
11. Timer (POST animation/10)
  * Displays a countdown timer.
  * The body of the request contains the duration in seconds to count down.

## REST interface
  * All POST requests require HTTP header content-type=application/json
  * Select a time animation from the above list (`POST http://<ip:8080>/animationc/<animation_number>`)
  * Display an adhoc scrolling message. This can be single shot, or repeated with a given frequency 
  (in seconds) (`POST http://<ip:8080>/animationf/0` with a json body of `{"data":"message","freq":"<seconds>"}`. freq is optional, 
  if ommitted is single shot. Once the message has reached the end of display, the current time animation is displayed.
  * Display a scrolling RSS feed subject text of the first item given an RSS feed URL. This can be single shot, or repeated with a given frequency 
  (in seconds) (`POST http://<ip:8080>/animationf/1` with a json body of `{"data":"<rss_feed_url>", "freq":"<seconds>"}`. 
  
  An example json for UK BBC news to update every 5 minutes would be {"data":"http://feeds.bbci.co.uk/news/rss.xml?edition=uk", "freq":300}
  
  freq is optional, if ommitted is single shot. Once the message has reached the end of display, the current time animation is displayed.
  * Adjust the brightness of the LED panel given a 0-100 percentage value (`POST http://<ip:8080>/brightness/<percent>`)

## Home automation - EG Amazon Echo

The REST interface can be used to control the clock from a home automation system such as Amazon Echo or Echo Dot etc. This can be achived using 
an Echo unit with a Hue Bridge (I have used the HABridge from BWS systems which I also run on the Raspberry Pi). 

This allows control of the LED display 
using voice commands such as 'Echo, Turn on pong clock', or 'Echo, turn on unix time', or 'Echo, dim unix time to 50 percent'

There is currently a blank animation on index 1 which can be used here to turn the clock off with Echo etc. So for example, set the 
'Off URL' in each device setting of the HABridge to activate the blank animation (`POST http://<ip:8080>/animationc/1`). Then you can issue 
commands such as 'Echo, turn off unix time' etc.

## Currently being developed:
  * Add a space invaders themed time animation
  * Add moon phase animation
  * Social media integration

