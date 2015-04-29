# Compass

This app represents my first foray into both Java programming and Android development. 
It's a compass that gives you the option to select your heading. I use it when I take the 
train downtown and need to get my bearings after coming out of the underground station.

I implemented some dynamics that (optionally) run on top of the raw sensor data. Real compasses
wobble a bit when you're moving around. This is modelled as a damped harmonic oscillator. 


## TODO

* Show heading on main screen (else user might forget and assume "North")
* Improve user-friendliness of custom dynamics selection
* Launcher icon
* Compass background
* When the phone is near vertical, it should have more intuitive behavior.
