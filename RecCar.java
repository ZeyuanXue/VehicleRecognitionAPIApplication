import java.io.*;
import java.net.*;
import javax.json.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.nio.*;
import java.util.concurrent.TimeUnit;
import java.util.*;


class RecCar{
  private static final File folder = new File("./image");
  private static final String api = "https://dev.sighthoundapi.com/v1/recognition?objectType=vehicle,licenseplate";
  private static final String accessToken = "indKmTdYP7ytEPrP6hFvx4VLr59bZbuvS2ho";



  private String plate(JsonArray obj){
    if (obj==null){
      return ", ";
    }
    String out = "";
    for (int i=0; i<obj.size(); i++){
      out += obj.getJsonObject(i).getString("character");
    }
    return out+", ";
  }


  public static void main (String[] args) throws IOException{
    RecCar temp = new RecCar();
    File[] fileNames = folder.listFiles();
    BufferedWriter writer = new BufferedWriter(new FileWriter("output.csv", true));
    for (File image : fileNames){
      System.out.println(image.getName());
      if (image.getName().contains(".jpeg") ||
          image.getName().contains(".JPG") ||
          image.getName().contains(".png")){
        try {
          BufferedImage img = ImageIO.read(image);
          ByteArrayOutputStream baos = new ByteArrayOutputStream();

          if (image.getName().contains(".jpeg") || image.getName().contains(".JPG")){
            ImageIO.write(img, "JPG", baos);
          }else{
            ImageIO.write(img, "PNG", baos);
          }

          baos.flush();
          byte[] body = baos.toByteArray();
          baos.close();

          URL apiURL = new URL(api);
          HttpURLConnection connection = (HttpURLConnection) apiURL.openConnection();
          connection.setRequestProperty("Content-Type", "application/octet-stream");
          connection.setRequestProperty("X-Access-Token", accessToken);
          connection.setRequestMethod("POST");
          connection.setDoInput(true);
          connection.setDoOutput(true);
          connection.setFixedLengthStreamingMode(body.length);
          OutputStream os = connection.getOutputStream();
          os.write(body);
          os.flush();
          //TimeUnit.SECONDS.sleep(10);
          int httpCode = connection.getResponseCode();


          if ( httpCode == 200 ){
            JsonReader jReader = Json.createReader(connection.getInputStream());
            JsonObject jsonBody = jReader.readObject();
            //System.out.println(jsonBody);
            //connection.disconnect();

            JsonObject attribute = jsonBody.getJsonArray("objects").getJsonObject(0).getJsonObject("vehicleAnnotation").getJsonObject("attributes").getJsonObject("system");
            String result = image.getName()+", ";
            JsonObject temp0;
            if ((temp0 = jsonBody.getJsonArray("objects").getJsonObject(0).getJsonObject("vehicleAnnotation").getJsonObject("licenseplate"))!=null){
              result += temp.plate(temp0.getJsonObject("attributes").getJsonObject("system").getJsonArray("characters"));
            }


            result += attribute.getJsonObject("make").getString("name");
            result += ", ";
            result += attribute.getJsonObject("model").getString("name");
            result += ", ";
            result += attribute.getJsonObject("color").getString("name");
            result += ", ";
            result += attribute.getString("vehicleType");
            result += ", ";
            result += jsonBody.getJsonArray("objects").getJsonObject(0).getJsonObject("vehicleAnnotation").getJsonNumber("recognitionConfidence").toString();
            result += ", ";
            result+="\n";
            writer.append(result);
            writer.flush();
            System.out.println(result+"\n");
            connection.disconnect();


          } else {
            JsonReader jReader = Json.createReader(connection.getErrorStream());
            JsonObject jsonError = jReader.readObject();
            System.out.println(jsonError);
          }
          //TimeUnit.SECONDS.sleep(10);
          os.close();


        } catch (Exception e) {
          e.printStackTrace();
        }

      }
    }
    writer.close();
  }
}
