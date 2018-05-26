
import processing.serial.*;
PrintWriter output;
Serial Serial_port;  // Create object from Serial class
PImage img1;
int screen = 0;
PFont medium;
String connecting = "disconnected";
boolean first_packet = true;
int data_rx_timer = 0;
boolean recording = false;
int overall_index = 0;
void setup() {
  frameRate(15);
  //size(1280, 800);
  fullScreen();

  //frame.setResizable(true);

  img1 = loadImage("img1.jpg");
  img1.resize(width, img1.height*width/height);
  medium = createFont("HelveticaNeue Medium.ttf", 24);
  String os=System.getProperty("os.name");
  println(os);
  clear_buffer();
  /*
  output = createWriter("testing-####.txt"); 
  output.println("Operating_sys : "+os);
  output.flush(); // Writes the remaining data to the file
  output.close(); // Finishes the file
  */
  
}
void draw() {
  /*remove this block*/
  /*
  if (connecting.equals("disconnected")) {
    connect();
  }
  */
  /*remove this block*/
  
  if (connecting.equals("connected") && millis() - data_rx_timer>2000) {
    Serial_port.stop();
    connecting = "disconnected";
    println("disconnected");
    screen = 0;
    recording = false;
    savefile();
  } else if (connecting.equals("connected")) {
    screen = 1;
  }
  switch(screen) {
  case 0:
    homepage_draw();
    break;

  case 1:
    monitor_draw();
  }
}

void serialEvent(Serial p) { 
  data_rx_timer = millis();
  if (first_packet) {
    first_packet = false;
    clear_buffer();
    return;
  }
  String data = p.readString();
  data = data.substring(0, data.length()-2);
  int n = int(data);


  update_data(n);
} 
String [] old_port_list = Serial.list();
