/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;


/**
 *
 * @author pedroj Esqueleto de agente para la estructura general que deben tener
 * todos los agentes
 */
public class AgenteMonitor extends Agent {

    //Variables del agente
    


    @Override
    protected void setup() {
        //Inicialización de las variables del agente
        
        //Configuración del GUI
        
        //Registro del agente en las Páginas Amarrillas
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("Monitor");
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
    
    
    //Clases internas que representan las tareas del agente
        
      
      
}
