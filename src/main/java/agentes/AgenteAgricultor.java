/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import java.util.ArrayList;
import java.util.Random;


/**
 *
 * @author pedroj Esqueleto de agente para la estructura general que deben tener
 * todos los agentes
 */
public class AgenteAgricultor extends Agent {

    //Variables del agente
    private int cosecha;
    Random rand = new Random(System.currentTimeMillis());
    private ArrayList<String> mensajesPendientes;


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
