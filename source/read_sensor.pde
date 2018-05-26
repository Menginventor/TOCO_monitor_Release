float R_filter = 0;
float[] buffer = new float[600];
int buffer_index = 0;
void clear_buffer() {
  buffer_index = 0;
  overall_index = 0;
  for (int i = 0; i<600; i++) {
    buffer[i] = 0;
  }
  over_all_plot = createGraphics(1180, 200);
  over_all_plot.beginDraw();
  over_all_plot.background(#D8D8D6, 200);
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
void update_data(int raw_data) {
  //println(raw_to_mmhg(float(raw_data)));
  while (buffer_index>=600)buffer_index-=600;
  buffer[buffer_index] = raw_to_mmhg(float(raw_data));
  buffer_index++;
  while (buffer_index>=600)buffer_index-=600;
   over_all_plot.beginDraw();
  over_all_plot.stroke(0, 200, 0);
  int last_over_all_buffer_index = buffer_index-2;

  while (last_over_all_buffer_index<0)last_over_all_buffer_index+=600;

  over_all_plot.line(overall_index*1180/18000,200-buffer[last_over_all_buffer_index]/2,(overall_index+1)*1180/18000,200-raw_to_mmhg(float(raw_data))/2);
  over_all_plot.endDraw();
  overall_index++;
  if (recording) {
    if (overall_index>= 18000) {
      recording = false;
      clear_buffer();
      output.flush(); // Writes the remaining data to the file
      output.close(); // Finishes the file
    } else {
      output.println(str(raw_data)+"\t"+str(raw_to_mmhg(float(raw_data))));
    }
  }
}

float raw_to_gram(float raw) {
  float V = raw*5.0/1023.0;
  float R_series = 5000.0;
  float i = V/R_series;
  //float R_sensor = (5.0 - V)/i;
  float R_sensor_inv = i/(5.0 - V);
  float gram = (1744806.46818*R_sensor_inv)-22.36691;
  if (gram<0)return 0;
  return gram;
}
float raw_to_kgf_m2(float raw) {
  float area = PI*pow(14.9/1000.0/2.0, 2);
  return (raw_to_gram(raw)/1000.0)/area;
}
float raw_to_mmhg(float raw) {
  return  raw_to_kgf_m2(raw)/13.595 ;
}
