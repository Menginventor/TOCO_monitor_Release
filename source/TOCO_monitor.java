import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class TOCO_monitor extends PApplet {



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
public void setup() {
  frameRate(15);
  //size(1280, 800);
  

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
public void draw() {
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

public void serialEvent(Serial p) { 
  data_rx_timer = millis();
  if (first_packet) {
    first_packet = false;
    clear_buffer();
    return;
  }
  String data = p.readString();
  data = data.substring(0, data.length()-2);
  int n = PApplet.parseInt(data);


  update_data(n);
} 
String [] old_port_list = Serial.list();

public void savefile(){
  
}
public void mouseReleased() {
  switch(screen) {
  case 0:

    break;

  case 1:
    if (overRectC(150, 100, 200, 100) && !recording) {//rec
      recording = true;
      clear_buffer();
      String Y_str = str(year());
      String M_str = str(month());
      String D_str = str(day());
      String H_str = str(hour());
      String MN_str = str(minute());
      String S_str = str(second());
      String filename = "Log-"+Y_str+"-"+M_str+"-"+D_str+"-"+H_str+"-"+MN_str+"-"+S_str+"-";
      output = createWriter(filename+".txt");
    } else if (overRectC(150, 250, 200, 100)&& recording) {//stop
      //println("STOP");
      recording = false;
      output.flush(); // Writes the remaining data to the file
      output.close(); // Finishes the file
      clear_buffer();
    } else if (overRectC(150, 400, 200, 100) && !recording) {//clear
      clear_buffer();
    }
    break;
  }
}
public boolean overRectC(int _x, int _y, int _w, int _h) {
  return overRect(_x - _w/2, _y-_h/2, _w, _h);
}
public boolean overRect(int x, int y, int w, int h) {
  if (mouseX >= x && mouseX <= x+w && 
    mouseY >= y && mouseY <= y+h) {
    return true;
  } else {
    return false;
  }
}

float R_filter = 0;
float[] buffer = new float[600];
int buffer_index = 0;
public void clear_buffer() {
  buffer_index = 0;
  overall_index = 0;
  for (int i = 0; i<600; i++) {
    buffer[i] = 0;
  }
  over_all_plot = createGraphics(1180, 200);
  over_all_plot.beginDraw();
  over_all_plot.background(0xffD8D8D6, 200);
  for(int i = 0;i<200;i+= 5){
    over_all_plot.stroke(250);
    if(i%50 == 0)over_all_plot.stroke(100);
    over_all_plot.line(0,i,1180,i);
  }
  for(int i = 0;i<1180;i+= 1180/8){
    over_all_plot.stroke(250);
    over_all_plot.line(i,0,i,200);
  }
  over_all_plot.endDraw();
}
public void update_data(int raw_data) {
  //println(raw_to_mmhg(float(raw_data)));
  while (buffer_index>=600)buffer_index-=600;
  buffer[buffer_index] = raw_to_mmhg(PApplet.parseFloat(raw_data));
  buffer_index++;
  while (buffer_index>=600)buffer_index-=600;
   over_all_plot.beginDraw();
  over_all_plot.stroke(0, 200, 0);
  int last_over_all_buffer_index = buffer_index-2;

  while (last_over_all_buffer_index<0)last_over_all_buffer_index+=600;

  over_all_plot.line(overall_index*1180/18000,200-buffer[last_over_all_buffer_index]/2,(overall_index+1)*1180/18000,200-raw_to_mmhg(PApplet.parseFloat(raw_data))/2);
  over_all_plot.endDraw();
  overall_index++;
  if (recording) {
    if (overall_index>= 18000) {
      recording = false;
      clear_buffer();
      output.flush(); // Writes the remaining data to the file
      output.close(); // Finishes the file
    } else {
      output.println(str(raw_data)+"\t"+str(raw_to_mmhg(PApplet.parseFloat(raw_data))));
    }
  }
}

public float raw_to_gram(float raw) {
  float V = raw*5.0f/1023.0f;
  float R_series = 5000.0f;
  float i = V/R_series;
  //float R_sensor = (5.0 - V)/i;
  float R_sensor_inv = i/(5.0f - V);
  float gram = (1744806.46818f*R_sensor_inv)-22.36691f;
  if (gram<0)return 0;
  return gram;
}
public float raw_to_kgf_m2(float raw) {
  float area = PI*pow(14.9f/1000.0f/2.0f, 2);
  return (raw_to_gram(raw)/1000.0f)/area;
}
public float raw_to_mmhg(float raw) {
  return  raw_to_kgf_m2(raw)/13.595f ;
}

PGraphics over_all_plot;
public void homepage_draw() {
  image(img1, 0, 0);
  float dialog_width = 700;
  float dialog_height = 500;
  rectMode(CENTER);
  fill(0xffD8D8D6, 200);
  noStroke();
  rect(width/2, height/2, dialog_width, dialog_height, 50);

  textFont(medium);
  fill(0xff4D4D4C);
  textSize(50);
  textAlign(CENTER, CENTER);
  text("Welcome to UTC Monitor", width/2, height/2-dialog_height/2+50);
  textSize(32);
  fill(0xff4D4D4C, abs(millis()%2000-1000)*255/750);
  if (connecting.equals("disconnected")) {

    text("Please connect your device", width/2, height/2);
    scan_port();
  } else  if (connecting.equals("connected")) {

    text("Connecting...", width/2, height/2);
  }
}
public void monitor_draw() {
  image(img1, 0, 0);
  rectMode(CENTER);
  fill(0xffD8D8D6, 150);
  if (overRectC(150, 100, 200, 100)&!recording) {//rec
    fill(255, 180);
  }
  noStroke();
  float btn_width = 200, btn_height = 100;

  rect(150, 100, btn_width, btn_height);
  fill(255, 0, 0, 150);
  if (overRectC(150, 100, 200, 100)&!recording) {//rec
    fill(255, 0, 0, 255);
  }
  if (!recording || millis()%1000>500)ellipse(80, 100, 30, 30);
  textSize(50);
  textAlign(LEFT, CENTER);
  text("REC", 115, 95);

  fill(0xffD8D8D6, 150);
  if (overRectC(150, 250, 200, 100)&recording) {//stop
    fill(255, 180);
  }
  rect(150, 250, btn_width, btn_height);
  fill(0, 150);
  rect(80, 250, 30, 30);
  textSize(50);
  textAlign(LEFT, CENTER);
  if (overRectC(150, 250, 200, 100) & recording) {//stop
    fill(0, 255);
  }
  text("STOP", 115, 245);

  fill(0xffD8D8D6, 150);
  if (overRectC(150, 400, 200, 100)&& !recording) {//clear
    fill(255, 180);
  }
  rect(150, 400, btn_width, btn_height);

  textSize(50);
  textAlign(LEFT, CENTER);
  fill(0, 150);
  if (overRectC(150, 400, 200, 100) && !recording) {//clear
    fill(0, 255);
  }
  text("Clear", 90, 395);

  rectMode(CORNER);
  fill(0xffD8D8D6, 200);
  rect(300, 50, 900, 400);//review monitor
  image(over_all_plot, 50, 550);
  //rect(50, 550, 1180, 200);//over all monitor


  stroke(0);
  float [] data = new float[600];
  arrayCopy(buffer, data);
  int index = buffer_index-1;
  while (index<0)index+=600;
  float last_val = data[index];

  for (int i = 50; i<450; i+= 10) {
    stroke(250);
    if ((i-50)%100 == 0)stroke(100);
    line(300, i, 1200, i);
  }
  stroke(0, 200, 0);
  for (int i = 1; i<600; i++) {
    index--;
    while (index<0)index+=600;
    line(300+(i-1)*900.0f/600.0f, 450-last_val, 300+i*900.0f/600.0f, 450-data[index]);
    last_val = data[index];
  }

 
  
}

public void connect() {
  String [] new_port_list = Serial.list();
  for (int i = 0; i<new_port_list.length; i++ ) {
    if (new_port_list[i].equals("/dev/cu.SLAB_USBtoUART")  == true) {
      try {
        Serial_port = new Serial(this, new_port_list[i], 9600);
        Serial_port.bufferUntil('\n');
        first_packet = true;
      }
      catch(Exception e) {
        return;
      }
      connecting = "connected";
      data_rx_timer = millis();
      break;
    }
  }
}
public void scan_port() {



  String [] new_port_list = Serial.list();
  if (old_port_list.length != new_port_list.length) {
    if (old_port_list.length < new_port_list.length)
      println("Plug device");
    if (connecting.equals("disconnected")) {
      for (int i = 0; i<new_port_list.length; i++ ) {
        boolean new_port = true;
        for (int j = 0; j<old_port_list.length; j++ ) {
          if (new_port_list[i].equals(old_port_list[j])  == true) {
            new_port = false;
            break;
          }
        }
        if (new_port) {
          if (!new_port_list[i].substring(0, 8).equals("/dev/tty")) {//for mac osx
            println(new_port_list[i]) ;
            try {
              Serial_port = new Serial(this, new_port_list[i], 9600);
              Serial_port.bufferUntil('\n');
              first_packet = true;
            }
            catch(Exception e) {
              return;
            }
            connecting = "connected";
            data_rx_timer = millis();
            break;
          }
        }
      }
    }
  }
  old_port_list = Serial.list();
}

  public void settings() {  fullScreen(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "TOCO_monitor" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
