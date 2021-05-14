import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


import java.util.ArrayList;
import java.util.Random;

import static java.lang.Integer.valueOf;

public class Seller extends Agent {
    private int Rooms, Size, Pet, Floor, Elev, Center, Price;
    private ArrayList<String> Spiti;
    private ArrayList<ArrayList<String>> Spitia;

    protected void setup() {
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                Random rand = new Random();
                int numOfHouses = rand.nextInt(3) + 1;

                Spitia = new ArrayList<>();
                for (int i = 0; i < numOfHouses; i++) {
                    Rooms = rand.nextInt((3 - 1) + 1) + 1;
                    Size = rand.nextInt((100 - 45) + 1) + 45;
                    Pet = rand.nextInt(2);
                    Floor = rand.nextInt(4);
                    Elev = rand.nextInt(2);
                    Center = rand.nextInt(2);
                    Price = rand.nextInt((375 - 300) + 1) + 300; //oi times twn spitwn tha einai apo 200 ews 575

                    Spiti = new ArrayList<>();

                    Spiti.add(String.valueOf(Rooms));
                    Spiti.add(String.valueOf(Size));
                    Spiti.add(String.valueOf(Pet));
                    Spiti.add(String.valueOf(Floor));
                    Spiti.add(String.valueOf(Elev));
                    Spiti.add(String.valueOf(Center));
                    Spiti.add(String.valueOf(Price));
                    Spiti.add(getLocalName());
                    String temp = String.valueOf(i);
                    temp = getLocalName().substring(6) + temp;
                    Spiti.add(temp);
                    Spitia.add(Spiti);
                }

                ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                msg.addReceiver(new AID("Mesitis", AID.ISLOCALNAME));
                msg.setConversationId("1000");
                msg.setContent(Spitia.toString());
                send(msg);


            }
        });

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                int step = 0,repliesCnt=0;
                int HighestRent=0;
                jade.core.AID TempBuyer;
                MessageTemplate mt=null;
                MessageTemplate ms = MessageTemplate.MatchConversationId("4000");
                ACLMessage msg = receive(ms);
                if (msg != null) {
                    String Splited[] = msg.getContent().split("-");

                    String temp = Splited[0].replace("[", "");
                    temp = temp.replace("]", "");
                    String[] tempList = temp.split(",");
                    ArrayList<String> Buyers = new ArrayList<>();
                    for (int i = 0; i < tempList.length; i++) {
                        Buyers.add(tempList[i]);
                    }
                    while (step < 4) {
                        switch (step) {
                            case 0:
                                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                                for (int i = 0; i < Buyers.size(); ++i) {
                                    cfp.addReceiver(new AID(Buyers.get(i), AID.ISLOCALNAME));
                                }
                                cfp.setConversationId("negotiation");
                                cfp.setReplyWith("cfp"); // Unique value
                                send(cfp);
                                // Prepare the template to get proposals
                                 mt = MessageTemplate.and(MessageTemplate.MatchConversationId("negotiation"),
                                        MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                                step = 1;
                                break;
                            case 1:
                                ACLMessage reply = myAgent.receive(mt);
                                if (reply != null) {
                                    // Reply received
                                    if (reply.getPerformative() == ACLMessage.PROPOSE) {
                                        // This is an offer
                                        int price = Integer.parseInt(reply.getContent());
                                        if ( price > HighestRent) {
                                            // This is the best offer at present
                                            HighestRent = price;
                                            TempBuyer = reply.getSender();
                                        }
                                    }
                                    repliesCnt++;
                                    if (repliesCnt >= Buyers.size()) {
                                        // We received all replies
                                        step = 2;
                                    }
                                }
                                break;

                        }
                        //https://www.programcreek.com/java-api-examples/?class=jade.lang.acl.ACLMessage&method=CFP
                    }
                }

            }
        });


    }
}
