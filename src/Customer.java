import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.Scanner;

import java.util.ArrayList;
import java.util.Random;

import static java.lang.Math.abs;

public class Customer extends Agent {
    private int Rooms, Size, Pet, Floor, Elev, Center, Price;
    private ArrayList<String> protimiseis;
    private ArrayList<Integer> vari; //poso simantiko thewrei o pelatis to kathe kritirio

    protected void setup() {

        int minPrice = 300, maxPrice = 375, minRooms = 1, maxRooms = 3, minFloor = 0, maxFloor = 3, minSize = 45, maxSize = 100;
        ArrayList<ArrayList<Float>> normHouses;
        normHouses = new ArrayList<>();
        ArrayList<ArrayList<String>> Houses;
        Houses = new ArrayList<>();

        Random rand = new Random();
        vari = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            vari.add(rand.nextInt(10));
        }


        Rooms = rand.nextInt((3 - 1) + 1) + 1;
        Size = rand.nextInt((65 - 45) + 1) + 45;
        Pet = rand.nextInt(2);
        Floor = rand.nextInt(4);
        Elev = rand.nextInt(2);
        Center = rand.nextInt(2);
        Price = rand.nextInt((375 - 300) + 1) + 300;

        protimiseis = new ArrayList<>();
        protimiseis.add(String.valueOf(Rooms));
        protimiseis.add(String.valueOf(Size));
        protimiseis.add(String.valueOf(Pet));
        protimiseis.add(String.valueOf(Floor));
        protimiseis.add(String.valueOf(Elev));
        protimiseis.add(String.valueOf(Center));
        protimiseis.add(String.valueOf(Price));
        protimiseis.add(getLocalName());


        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        msg.addReceiver(new AID("Mesitis", AID.ISLOCALNAME));
        msg.setConversationId("2000");
        msg.setContent(protimiseis.toString());

        send(msg);

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    int counter = 0;
                    String temp = msg.getContent().replace("[", "");
                    System.out.println(getLocalName() + " :" + temp);
                    temp = temp.replace("]", "");
                    String[] tempList = temp.split(",");

                    for (int i = 0; i < tempList.length / 8; i++) {
                        ArrayList<String> tempDB = new ArrayList<>();
                        for (int j = 0; j < 8; j++) {
                            tempList[counter] = tempList[counter].replace(" ", "");
                            tempDB.add(tempList[counter]);
                            counter++;
                        }
                        ArrayList<Float> tempNormDB = new ArrayList<>();
                        tempNormDB.add((Float.valueOf(tempDB.get(0)) - minRooms) / (maxRooms - minRooms));
                        tempNormDB.add((Float.valueOf(tempDB.get(1)) - minSize) / (maxSize - minSize));
                        tempNormDB.add((Float.valueOf(tempDB.get(2))));
                        tempNormDB.add((Float.valueOf(tempDB.get(3)) - minFloor) / (maxFloor - minFloor));
                        tempNormDB.add((Float.valueOf(tempDB.get(4))));
                        tempNormDB.add((Float.valueOf(tempDB.get(5))));
                        tempNormDB.add((Float.valueOf(tempDB.get(6)) - minPrice) / (maxPrice - minPrice));
                        tempNormDB.add(Float.valueOf(tempDB.get(7)));
                        normHouses.add(tempNormDB);
                        Houses.add(tempDB);

                    }
                    float minDist = 999999;
                    float minID = 999999;
                    ArrayList<Float> normProtim = new ArrayList<>();
                    normProtim.add((Float.valueOf(Rooms) - minRooms) / (maxRooms - minRooms));
                    normProtim.add((Float.valueOf(Size) - minSize) / (maxSize - minSize));
                    normProtim.add((Float.valueOf(Pet)));
                    normProtim.add((Float.valueOf(Floor) - minFloor) / (maxFloor - minFloor));
                    normProtim.add((Float.valueOf(Elev)));
                    normProtim.add((Float.valueOf(Center)));
                    normProtim.add((Float.valueOf(Price) - minPrice) / (maxPrice - minPrice));
                    for (int i = 0; i < normHouses.size(); i++) {

                        float distance = abs(normProtim.get(0) - normHouses.get(i).get(0)) * vari.get(0);
                        distance += abs(normProtim.get(1) - normHouses.get(i).get(1)) * vari.get(1);
                        distance += abs(normProtim.get(2) - normHouses.get(i).get(2)) * vari.get(2);
                        distance += abs(normProtim.get(3) - normHouses.get(i).get(3)) * vari.get(3);
                        distance += abs(normProtim.get(4) - normHouses.get(i).get(4)) * vari.get(4);
                        distance += abs(normProtim.get(5) - normHouses.get(i).get(5)) * vari.get(5);
                        distance += abs(normProtim.get(6) - normHouses.get(i).get(6)) * vari.get(6);
                        if (distance < minDist) {
                            minDist = distance;
                            minID = normHouses.get(i).get(7);
                        }

                    }
                    String epilogi = getLocalName() + "-" + minID;
                    ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
                    msg2.addReceiver(new AID("Mesitis", AID.ISLOCALNAME));
                    msg2.setContent(epilogi);
                    msg2.setConversationId("3000");
                    send(msg2);
                }
            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate ms = MessageTemplate.MatchConversationId("4000");
                ACLMessage msg = receive(ms);
                if (msg != null) {
                    int limit = rand.nextInt((50 - 10) + 1) + 10;

                }
            }


        });
    }


}
