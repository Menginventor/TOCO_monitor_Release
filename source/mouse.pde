void mouseReleased() {
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
boolean overRectC(int _x, int _y, int _w, int _h) {
  return overRect(_x - _w/2, _y-_h/2, _w, _h);
}
boolean overRect(int x, int y, int w, int h) {
  if (mouseX >= x && mouseX <= x+w && 
    mouseY >= y && mouseY <= y+h) {
    return true;
  } else {
    return false;
  }
}
