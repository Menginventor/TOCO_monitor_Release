void connect() {
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
void scan_port() {



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
