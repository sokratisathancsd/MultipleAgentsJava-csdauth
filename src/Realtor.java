import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.lang.reflect.Array;
import java.util.*;

import static java.lang.Math.abs;


public class Realtor extends Agent {
    public ArrayList<ArrayList<String>> Houses, Customers, Searching;
    public Map<String, ArrayList<String>> Interested;
    public Map<String, ArrayList<String>> Rented;
    public Map<ArrayList<String>, ArrayList<String>> negotiate;
    public int flag = 0, flag2 = 0;      // Flag sees when all the houses and flag2 sees when all negotiations are over
    public int BuyCounter = 0;
    public ACLMessage flag1;

    protected void setup() {
        int minPrice = 300, maxPrice = 375, minRooms = 1, maxRooms = 3, minFloor = 0, maxFloor = 3, minSize = 45, maxSize = 100;
        ArrayList<ArrayList<Float>> normHouses;
        normHouses = new ArrayList<>();
        Houses = new ArrayList<>();
        Searching = new ArrayList<>();
        Customers = new ArrayList<>();
        negotiate = new HashMap<ArrayList<String>, ArrayList<String>>();
        Interested = new HashMap<String, ArrayList<String>>();
        Rented = new HashMap<String, ArrayList<String>>();
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate ms = MessageTemplate.MatchConversationId("1000");
                ACLMessage msg = receive(ms);

                if (msg != null) {
                    int counter = 0;
                    String temp = msg.getContent().replace("[", "");
                    temp = temp.replace("]", "");
                    String[] tempList = temp.split(",");

                    for (int i = 0; i < tempList.length / 9; i++) {
                        ArrayList<String> tempDB = new ArrayList<>();
                        for (int j = 0; j < 9; j++) {
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
                        tempNormDB.add(Float.valueOf(tempDB.get(8)));
                        normHouses.add(tempNormDB);
                        Houses.add(tempDB);
                    }


                }
            }
        });
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate ms = MessageTemplate.MatchConversationId("2000");
                ACLMessage msg = receive(ms);
                if (msg != null) {
                    int counter = 0;
                    String temp = msg.getContent().replace("[", "");
                    temp = temp.replace("]", "");
                    String[] tempList = temp.split(",");
                    ArrayList<String> tempDB = new ArrayList<>();
                    for (int j = 0; j < 8; j++) {
                        tempList[counter] = tempList[counter].replace(" ", "");
                        tempDB.add(tempList[counter]);
                        counter++;
                    }
                    Customers.add(tempDB);

                } else if (Houses.size() > 0 && flag1 == null) {
                    flag = 1;
                }
                flag1 = msg;
            }
        });
        addBehaviour(new CyclicBehaviour() {

            @Override
            public void action() {
                if (flag == 1 && flag2 == 0) {
                    if (Customers.size() > 0) {
                        Random rand = new Random();
                        ArrayList<String> chosenCust = Customers.remove(rand.nextInt(Customers.size()));
                        Searching.add(chosenCust);
                        ArrayList<Float> tempNormCust = new ArrayList<>();
                        tempNormCust.add((Float.valueOf(chosenCust.get(0)) - minRooms) / (maxRooms - minRooms));
                        tempNormCust.add((Float.valueOf(chosenCust.get(1)) - minSize) / (maxSize - minSize));
                        tempNormCust.add((Float.valueOf(chosenCust.get(2))));
                        tempNormCust.add((Float.valueOf(chosenCust.get(3)) - minFloor) / (maxFloor - minFloor));
                        tempNormCust.add((Float.valueOf(chosenCust.get(4))));
                        tempNormCust.add((Float.valueOf(chosenCust.get(5))));
                        tempNormCust.add((Float.valueOf(chosenCust.get(6)) - minPrice) / (maxPrice - minPrice));

                        float minDist1 = 999999, minDist2 = 999999, minDist3 = 999999;
                        float minID1 = 999999, minID2 = 999999, minID3 = 999999;
                        for (int i = 0; i < normHouses.size(); i++) {
                            float distance = abs(tempNormCust.get(0) - normHouses.get(i).get(0));
                            distance += abs(tempNormCust.get(1) - normHouses.get(i).get(1));
                            distance += abs(tempNormCust.get(2) - normHouses.get(i).get(2));
                            distance += abs(tempNormCust.get(3) - normHouses.get(i).get(3));
                            distance += abs(tempNormCust.get(4) - normHouses.get(i).get(4));
                            distance += abs(tempNormCust.get(5) - normHouses.get(i).get(5));
                            distance += abs(tempNormCust.get(6) - normHouses.get(i).get(6));
                            if (distance < minDist1) {
                                minDist3 = minDist2;
                                minDist2 = minDist1;
                                minID3 = minID2;
                                minID2 = minID1;
                                minDist1 = distance;
                                minID1 = normHouses.get(i).get(7);


                            } else if (distance < minDist2) {
                                minDist3 = minDist2;
                                minID3 = minID2;
                                minDist2 = distance;
                                minID2 = normHouses.get(i).get(7);

                            } else if (distance < minDist3) {
                                minDist3 = distance;
                                minID3 = normHouses.get(i).get(7);

                            }

                        }
                        ArrayList<ArrayList<String>> SentList = new ArrayList<>();

                        for (int i = 0; i < Houses.size(); i++) {
                            if (minID1 == Integer.valueOf(Houses.get(i).get(8))) {
                                ArrayList<String> tempList = new ArrayList<>();
                                for (int j = 0; j < 7; j++) {
                                    tempList.add(Houses.get(i).get(j));
                                }
                                tempList.add(Houses.get(i).get(8));
                                SentList.add(tempList);
                            }
                            if (minID2 == Integer.valueOf(Houses.get(i).get(8))) {
                                ArrayList<String> tempList = new ArrayList<>();
                                for (int j = 0; j < 7; j++) {
                                    tempList.add(Houses.get(i).get(j));
                                }
                                tempList.add(Houses.get(i).get(8));
                                SentList.add(tempList);
                            }
                            if (minID3 == Integer.valueOf(Houses.get(i).get(8))) {
                                ArrayList<String> tempList = new ArrayList<>();
                                for (int j = 0; j < 7; j++) {
                                    tempList.add(Houses.get(i).get(j));
                                }
                                tempList.add(Houses.get(i).get(8));
                                SentList.add(tempList);
                            }
                        }
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.addReceiver(new AID(chosenCust.get(7), AID.ISLOCALNAME));
                        msg.setContent(SentList.toString());
                        send(msg);

                    }

                }
            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                MessageTemplate ms = MessageTemplate.MatchConversationId("3000");
                ACLMessage msg = receive(ms);
                if (msg != null) {
                    BuyCounter++;
                    String temp[] = msg.getContent().split("-");
                    if (Interested.containsKey(temp[1])) {
                        Interested.get(temp[1]).add(temp[0]);
                    } else {
                        ArrayList<String> temporary = new ArrayList<>();
                        temporary.add(temp[0]);
                        Interested.put(temp[1], temporary);
                    }

                    System.out.println(Interested);
                }

                if (BuyCounter == Searching.size() && Customers.isEmpty()) {
                    flag2 = 1;
                    for (Map.Entry<String, ArrayList<String>> entry : Interested.entrySet()) {
                        if (entry.getValue().size() == 1) {
                            //Rented.put(entry.getValue(),entry.getKey())
                            for (int i = 0; i < Houses.size(); i++) {
                                if (Integer.valueOf(Houses.get(i).get(8)) == Integer.valueOf(entry.getKey())) {
                                    Rented.put(entry.getValue().get(0), Houses.remove(i));
                                }
                            }
                            Interested.remove(entry.getKey());
                        } else {
                            ACLMessage msg2 = new ACLMessage(ACLMessage.INFORM);
                            for (int i = 0; i < Houses.size(); i++) {
                                if (Integer.valueOf(Houses.get(i).get(8)) == Integer.valueOf(entry.getKey())) {
                                    msg2.addReceiver(new AID(Houses.get(i).get(7), AID.ISLOCALNAME));
                                }
                            }
                            msg2.setContent(String.valueOf(entry.getValue())+"-"+entry.getKey());
                            msg2.setConversationId("4000");
                            send(msg2);
                            doWait();
                        }
                    }
                    flag2 = 0;
                }

            }

        });


    }
}
