import jade.core.Agent;
import jade.Boot;

import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class MainAgent extends Agent {

    protected void setup() {

        ContainerController cont = this.getContainerController();
        AgentController agentController;
        try {
            agentController = cont.createNewAgent("Mesitis", "Realtor", null);
            agentController.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < 3; i++) {
            try {
                agentController = cont.createNewAgent("Seller" + i, "Seller", null);
                agentController.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 3; i++) {
            try {
                agentController = cont.createNewAgent("Customer" + i, "Customer", null);
                agentController.start();
            } catch (StaleProxyException e) {
                e.printStackTrace();
            }
        }


    }

}
