package data;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Maria.Guseva on 17.03.2017.
 */
public class DataCollection {
    private static final String OUTFILE_PATH = System.getProperty("user.dir") + "/src/test/resources/data.txt";
    private static final String ADDRESSES_PATH = System.getProperty("user.dir") + "/src/test/resources/Addresses.txt";
    private static final String DIRECTIONS_PATH = System.getProperty("user.dir") + "/src/test/resources/Directions.txt";

    protected void startFrom(){

    }

    protected void continueUpload(){

    }

    protected static void getDirections(){
        List<String> addresses = getAddresses();
        String info = "";
        File file = new File(DIRECTIONS_PATH);
        try {
            if(!file.exists()){
                file.createNewFile();
            }
            PrintWriter out = new PrintWriter(file.getAbsoluteFile());
            try {
                for (int i = 0; i < addresses.size(); i++) {
                    for (int j = 0; j < addresses.size(); j++) {
                        if (i != j) {
                            info = addresses.get(i) + ";" + addresses.get(j);
                            out.println(info);
                        }
                    }
                }
            } finally {
                out.close();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    protected static List<String> getAddresses(){
        List<String> addresses = new ArrayList<String>();

        try{

            FileReader reader = new FileReader(ADDRESSES_PATH);
            BufferedReader in = new BufferedReader(reader);
            String address;
            while ((address = in.readLine()) != null){
                addresses.add(address);
            }
            in.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        return addresses;
    }
}
