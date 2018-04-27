import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCPort;
import com.illposed.osc.OSCPortOut;
import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;

import java.awt.geom.Area;
import java.lang.reflect.Array;
import java.net.SocketPermission;
import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class ChordAnalysis {

    public static void main(String[] args) throws IOException {

        double vUser;
        double tUser;
        ArrayList vL = new ArrayList();
        ArrayList tL = new ArrayList();
        ArrayList<ArrayList> ultimateLable = new ArrayList<>();
        ArrayList<ArrayList> ultimatevL = new ArrayList<>();
        ArrayList<ArrayList> ultimatetL = new ArrayList<>();
        ArrayList<ArrayList> calculationV = new ArrayList<>();
        ArrayList<ArrayList> calculationT = new ArrayList<>();
        ArrayList difV = new ArrayList();
        ArrayList difT = new ArrayList();
        ArrayList difVnT = new ArrayList();
        int indVnT = 0;
        ArrayList chord = new ArrayList();
        ArrayList strA = new ArrayList();

        Scanner in = new Scanner(new File(args [0]));
        vUser = Double.parseDouble(args[1]);
        tUser = Double.parseDouble(args[2]);

        addComprehensive(in, vL, tL);
        divideIntoEights(vL,tL, ultimateLable, ultimatevL, ultimatetL);
        calculate(ultimatevL, ultimatetL, calculationV, calculationT);
        indVnT = findClosest(calculationV, calculationT, vUser, tUser, difV, difT, difVnT, indVnT);

        chord = findIntervals(ultimatevL.get(indVnT), ultimatetL.get(indVnT));

        System.out.println();
        System.out.println("User input: " + vUser + " (variation), " + tUser + " (tonalness) ");
        System.out.println();

        for (int z = 0; z < 25; z++) {
            System.out.print(ultimateLable.get(z).toString());
            System.out.print(ultimatevL.get(z).toString());
            System.out.print(ultimatetL.get(z).toString());
            System.out.print(calculationV.get(z).toString());
            System.out.print(calculationT.get(z).toString());
            System.out.println();
        }
        System.out.println();

        for (int g = 0; g < 8; g++) {
            strA.add(((ArrayList) ultimatevL.get(indVnT)).get(g).toString()
                    + ((ArrayList) ultimatetL.get(indVnT)).get(g).toString());
        }
        System.out.println((strA).toString());
        System.out.println();
        System.out.println(chord.toString());



        InetAddress localhost = InetAddress.getLocalHost();
        OSCPortOut sender = new OSCPortOut(localhost, 2112);

        for (int f = 0; f < chord.size(); f++) {
            ArrayList<Object> chord0 = new ArrayList<>();
            chord0.add(f);
            chord0.add(((ArrayList) chord.get(f)).get(0));
            chord0.add(((ArrayList)chord.get(f)).get(1));
            chord0.add(((ArrayList) chord.get(f)).get(2));
            chord0.add(((ArrayList) chord.get(f)).get(3));

            sender.send(new OSCMessage("/chord", chord0));
        }


    }
    public static void addComprehensive(Scanner in, ArrayList vL, ArrayList tL) {
        int total = 0;
        while(in.hasNext()) {
            String block = in.next();

            String splitarray[] = block.split(",");
            for (int i = 0; i < splitarray.length; i++) {
                if (splitarray[i].contains(":")) {
                    String split0 = splitarray[i].split(":")[0].trim();
                    String split1 = splitarray[i].split(":")[1].trim();
                    String quote = Character.toString((char) 34);
                    total++;
                    if (split1.charAt(0) == '7') {
                        split1 = split1.substring(0, 1);
                    }
                    if (split1.contains("h")) {
                        split1 = split1.substring(0, 4);
                    }
                    if (split1.contains("(")) {
                        split1 = split1.substring(0, 3);
                    }
                    if (split1.length() > 3 && !split1.contains("h")) {
                        split1 = split1.substring(0, 3);
                    }

                    if (split0.contains(quote)) {
                        split0 = split0.replace(quote, "");
                    }
                    vL.add(split0);
                    tL.add(split1);
                }
            }
        }
    }

    public static void divideIntoEights(ArrayList vL, ArrayList tL, ArrayList uL, ArrayList uVL, ArrayList uTL) {


        int num = 0;
        if (vL.size() % 8 == 0) {
            num = vL.size() / 8;
        } else {
            num = vL.size() / 8 + 1;
        }

        int i = 0;
        while (i < num -1) {
            uL.add(new ArrayList<>());
            ((ArrayList) uL.get(i)).add(i * 8 + "-" + (i * 8 + 7));
            uVL.add(new ArrayList<>());
            uTL.add(new ArrayList<>());

            for (int j = 0; j < 8; j++) {
                ((ArrayList) uVL.get(i)).add(vL.get(i * 8 + j));
                ((ArrayList) uTL.get(i)).add(tL.get(i * 8 + j));
            }
            i++;
        }

        uL.add(new ArrayList<>());
        ((ArrayList) uL.get(i)).add(i * 8 + "-" + (i * 8 + (vL.size() - i * 8)));
        uVL.add(new ArrayList<>());
        uTL.add(new ArrayList<>());


        for (int k = (num - 1) * 8; k < vL.size(); k++) {
            ((ArrayList) uVL.get(num - 1)).add(vL.get(k));
            ((ArrayList) uTL.get(num - 1)).add(tL.get(k));
        }


    }

    public static void calculate(ArrayList uVL, ArrayList uTL, ArrayList calculationV, ArrayList calculationT) {

        for (int l = 0; l < uVL.size(); l++) {
            calculationV.add(new ArrayList<>());
            calculationT.add(new ArrayList<>());
            Set<String> calculateV = new HashSet<>();
            double tonalness = 0.0;

            for (int m = 0; m < ((ArrayList) uVL.get(l)).size(); m++) {
                String v = (((ArrayList) uVL.get(l)).get(m)).toString();
                String t = (((ArrayList) uTL.get(l)).get(m)).toString();

                calculateV.add(v);

                if (v.equals("C") && t.equals("maj")) {
                    tonalness = tonalness + 1.0;
                } else if (v.equals("D") && t.equals("min")) {
                    tonalness = tonalness + 1.0;
                } else if (v.equals("E") && t.equals("min")) {
                    tonalness = tonalness + 1.0;
                } else if (v.equals("E") && t.equals("min")) {
                    tonalness = tonalness + 1.0;
                } else if (v.equals("F") && t.equals("maj")) {
                    tonalness = tonalness + 1.0;
                } else if (v.equals("G") && t.equals("7")) {
                    tonalness = tonalness + 1.0;
                } else if (v.equals("A") && t.equals("min")) {
                    tonalness = tonalness + 1.0;
                } else if (v.equals("B") && t.equals("dim")) {
                    tonalness = tonalness + 1.0;
                } else if (v.equals("D") && t.equals("7")) {
                    tonalness = tonalness + 0.5;
                } else if (v.equals("E") && t.equals("7")) {
                    tonalness = tonalness + 0.5;
                } else if (v.equals("F") && t.equals("7")) {
                    tonalness = tonalness + 0.5;
                } else if (v.equals("G") && t.equals("maj")) {
                    tonalness = tonalness + 0.5;
                } else if (v.equals("B") && t.equals("7")) {
                    tonalness = tonalness + 0.5;
                }
            }
            ((ArrayList) calculationV.get(l)).add((double) calculateV.size()/((ArrayList) uVL.get(l)).size());
            ((ArrayList) calculationT.get(l)).add(tonalness/((ArrayList) uVL.get(l)).size());
        }
    }
    public static int findClosest(ArrayList cV, ArrayList cT, double variationUser, double tonalnessUser,
                                   ArrayList difV, ArrayList difT, ArrayList difVnT, int indVnT) {

        for (int n = 0; n < cV.size(); n++) {
            difV.add(Math.abs(variationUser - ((double) ((ArrayList) (cV.get(n))).get(0))));
            difT.add(Math.abs(tonalnessUser - ((double) ((ArrayList) (cT.get(n))).get(0))));
            difVnT.add((double) difV.get(n) + (double)difT.get(n));
        }

        for (int o = 1; o < difVnT.size() - 1; o++) {
            if ((double) difVnT.get(indVnT) >= (double) difVnT.get(o)) {
                indVnT = o;
            }
        }
        return indVnT;
    }
    public static ArrayList findIntervals(ArrayList uvl, ArrayList utl) {
        ArrayList intervals = new ArrayList();
        int a = 0;
        int b = 0;
        int c = 0;
        int d = 0;

        for (int v = 0; v < uvl.size(); v++) {
            if ((uvl.get(v).toString()).equals("C") || (uvl.get(v).toString()).equals("B#")) {
                a = 60;
            } else if ((uvl.get(v).toString()).equals("C#") || (uvl.get(v).toString()).equals("Db")) {
                a = 61;
            } else if ((uvl.get(v).toString()).equals("D")) {
                a = 62;
            } else if ((uvl.get(v).toString()).equals("D#") || (uvl.get(v).toString()).equals("Eb")) {
                a = 63;
            } else if ((uvl.get(v).toString()).equals("E") || (uvl.get(v).toString()).equals("Fb")) {
                a = 64;
            } else if ((uvl.get(v).toString()).equals("F") || (uvl.get(v).toString()).equals("E#")) {
                a = 65;
            } else if ((uvl.get(v).toString()).equals("F#") || (uvl.get(v).toString()).equals("Gb")) {
                a = 66;
            } else if ((uvl.get(v).toString()).equals("G")) {
                a = 67;
            } else if ((uvl.get(v).toString()).equals("G#") || (uvl.get(v).toString()).equals("Ab")) {
                a = 68;
            } else if ((uvl.get(v).toString()).equals("A")) {
                a = 69;
            } else if ((uvl.get(v).toString()).equals("A#") || (uvl.get(v).toString()).equals("Bb")) {
                a = 70;
            } else if ((uvl.get(v).toString()).equals("B") || (uvl.get(v).toString()).equals("Cb")) {
                a = 71;
            }

            if ((utl.get(v).toString()).equals("maj")) {
                b = a + 4;
                c = a + 7;
                d = a + 12;
            } else if ((utl.get(v).toString()).equals("min")) {
                b = a + 3;
                c = a + 7;
                d = a + 12;
            } else if ((utl.get(v).toString()).equals("dim")) {
                b = a + 3;
                c = a + 6;
                d = a + 12;
            } else if ((utl.get(v).toString()).equals("aug")) {
                b = a + 4;
                c = a + 8;
                d = a + 12;
            } else if ((utl.get(v).toString()).equals("Maj7")) {
                b = a + 4;
                c = a + 7;
                d = a + 11;
            } else if ((utl.get(v).toString()).equals("min7")) {
                b = a + 3;
                c = a + 7;
                d = a + 10;
            } else if ((utl.get(v).toString()).equals("7")) {
                b = a + 4;
                c = a + 7;
                d = a + 10;
            } else if ((utl.get(v).toString()).equals("dim7")) {
                b = a + 3;
                c = a + 6;
                d = a + 10;
            } else if ((utl.get(v).toString()).equals("dim6")) {
                b = a + 3;
                c = a + 6;
                d = a + 9;
            } else if ((utl.get(v).toString()).equals("sus")) {
                b = a + 5;
                c = a + 7;
                d = a + 13;
            } else if ((utl.get(v).toString()).equals("hdim")) {
                b = a + 3;
                c = a + 6;
                d = a + 10;
            } else if ((utl.get(v).toString()).equals("9")) {
                b = a + 4;
                c = a + 7;
                d = a + 11;
            }
            intervals.add(new ArrayList<>());
            ((ArrayList) intervals.get(v)).add(a);
            ((ArrayList) intervals.get(v)).add(b);
            ((ArrayList) intervals.get(v)).add(c);
            ((ArrayList) intervals.get(v)).add(d);
        }
        return intervals;
    }
}
