PGraphics over_all_plot;
void homepage_draw() {
  image(img1, 0, 0);
  float dialog_width = 700;
  float dialog_height = 500;
  rectMode(CENTER);
  fill(#D8D8D6, 200);
  noStroke();
  rect(width/2, height/2, dialog_width, dialog_height, 50);

  textFont(medium);
  fill(#4D4D4C);
  textSize(50);
  textAlign(CENTER, CENTER);
  text("Welcome to UTC Monitor", width/2, height/2-dialog_height/2+50);
  textSize(32);
  fill(#4D4D4C, abs(millis()%2000-1000)*255/750);
  if (connecting.equals("disconnected")) {

    text("Please connect your device", width/2, height/2);
    scan_port();
  } else  if (connecting.equals("connected")) {

    text("Connecting...", width/2, height/2);
  }
}
void monitor_draw() {
  image(img1, 0, 0);
  rectMode(CENTER);
  fill(#D8D8D6, 150);
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

  fill(#D8D8D6, 150);
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

  fill(#D8D8D6, 150);
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
  fill(#D8D8D6, 200);
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
    line(300+(i-1)*900.0/600.0, 450-last_val, 300+i*900.0/600.0, 450-data[index]);
    last_val = data[index];
  }

 
  
}
