/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import java.util.ArrayList;
import java.util.Random;


/**
 *
 * @author pedroj Esqueleto de agente para la estructura general que deben tener
 * todos los agentes
 */
public class AgenteMercado extends Agent {

    //Variables del agente
    private int Fondos;
    private int StockCosechas;
    Random rand = new Random(System.currentTimeMillis());
    private ArrayList<AID> agentesAgricultor;
    private ArrayList<AID> agentesConsola;
    private ArrayList<String> mensajesPendientes;


    @Override
    protected void setup() {
        //Inicialización de las variables del agente
        agentesAgricultor = new ArrayList<>();
        agentesConsola = new ArrayList<>();
        
        //Configuración del GUI
        
        //Registro del agente en las Páginas Amarrillas
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Mercado");
        sd.setName("Deku");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }

    
        //Registro de la Ontología
        String[] split = this.getName().split("@");

        System.out.println ("Se inicia la ejecución del agente: " + split[0]);
       //Añadir las tareas principales
        RecibirInversion cobrando = new RecibirInversion(this, 3000);
               addBehaviour(cobrando);
        
        SolicitarCompra comprando = new SolicitarCompra(this, 6000);
               addBehaviour(comprando);
    
    }

  

    @Override
        protected void takeDown() {
       //Eliminar registro del agente en las Páginas Amarillas
       try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
       // Close the GUI
       
       //Liberación de recursos, incluido el GUI
       
       //Despedida
       System.out.println("Finaliza la ejecución del agente: " + this.getName());
    }
    
    //Métodos de trabajo del agente
        

    public int getFondos() {
        return Fondos;
    }

    public void setFondos(int Fondos) {
        this.Fondos = Fondos;
    }
    
    
    //Clases internas que representan las tareas del agente
        
      public class RecibirInversion extends TickerBehaviour {

        public RecibirInversion(Agent a, long period) {
            super(a, period);
        }
            @Override
            protected void onTick(){
                setFondos(getFondos()+rand.nextInt(6)+2);
            }
        }
      
      public class SolicitarCompra extends TickerBehaviour {

        public SolicitarCompra(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            
            ACLMessage mensaje;
            if (agentesConsola != null) {
                if (!mensajesPendientes.isEmpty()) {
                    mensaje = new ACLMessage(ACLMessage.INFORM);
                    mensaje.setSender(myAgent.getAID());
                    mensaje.addReceiver(agentesConsola.get(0));
                    mensaje.setContent("Solicitada compra a los agentes agricultor");
            
                    myAgent.send(mensaje);
                }
            }
            
            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.setSender(myAgent.getAID());
            for (int c=0; c<agentesAgricultor.size(); c++){
                msg.addReceiver(agentesAgricultor.get(c));
            }
            send(msg);

        }
          
      }
      
      public class TareaBuscarAgentes extends TickerBehaviour {
        public TareaBuscarAgentes(Agent a, long period) {
            super(a, period);
        }

        @Override
        protected void onTick() {
            DFAgentDescription template;
            ServiceDescription sd;
            DFAgentDescription[] result;
            
            //Busca agentes consola
            template = new DFAgentDescription();
            sd = new ServiceDescription();
            sd.setName("Consola");
            template.addServices(sd);
            
            try {
                result = DFService.search(myAgent, template); 
                if (result.length > 0) {
                    agentesConsola.clear();
                    for (int i = 0; i < result.length; ++i) {
                        agentesConsola.set(i, result[i].getName());
                    }
                }
                else {
                    //No se han encontrado agentes consola
                    agentesConsola = null;
                }
            }
            catch (FIPAException fe) {
		fe.printStackTrace();
            }
            
            //Busca agentes operación
            template = new DFAgentDescription();
            sd = new ServiceDescription();
            sd.setName("Operacion");
            template.addServices(sd);
            
            try {
                result = DFService.search(myAgent, template); 
                if (result.length > 0) {
                    agentesAgricultor.clear();
                    for (int i = 0; i < result.length; ++i) {
                        agentesAgricultor.set(i, result[i].getName());
                    }
                    
                }
                else {
                    //No se han encontrado agentes operación
                    agentesAgricultor = null;
                    
                } 
            }
            catch (FIPAException fe) {
		fe.printStackTrace();
            }
        }
        }
    
      
}
