/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import GUI.ConsolaJFrame;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.Iterator;
import util.MensajeConsola;

/**
 *
 * @author miguerubsk
 * 
 * 
 */
public class AgenteConsola extends Agent {
    //Variables del agente
    private ArrayList<ConsolaJFrame> gui;
    private ArrayList<MensajeConsola> mensajesPendientes;

    @Override
    protected void setup() {
        //Inicialización de las variables del agente
        gui = new ArrayList();
        mensajesPendientes = new ArrayList();
        //Configuración del GUI
        
        //Registro del agente en las Páginas Amarrillas
        
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("GUI");
        sd.setName("Niño mulsulman que le hace bullying a Deku");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
        
        //Registro de la Ontología
       String[] split = this.getName().split("@");
       System.out.println("Se inicia la ejecución del agente: " + split[0]);
       //Añadir las tareas principales
       addBehaviour(new TareaRecepcionMensajes());
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
       
       //Liberación de recursos, incluido el GUI
        cerrarConsolas();
        System.out.println("Finaliza la ejecución de " + this.getName());
       
       //Despedida
       String[] split = this.getName().split("@");
       System.out.println("Finaliza la ejecución del agente: " + split[0]);
    }
    
    //Métodos de trabajo del agente
    private ConsolaJFrame buscarConsola(String nombreAgente) {
        Iterator<ConsolaJFrame> it = gui.iterator();
        while (it.hasNext()) {
            ConsolaJFrame gui = it.next();
            if (gui.getNombreAgente().compareTo(nombreAgente) == 0)
                return gui;
        }
        
        return null;
    }
    
    private void cerrarConsolas() {
        Iterator<ConsolaJFrame> it = gui.iterator();
        while (it.hasNext()) {
            ConsolaJFrame gui = it.next();
            gui.dispose();
        }
    }
    
    
    //Clases internas que representan las tareas del agente
    public class TareaRecepcionMensajes extends CyclicBehaviour {

        @Override
        public void action() {

            MessageTemplate plantilla = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            ACLMessage mensaje = myAgent.receive(plantilla);
            if (mensaje != null) {

                MensajeConsola mensajeConsola = new MensajeConsola(mensaje.getSender().getName(),
                                    mensaje.getContent());
                mensajesPendientes.add(mensajeConsola);
                addBehaviour(new TareaPresentarMensaje());
            } 
            else
                block();
            
        }
    
    }
    
    public class TareaPresentarMensaje extends OneShotBehaviour {

        @Override
        public void action() {

            MensajeConsola mensajeConsola = mensajesPendientes.remove(0);
            

            ConsolaJFrame gui = buscarConsola(mensajeConsola.getNombreAgente());
            if (gui == null) {
                gui = new ConsolaJFrame(mensajeConsola.getNombreAgente());
                gui.add(gui);
            } 
            
            gui.MostrarTexto(mensajeConsola);
        }
        
    }
      
}
