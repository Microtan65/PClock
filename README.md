# PClock
A clock in scala to run on RPI driving Sure2416 LED panels via SPI

This code is inteded to be run on a Rasberry Pi to drive two Sure 2416 LED panels mounted side by side giving a display area of 48x16 pixels. 
It uses the pi4j java library to drive the Raspberry Pi SPI interface.
It is written in Scala using Akka asyncronous operation and Spray to provide an HTTP REST interface for control.

It provides a set of time animations which include:

1. Pong Clock (animationc/0)
  * A game of pong where the computer plays itself such that the score represents the current time. 
  * Each minute the right player wins advancing the minutes value. 
  * Each hour the left player wins advancing the hours value and the right player score resets to zero.
  * The speed of the ball increases during each minute of play giving a visual indication the time within the minute.

2. Simple time (animationc/2)
  * The current time (hours, minutes, seconds) is displayed with each change of digit scrolling up from below

3. Time and date (animationc/3)
  * As per simple time, but the date (day, month, year) is displayed beneath

4. Bars (animationc/4)
  * Three horizantal bars represent percentage of the current hour in the day, minute in the hour and second in the minute
  
5. Unix time (animationc/5)
  * Displays the current 32 bit unix time in decimal as the top number and the below the number of seconds remaining until the 2038 problem.
  
6. Blank display (animationc/1)
  * Displays nothing. Used for turning the clock off (see below)

## The code includes an HTTP REST interface which allows:
  * Selecting a time animation from the above list (`POST http://<ip:8080>/animationc/<animation_number>`)
  * Displaying an adhoc scrolling message. This can be single shot, or repeated with a given frequency 
  (in seconds) (`POST http://<ip:8080>/animationf/0` with a json body of `{"data":"message","freq":"<seconds>"}`. freq is optional, 
  if ommitted is single shot. Once the message has reached the end of display, the current time animation is displayed.
  * Displaying a scrolling RSS feed subject text of the first item given an RSS feed URL. This can be single shot, or repeated with a given frequency 
  (in seconds) (`POST http://<ip:8080>/animationf/1` with a json body of `{"data":"<rss_feed_url>", "freq":"<seconds>"}`. 
  freq is optional, if ommitted is single shot. Once the message has reached the end of display, the current time animation is displayed.
  * Adjusting the brightness of the LED panel given a 0-100 percentage value (`POST http://<ip:8080>/brightness/<percent>`)

## Home automation - EG Amazon Echo

The REST interface can be used to control the clock from a home automation system such as Amazon Echo or Echo Dot etc. This can be achived using 
an Echo unit with a Hue Bridge (I have used the HABridge from BWS systems which I also run on the Raspberry Pi). 

This allows control of the LED display 
using voice commands such as 'Echo, Turn on pong clock', or 'Echo, turn on unix time', or 'Echo, dim unix time to 50 percent'

There is currently a blank animation on index 1 which can be used here to turn the clock off with Echo etc. So for example, set the 
'Off URL' in each device setting of the HABridge to activate the blank animation (`POST http://<ip:8080>/animationc/1`). Then you can issue 
commands such as 'Echo, turn off unix time' etc.

## Currently being developed:
  * Add a tetris themed time animation
  * Add a space invaders themed time animation
  * Add moon phase animation
  * Add a 'block' time dial animation

