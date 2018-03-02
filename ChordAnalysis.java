import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
//import java.util.Random;

public class ChordAnalysis {
    public static void main(String[] args) throws Exception {
        String block;
        double variation;
        double tonalness = 0.0;
        int total = 0;
        ArrayList variationList = new ArrayList();
        ArrayList tonalnessList = new ArrayList();
        Set<String> preventDuplicateV = new HashSet<>();
        Set<String> preventDuplicateT = new HashSet<>();

        //scanner
        Scanner in = new Scanner(new File(args [0]));

        //variation pool created
        while(in.hasNext()) {
            block = in.next();
            if (!(block.equals("_END_")) && !(block.equals("_START_"))) {
                String splitarray[] = block.split(",");
                for (int i = 0; i < splitarray.length; i++) {
                    if (splitarray[i].contains(":")) {
                        String split0 = splitarray[i].split(":")[0].trim();
                        String split1 = splitarray[i].split(":")[1].trim();
                        // int ascii = 
                        String quote = Character.toString((char) 34);
                        total++;

                        if (split0.contains(quote)) {
                            split0 = split0.replace(quote,"");
                        }
                        preventDuplicateV.add(split0);
                        preventDuplicateT.add(split1);

                        if (split0.equals("C") && split1.equals("maj")) {
                            tonalness = tonalness + 1.0;
                        } else if (split0.equals("D") && split1.equals("min")) {
                            tonalness = tonalness + 1.0;
                        } else if (split0.equals("E") && split1.equals("min")) {
                            tonalness = tonalness + 1.0;
                        } else if (split0.equals("E") && split1.equals("min")) {
                            tonalness = tonalness + 1.0;
                        } else if (split0.equals("F") && split1.equals("maj")) {
                            tonalness = tonalness + 1.0;
                        } else if (split0.equals("G") && split1.equals("7")) {
                            tonalness = tonalness + 1.0;
                        } else if (split0.equals("A") && split1.equals("min")) {
                            tonalness = tonalness + 1.0;
                        } else if (split0.equals("B") && split1.equals("dim")) {
                            tonalness = tonalness + 1.0;
                        } else if (split0.equals("D") && split1.equals("7")) {
                            tonalness = tonalness + 0.5;
                        } else if (split0.equals("E") && split1.equals("7")) {
                            tonalness = tonalness + 0.5;
                        } else if (split0.equals("F") && split1.equals("7")) {
                            tonalness = tonalness + 0.5;
                        } else if (split0.equals("G") && split1.equals("maj")) {
                            tonalness = tonalness + 0.5;
                        } else if (split0.equals("B") && split1.equals("7")) {
                            tonalness = tonalness + 0.5;
                        }

                    }
                }
            }
        }
        variationList.addAll(preventDuplicateV);
        tonalnessList.addAll(preventDuplicateT);

        System.out.println(variationList.toString());
        //System.out.println(tonalnessList.toString());

        System.out.println("Variation: " +  ( (double )variationList.size()) / total);
        System.out.println("Tonalness: " + tonalness/total);


    }
}