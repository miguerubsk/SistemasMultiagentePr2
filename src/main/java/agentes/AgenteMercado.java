/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
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
    private ArrayList<AID> agentesMonitor;
    private ArrayList<ACLMessage> mensajesPendientes;


    @Override
    protected void setup() {
        //Inicialización de las variables del agente
        agentesAgricultor = new ArrayList<>();
        agentesConsola = new ArrayList<>();
        agentesMonitor = new ArrayList<>();
        
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
    
    
    public ACLMessage getMejorPrecio(){
        int precio = 1000000000;
        int index = 0;
        for(int i =0; i<mensajesPendientes.size(); i++){
            if(precio > Integer.parseInt(mensajesPendientes.get(i).getContent())){
                index = i;
                precio = Integer.parseInt(mensajesPendientes.get(i).getContent());
            }
        }
        
        return mensajesPendientes.get(index);
    }
    
    
    //Clases internas que representan las tareas del agente
    
    public class TareaRecepcionMensajes extends CyclicBehaviour {

        @Override
        public void action() {

            MessageTemplate plantilla = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage mensaje = myAgent.receive(plantilla);
            if (mensaje != null) {
                mensajesPendientes.add(mensaje);
                addBehaviour(new TareaComprar());
            } 
            else
                block();
            
        }
    
    }
    
    public class TareaComprar extends OneShotBehaviour{

        @Override
        public void action() {
            ACLMessage index = getMejorPrecio();
            ACLMessage mensaje;
            
            if(mensajesPendientes.remove(0).getContent()=="Cosecha"){
                Fondos -=Integer.parseInt(index.getContent());
                StockCosechas ++;
                mensaje = new ACLMessage(ACLMessage.INFORM);
                mensaje.setSender(myAgent.getAID());
                mensaje.addReceiver(agentesMonitor.get(0));
                mensaje.setContent(Integer.toString(StockCosechas));
            }else if (Integer.parseInt(index.getContent())<Fondos) {
                    mensaje = new ACLMessage(ACLMessage.INFORM);
                    mensaje.setSender(myAgent.getAID());
                    mensaje.addReceiver(mensajesPendientes.get(0).getSender());
                    mensaje.setContent("Confirmar");
                    mensajesPendientes.clear();
                    
                    myAgent.send(mensaje);
            }
        }
            
        }
        
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
            
            ACLMessage mensaje = null;
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
            mensaje.setContent("Comprar");
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
            
            //Busca agentes Monitor
            template = new DFAgentDescription();
            sd = new ServiceDescription();
            sd.setName("Monitor");
            template.addServices(sd);
            
            try {
                result = DFService.search(myAgent, template); 
                if (result.length > 0) {
                    agentesMonitor.clear();
                    for (int i = 0; i < result.length; ++i) {
                        agentesMonitor.set(i, result[i].getName());
                    }
                    
                }
                else {
                    //No se han encontrado agentes operación
                    agentesMonitor = null;
                    
                } 
            }
            catch (FIPAException fe) {
		fe.printStackTrace();
            }
        }
        }
    
      
}
