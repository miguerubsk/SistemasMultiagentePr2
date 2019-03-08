/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

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
import util.MensajeConsola;


/**
 *
 * @author pedroj Esqueleto de agente para la estructura general que deben tener
 * todos los agentes
 */
public class AgenteAgricultor extends Agent {

    //Variables del agente
    private int cosecha;
    Random rand = new Random(System.currentTimeMillis());
    private ArrayList<ACLMessage> mensajesPendientes;


    @Override
    protected void setup() {
        //Inicialización de las variables del agente
        cosecha = 0;
        
        //Configuración del GUI
        
        //Registro del agente en las Páginas Amarrillas
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Agricultor");
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
       Cosechar cosechando = new Cosechar(this, 5000);
       addBehaviour(cosechando);
    
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

    public int getCosecha() {
        return cosecha;
    }

    public void setCosecha(int cosecha) {
        this.cosecha = cosecha;
    }
    
    public int getPrecioCosecha(){
        return rand.nextInt(5)+5;
    }
    
    
    //Clases internas que representan las tareas del agente
    
     public class TareaRecepcionMensajes extends CyclicBehaviour {

        @Override
        public void action() {

            MessageTemplate plantilla = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage mensaje = myAgent.receive(plantilla);
            if (mensaje != null) {
                mensajesPendientes.add(mensaje);
                if(cosecha > 0)
                addBehaviour(new TareaPresentarPrecio());
            } 
            else
                block();
            
        }
    
    }
     
     
        public class TareaPresentarPrecio extends OneShotBehaviour{

        @Override
        public void action() {
            
            ACLMessage mensaje;
            if (mensajesPendientes.get(0).getContent() == "Comprar") {
                    mensaje = new ACLMessage(ACLMessage.INFORM);
                    mensaje.setSender(myAgent.getAID());
                    mensaje.addReceiver(mensajesPendientes.get(0).getSender());
                    int precio = getPrecioCosecha();
                    mensaje.setContent(Integer.toString(precio));
                    myAgent.send(mensaje);
            }else if(mensajesPendientes.get(0).getContent() == "Confirmar" && cosecha > 0){
                mensaje = new ACLMessage(ACLMessage.INFORM);
                    mensaje.setSender(myAgent.getAID());
                    mensaje.addReceiver(mensajesPendientes.get(0).getSender());
                    mensaje.setContent("Cosecha");
                    myAgent.send(mensaje);
            }
        }
            
        }
    
    
        public class Cosechar extends TickerBehaviour {

        public Cosechar(Agent a, long period) {
            super(a, period);
        }
            @Override
            protected void onTick(){
                setCosecha(getCosecha()+1);
            }
        }
        
      
      
}
