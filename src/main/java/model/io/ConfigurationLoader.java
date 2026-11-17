package model.io;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.regex.*;
import model.service.Simulation;
import model.domain.*;
import model.domain.Types.*;
/** Strict JSON configuration reader for the intentionally small, versioned academic schema. */
public final class ConfigurationLoader {
    private ConfigurationLoader(){}
    private static String read(Path p)throws IOException{if(!Files.isRegularFile(p))throw new IOException("Configuration missing: "+p);return Files.readString(p,StandardCharsets.UTF_8);}
    private static String field(String json,String name)throws IOException{Matcher m=Pattern.compile("\\\""+name+"\\\"\\s*:\\s*(\\\"[^\\\"]*\\\"|-?\\d+)").matcher(json);if(!m.find())throw new IOException("Missing JSON property: "+name);return m.group(1).replace("\"","");}
    private static String array(String json,String key)throws IOException{Matcher m=Pattern.compile("\\\""+key+"\\\"\\s*:\\s*\\[(.*?)\\]",Pattern.DOTALL).matcher(json);if(!m.find())throw new IOException("Missing JSON array: "+key);return m.group(1);}
    private static String[] objects(String s){return s.split("(?<=\\})\\s*,\\s*(?=\\{)");}
    public static Simulation load(Path dir)throws IOException{
        String map=read(dir.resolve("map.json"));int width=Integer.parseInt(field(map,"width")),height=Integer.parseInt(field(map,"height"));
        String[] cells=array(map,"cells").replace("\"","").replaceAll("\\s","").split(",");
        Simulation simulation=new Simulation(width,height,cells,Integer.parseInt(field(map,"center")));
        String routes=array(read(dir.resolve("routes.json")),"edges");for(String entry:objects(routes)){if(entry.isBlank())continue;simulation.connect(Integer.parseInt(field(entry,"from")),Integer.parseInt(field(entry,"to")),Double.parseDouble(field(entry,"weight")));}
        simulation.verifyMap();String vehicles=array(read(dir.resolve("vehicles.json")),"vehicles");for(String entry:objects(vehicles)){if(entry.isBlank())continue;simulation.addVehicle(new Vehicle(field(entry,"id"),VehicleType.valueOf(field(entry,"type")),Integer.parseInt(field(entry,"location"))));}return simulation;
    }
}
