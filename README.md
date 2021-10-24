# Smart Home

Started in 2020, my goal was to create my own home automation app to control several arduinos.
The app is developed in kotlin with  and reusable code.
Through the app you can manage and create "rooms" to identify your arduino's location, within the "room" you can control and check any data shared by the arduino.
The data sharing between the app and the arduino is based on the bluetooth communication (normal bluetooth, no BLE), through a specific and standard string the app recevies the data and process it, inserting it inside the specific views.

<h4>In my case I used the app to control the blinds in my room, normal blinds but automated with arduino nano, bluetooth adapter HC-05 and stepper motors.</h4>


<img src="https://helpx.adobe.com/content/dam/help/en/photoshop/using/convert-color-image-black-white/jcr_content/main-pars/before_and_after/image-before/Landscape-Color.jpg">


<h3>Here's the other related project for the arduino in C++</h3>
https://github.com/2dadsgn/SmartHome-Arduino
